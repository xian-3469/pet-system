package com.ly.pet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.pet.dto.GlmMessage;
import com.ly.pet.dto.GlmChatResult;
import com.ly.pet.entity.HealthAdvice;
import com.ly.pet.entity.HealthRecord;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.service.IHealthAdviceService;
import com.ly.pet.service.IHealthRecordService;
import com.ly.pet.service.IPetProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AI 健康建议生成服务
 * 基于宠物档案与健康记录，由大模型生成个性化健康建议并落库
 * LLM 调用或解析失败时自动降级为原有规则引擎
 */
@Service
public class HealthAdviceAiService {

    private static final Logger log = LoggerFactory.getLogger(HealthAdviceAiService.class);

    /** 建议类型合法值 */
    private static final Set<String> ADVICE_TYPES = new java.util.HashSet<>(Arrays.asList(
            "DIET", "EXERCISE", "VACCINE", "CHECKUP", "DENTAL", "GROOMING", "OTHER"));

    /** 优先级合法值 */
    private static final Set<String> PRIORITIES = new java.util.HashSet<>(Arrays.asList(
            "LOW", "MEDIUM", "HIGH"));

    @Resource
    private GlmClient glmClient;

    @Resource
    private IPetProfileService petProfileService;

    @Resource
    private IHealthRecordService healthRecordService;

    @Resource
    private IHealthAdviceService healthAdviceService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 为指定宠物生成 AI 健康建议
     *
     * @param petId 宠物档案ID
     * @return 生成结果：source=AI 表示大模型生成，RULE 表示降级为规则引擎
     */
    public Map<String, Object> generateForPet(Integer petId) {
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new com.ly.pet.exception.ServiceException(com.ly.pet.common.Constants.CODE_400, "宠物不存在");
        }

        List<HealthRecord> records = healthRecordService.list(
                new QueryWrapper<HealthRecord>().eq("pet_id", petId).orderByDesc("record_date").last("LIMIT 15"));

        int count;
        String source;
        try {
            List<HealthAdvice> advices = generateByLlm(pet, records);
            source = "AI";
            // 覆盖旧建议
            healthAdviceService.remove(new QueryWrapper<HealthAdvice>().eq("pet_id", petId));
            LocalDateTime now = LocalDateTime.now();
            for (HealthAdvice advice : advices) {
                advice.setPetId(petId);
                advice.setPetName(pet.getPetName());
                advice.setBreed(pet.getBreed());
                advice.setAge(pet.getAge());
                advice.setSource(source);
                advice.setIsRead(0);
                advice.setIsHandled(0);
                advice.setCreateTime(now);
                healthAdviceService.save(advice);
            }
            count = advices.size();
        } catch (Exception e) {
            log.warn("AI 健康建议生成失败，降级为规则引擎。petId={}", petId, e);
            source = "RULE";
            // 规则引擎内部会先删除旧建议再落库
            healthAdviceService.generateAdvice(petId);
            List<HealthAdvice> saved = healthAdviceService.list(
                    new QueryWrapper<HealthAdvice>().eq("pet_id", petId));
            for (HealthAdvice advice : saved) {
                advice.setSource(source);
                healthAdviceService.updateById(advice);
            }
            count = saved.size();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("source", source);
        result.put("count", count);
        return result;
    }

    /**
     * 调用大模型生成建议，要求返回 JSON 数组
     */
    private List<HealthAdvice> generateByLlm(PetProfile pet, List<HealthRecord> records) throws Exception {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请为以下宠物生成个性化健康建议。\n\n")
              .append("【宠物信息】\n")
              .append("名字：").append(pet.getPetName()).append("\n")
              .append("品种：").append(pet.getBreed()).append("\n")
              .append("大类：").append(pet.getPetType()).append("\n")
              .append("年龄：").append(pet.getAge() == null ? "未知" : pet.getAge() + " 个月").append("\n")
              .append("性别：").append(pet.getGender()).append("\n")
              .append("体重：").append(pet.getWeight() == null ? "未知" : pet.getWeight() + " kg").append("\n")
              .append("绝育：").append(pet.getNeutered() != null && pet.getNeutered() == 1 ? "已绝育" : "未绝育").append("\n")
              .append("简介：").append(pet.getIntro() == null || pet.getIntro().isEmpty() ? "无" : pet.getIntro()).append("\n");
        if (records != null && !records.isEmpty()) {
            prompt.append("\n【近期健康记录】\n");
            for (HealthRecord r : records) {
                prompt.append("- ").append(r.getRecordType()).append(" / ").append(r.getItemName())
                      .append("，记录日期 ").append(r.getRecordDate() == null ? "未知" : r.getRecordDate())
                      .append(r.getNextDate() == null ? "" : "，下次提醒 " + r.getNextDate())
                      .append("\n");
            }
        } else {
            prompt.append("\n【近期健康记录】无任何记录（请特别提醒建立基础免疫和体检计划）\n");
        }
        prompt.append("\n请生成 5-8 条针对性的健康建议，覆盖疫苗、驱虫、体检、饮食、运动、美容、牙齿护理等维度中与该宠物最相关的几项。\n")
              .append("只返回 JSON 数组，不要任何其他文字或 markdown 代码块标记，格式：\n")
              .append("[{\"adviceType\":\"DIET|EXERCISE|VACCINE|CHECKUP|DENTAL|GROOMING|OTHER 之一\",\"title\":\"简短标题\",\"content\":\"具体建议内容，100字以内，结合该宠物的品种、年龄、体重和健康记录\",\"priority\":\"HIGH|MEDIUM|LOW 之一\"}]");

        List<GlmMessage> messages = new ArrayList<>();
        messages.add(GlmMessage.system("你是一位专业的宠物健康管理顾问，熟悉猫狗等常见宠物的品种特点、不同生命阶段的护理要点。回答必须是纯 JSON。"));
        messages.add(GlmMessage.user(prompt.toString()));

        GlmChatResult result = glmClient.chat(messages);
        String content = result.getContent();
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalStateException("LLM 返回为空");
        }
        return parseAdviceList(content);
    }

    /**
     * 解析 LLM 返回的建议 JSON，做严格校验与清洗
     */
    private List<HealthAdvice> parseAdviceList(String content) throws Exception {
        JsonNode arrayNode = GlmClient.extractJsonArray(objectMapper, content);
        if (!arrayNode.isArray() || arrayNode.size() == 0) {
            throw new IllegalStateException("LLM 返回了空建议列表");
        }
        List<HealthAdvice> advices = new ArrayList<>();
        for (JsonNode node : arrayNode) {
            String adviceType = node.path("adviceType").asText("OTHER").toUpperCase();
            String priority = node.path("priority").asText("MEDIUM").toUpperCase();
            String title = node.path("title").asText(null);
            String adviceContent = node.path("content").asText(null);
            if (title == null || adviceContent == null || title.trim().isEmpty() || adviceContent.trim().isEmpty()) {
                continue;
            }
            if (!ADVICE_TYPES.contains(adviceType)) {
                adviceType = "OTHER";
            }
            if (!PRIORITIES.contains(priority)) {
                priority = "MEDIUM";
            }
            if (adviceContent.length() > 500) {
                adviceContent = adviceContent.substring(0, 500);
            }
            HealthAdvice advice = new HealthAdvice();
            advice.setAdviceType(adviceType);
            advice.setTitle(title.trim());
            advice.setContent(adviceContent.trim());
            advice.setPriority(priority);
            advices.add(advice);
        }
        if (advices.isEmpty()) {
            throw new IllegalStateException("LLM 返回的建议全部无效");
        }
        return advices;
    }
}
