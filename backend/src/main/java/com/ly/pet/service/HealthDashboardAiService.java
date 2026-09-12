package com.ly.pet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.pet.common.Constants;
import com.ly.pet.dto.GlmMessage;
import com.ly.pet.dto.GlmChatResult;
import com.ly.pet.entity.HealthRecord;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.service.IHealthRecordService;
import com.ly.pet.service.IPetProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康看板 AI 解读服务
 * 聚合体重序列/记录覆盖/到期事项，规则检测异常，由大模型生成健康解读卡片
 * LLM 不可用或解析失败时降级为规则摘要
 */
@Service
public class HealthDashboardAiService {

    private static final Logger log = LoggerFactory.getLogger(HealthDashboardAiService.class);

    @Resource
    private GlmClient glmClient;

    @Resource
    private IHealthRecordService healthRecordService;

    @Resource
    private IPetProfileService petProfileService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 生成健康解读卡片
     */
    public Map<String, Object> generate(Integer petId, com.ly.pet.entity.User user) {
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException(Constants.CODE_400, "宠物不存在");
        }
        boolean isAdmin = user.getRole() != null && !user.getRole().equals("ROLE_USER");
        if (!isAdmin && !user.getId().equals(pet.getOwnerId())) {
            throw new ServiceException(Constants.CODE_401, "只能查看自己宠物的健康解读");
        }

        // ===== 数据聚合 =====
        List<HealthRecord> records = healthRecordService.list(
                new QueryWrapper<HealthRecord>().eq("pet_id", petId).orderByAsc("record_date"));

        List<Map<String, Object>> weightSeries = new ArrayList<>();
        for (HealthRecord r : records) {
            if (r.getWeight() != null && r.getRecordDate() != null) {
                Map<String, Object> p = new LinkedHashMap<>();
                p.put("date", r.getRecordDate().toString());
                p.put("weight", r.getWeight());
                weightSeries.add(p);
            }
        }

        // 规则检测：异常与覆盖
        List<String> ruleWarnings = new ArrayList<>();
        Map<String, Object> weightAlert = detectWeightAlert(weightSeries);
        if (weightAlert != null) {
            ruleWarnings.add(String.valueOf(weightAlert.get("message")));
        }
        LocalDate today = LocalDate.now();
        for (String type : new String[]{"疫苗", "驱虫", "体检"}) {
            HealthRecord last = records.stream()
                    .filter(r -> type.equals(r.getRecordType()))
                    .reduce((a, b) -> b).orElse(null);
            if (last == null || last.getRecordDate() == null) {
                ruleWarnings.add("从未记录过" + type + "，建议尽快补全基础健康计划");
            } else {
                long days = ChronoUnit.DAYS.between(last.getRecordDate(), today);
                if ("疫苗".equals(type) && days > 365) {
                    ruleWarnings.add("疫苗已超过 " + days / 30 + " 个月未记录，可能需要加强");
                }
                if ("体检".equals(type) && days > 365) {
                    ruleWarnings.add("已超过 1 年没有体检记录");
                }
            }
        }
        for (HealthRecord r : records) {
            if (r.getNextDate() == null) continue;
            long days = ChronoUnit.DAYS.between(r.getNextDate(), today);
            if (days > 0) {
                ruleWarnings.add("「" + r.getItemName() + "」复诊已逾期 " + days + " 天（建议日期 " + r.getNextDate() + "）");
            } else if (days >= -7) {
                ruleWarnings.add("「" + r.getItemName() + "」将于 " + Math.abs(days) + " 天后到期（" + r.getNextDate() + "）");
            }
        }

        // 记录覆盖概览
        Map<String, Object> coverage = new LinkedHashMap<>();
        for (String type : new String[]{"疫苗", "驱虫", "体检", "治疗"}) {
            long n = records.stream().filter(r -> type.equals(r.getRecordType())).count();
            coverage.put(type, n);
        }

        // ===== LLM 解读（失败降级规则摘要）=====
        String source = "AI";
        Map<String, Object> card;
        try {
            card = generateByLlm(pet, weightSeries, coverage, ruleWarnings);
        } catch (Exception e) {
            log.warn("健康解读 LLM 生成失败，降级为规则摘要。petId={}", petId, e);
            card = ruleFallback(pet, coverage, ruleWarnings);
            source = "RULE";
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("petName", pet.getPetName());
        result.put("source", source);
        result.put("overall", card.get("overall"));
        result.put("warnings", card.get("warnings"));
        result.put("tips", card.get("tips"));
        result.put("weightAlert", weightAlert);
        result.put("coverage", coverage);
        return result;
    }

