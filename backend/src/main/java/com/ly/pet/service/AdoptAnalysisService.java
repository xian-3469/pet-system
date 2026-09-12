package com.ly.pet.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.pet.dto.GlmChatResult;
import com.ly.pet.dto.GlmMessage;
import com.ly.pet.entity.Animal;
import com.ly.pet.entity.Applcation;
import com.ly.pet.service.IAnimalService;
import com.ly.pet.service.IApplcationService;
import com.ly.pet.service.impl.AdoptMatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 领养匹配 AI 分析服务
 * 规则算法打分 + 大模型对申请理由与动物性格的个性化分析
 */
@Service
public class AdoptAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AdoptAnalysisService.class);

    @Resource
    private IApplcationService applcationService;

    @Resource
    private IAnimalService animalService;

    @Resource
    private AdoptMatchService adoptMatchService;

    @Resource
    private GlmClient glmClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析一条领养申请与目标动物的匹配度
     *
     * @param applicationId 领养申请ID
     * @return matchScore/matchLevel（规则算法）+ highlights/risks/suggestions/conclusion（大模型分析）
     */
    public Map<String, Object> analyze(Integer applicationId) {
        Applcation application = applcationService.getById(applicationId);
        if (application == null) {
            throw new com.ly.pet.exception.ServiceException(com.ly.pet.common.Constants.CODE_400, "领养申请不存在");
        }
        Animal animal = application.getAnimalId() == null ? null : animalService.getById(application.getAnimalId());
        if (animal == null) {
            throw new com.ly.pet.exception.ServiceException(com.ly.pet.common.Constants.CODE_400, "申请对应的动物不存在");
        }

        // 1. 规则算法打分
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("experience", application.getExperience());
        userProfile.put("housing", application.getHousing());
        userProfile.put("income", application.getIncome());
        userProfile.put("familyStructure", application.getFamilyStructure());
        int matchScore = adoptMatchService.calculateMatchScore(animal, userProfile);
        String matchLevel = adoptMatchService.getMatchLevel(matchScore);

        // 2. 大模型个性化分析
        Map<String, Object> analysis;
        try {
            analysis = analyzeByLlm(application, animal, matchScore, matchLevel);
        } catch (Exception e) {
            log.warn("领养 AI 分析失败，返回规则算法结果。applicationId={}", applicationId, e);
            analysis = new LinkedHashMap<>();
            analysis.put("conclusion", "AI 深度分析暂时不可用，以下为算法匹配结果：" + matchLevel + "（" + matchScore + " 分）。");
            List<String> fallback = new ArrayList<>();
            fallback.add("申请画像：养宠经验「" + safe(application.getExperience()) + "」，住房「" + safe(application.getHousing())
                    + "」，家庭结构「" + safe(application.getFamilyStructure()) + "」，收入「" + safe(application.getIncome()) + "」。");
            analysis.put("highlights", fallback);
            analysis.put("risks", new ArrayList<>());
            analysis.put("suggestions", new ArrayList<>());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("matchScore", matchScore);
        result.put("matchLevel", matchLevel);
        result.putAll(analysis);
        return result;
    }

    private Map<String, Object> analyzeByLlm(Applcation application, Animal animal, int matchScore, String matchLevel) throws Exception {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请分析以下领养申请人与目标流浪动物的匹配情况，辅助平台审核与申请人改善领养条件。\n\n")
              .append("【目标动物】\n")
              .append("名字：").append(safe(animal.getNickname())).append("\n")
              .append("品种：").append(safe(animal.getType())).append("\n")
              .append("年龄：").append(safe(animal.getAge())).append("\n")
              .append("体型：").append(safe(animal.getBodyType())).append("\n")
              .append("性格：").append(safe(animal.getPersonality())).append("\n")
              .append("活动范围：").append(safe(animal.getAddress())).append("\n")
              .append("描述：").append(safe(animal.getInformation())).append("\n")
              .append("\n【申请人画像】\n")
              .append("养宠经验：").append(safe(application.getExperience())).append("\n")
              .append("住房条件：").append(safe(application.getHousing())).append("\n")
              .append("家庭结构：").append(safe(application.getFamilyStructure())).append("\n")
              .append("婚姻状况：").append(safe(application.getMarried())).append("\n")
              .append("收入：").append(safe(application.getIncome())).append("\n")
              .append("职业：").append(safe(application.getProfession())).append("\n")
              .append("居住地址：").append(safe(application.getAddress())).append("\n")
              .append("领养理由：").append(safe(application.getReason())).append("\n")
              .append("\n【规则算法结果】").append(matchLevel).append("（").append(matchScore).append(" 分/100）\n")
              .append("\n请重点关注领养理由中的诚意与准备程度，结合动物性格与申请人条件给出分析。\n")
              .append("只返回 JSON 对象，不要任何其他文字或 markdown 代码块标记，格式：\n")
              .append("{\"highlights\":[\"匹配亮点1\",\"匹配亮点2\"],\"risks\":[\"潜在风险1\",\"潜在风险2\"],\"suggestions\":[\"改善建议1\",\"改善建议2\"],\"conclusion\":\"一段40-80字的整体结论\"}")
              .append("\n每个数组1-3条，具体、贴合本次申请内容，不要泛泛而谈。");

        List<GlmMessage> messages = new ArrayList<>();
        messages.add(GlmMessage.system("你是一位专业的流浪动物领养评估顾问，擅长根据申请人画像与动物情况评估领养匹配度，回答必须是纯 JSON。"));
        messages.add(GlmMessage.user(prompt.toString()));

        GlmChatResult result = glmClient.chat(messages);
        String content = result.getContent();
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalStateException("LLM 返回为空");
        }
        JsonNode node = GlmClient.extractJsonObject(objectMapper, content);

        Map<String, Object> analysis = new LinkedHashMap<>();
        analysis.put("highlights", toStringList(node.path("highlights")));
        analysis.put("risks", toStringList(node.path("risks")));
        analysis.put("suggestions", toStringList(node.path("suggestions")));
        String conclusion = node.path("conclusion").asText(null);
        analysis.put("conclusion", conclusion == null || conclusion.trim().isEmpty()
                ? "算法匹配等级：" + matchLevel + "（" + matchScore + " 分）。"
                : conclusion.trim());
        return analysis;
    }

    private List<String> toStringList(JsonNode arrayNode) {
        List<String> list = new ArrayList<>();
        if (arrayNode.isArray()) {
            for (JsonNode item : arrayNode) {
                String text = item.asText();
                if (text != null && !text.trim().isEmpty()) {
                    list.add(text.trim());
                }
            }
        }
        return list;
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "未填写" : value;
    }
}