    /**
     * 体重趋势规则检测：最近两次称重变化率超过 8% 触发预警
     */
    private Map<String, Object> detectWeightAlert(List<Map<String, Object>> weightSeries) {
        if (weightSeries.size() < 2) {
            return null;
        }
        BigDecimal prev = new BigDecimal(String.valueOf(weightSeries.get(weightSeries.size() - 2).get("weight")));
        BigDecimal last = new BigDecimal(String.valueOf(weightSeries.get(weightSeries.size() - 1).get("weight")));
        if (prev.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        double pct = last.subtract(prev).divide(prev, 4, RoundingMode.HALF_UP).doubleValue();
        if (Math.abs(pct) < 0.08) {
            return null;
        }
        Map<String, Object> alert = new LinkedHashMap<>();
        alert.put("from", prev.toPlainString());
        alert.put("to", last.toPlainString());
        alert.put("changePct", Math.round(pct * 1000) / 10.0);
        alert.put("direction", pct > 0 ? "上升" : "下降");
        alert.put("message", "体重短期" + (pct > 0 ? "上升" : "下降") + "约 " + Math.round(Math.abs(pct) * 1000) / 10.0
                + "%（" + prev.toPlainString() + "kg → " + last.toPlainString() + "kg），建议关注饮食与运动");
        return alert;
    }

    private Map<String, Object> generateByLlm(PetProfile pet, List<Map<String, Object>> weightSeries,
                                              Map<String, Object> coverage, List<String> ruleWarnings) throws Exception {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请为以下宠物的健康看板生成一份简明解读。\n\n")
              .append("【宠物】").append(pet.getPetName())
              .append("（品种：").append(pet.getBreed() == null ? "未知" : pet.getBreed())
              .append("，月龄：").append(pet.getAge() == null ? "未知" : pet.getAge())
              .append("，当前体重：").append(pet.getWeight() == null ? "未知" : pet.getWeight() + "kg").append("）\n")
              .append("【记录覆盖】疫苗 ").append(coverage.get("疫苗")).append(" 次、驱虫 ")
              .append(coverage.get("驱虫")).append(" 次、体检 ").append(coverage.get("体检"))
              .append(" 次、治疗 ").append(coverage.get("治疗")).append(" 次\n");
        if (!weightSeries.isEmpty()) {
            prompt.append("【体重序列】");
            for (Map<String, Object> p : weightSeries) {
                prompt.append(p.get("date")).append("=").append(p.get("weight")).append("kg  ");
            }
            prompt.append("\n");
        }
        if (!ruleWarnings.isEmpty()) {
            prompt.append("【规则检测到的异常（解读时必须涵盖）】\n");
            for (String w : ruleWarnings) {
                prompt.append("- ").append(w).append("\n");
            }
        }
        prompt.append("\n只返回 JSON 对象，不要 markdown 代码块标记，格式：\n")
              .append("{\"overall\":\"60-120字的总体健康评估，结合月龄阶段与记录覆盖情况\",\"warnings\":[\"具体预警1\",\"具体预警2\"],\"tips\":[\"实用建议1\",\"实用建议2\",\"实用建议3\"]}")
              .append("\nwarnings 需涵盖上述规则异常并解释影响；tips 给出可执行的具体建议；语言亲切专业。");

        List<GlmMessage> messages = new ArrayList<>();
        messages.add(GlmMessage.system("你是一位专业的宠物健康管理顾问，回答必须是纯 JSON。"));
        messages.add(GlmMessage.user(prompt.toString()));

        GlmChatResult result = glmClient.chat(messages);
        String content = result.getContent();
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalStateException("LLM 返回为空");
        }
        JsonNode node = GlmClient.extractJsonObject(objectMapper, content);
        Map<String, Object> card = new LinkedHashMap<>();
        card.put("overall", node.path("overall").asText(""));
        card.put("warnings", toStringList(node.path("warnings")));
        card.put("tips", toStringList(node.path("tips")));
        if (card.get("overall").toString().trim().isEmpty()) {
            throw new IllegalStateException("LLM 返回的 overall 为空");
        }
        return card;
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

    /**
     * 降级：纯规则摘要卡片
     */
    private Map<String, Object> ruleFallback(PetProfile pet, Map<String, Object> coverage, List<String> ruleWarnings) {
        Map<String, Object> card = new LinkedHashMap<>();
        StringBuilder overall = new StringBuilder();
        overall.append(pet.getPetName());
        if (pet.getAge() != null) {
            overall.append("（").append(pet.getAge() / 12).append("岁").append(pet.getAge() % 12).append("个月）");
        }
        overall.append("共有疫苗 ").append(coverage.get("疫苗")).append(" 次、驱虫 ").append(coverage.get("驱虫"))
               .append(" 次、体检 ").append(coverage.get("体检")).append(" 次、治疗 ").append(coverage.get("治疗")).append(" 次记录。");
        if (ruleWarnings.isEmpty()) {
            overall.append("暂未检测到明显异常，请保持当前的养护节奏。");
        } else {
            overall.append("检测到 ").append(ruleWarnings.size()).append(" 项需要关注的事项，详见下方预警。");
        }
        card.put("overall", overall.toString());
        card.put("warnings", ruleWarnings);
        List<String> tips = new ArrayList<>();
        tips.add("保持每年至少一次全面体检，幼宠与老年宠适当加密");
        tips.add("按时完成疫苗加强和定期驱虫");
        tips.add("日常留意体重变化，波动明显时及时调整饮食");
        card.put("tips", tips);
        return card;
    }
}
