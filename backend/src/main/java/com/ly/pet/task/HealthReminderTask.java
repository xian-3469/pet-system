package com.ly.pet.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.pet.config.AiConfig;
import com.ly.pet.dto.GlmMessage;
import com.ly.pet.entity.HealthAdvice;
import com.ly.pet.entity.HealthRecord;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.service.GlmClient;
import com.ly.pet.service.IHealthAdviceService;
import com.ly.pet.service.IHealthRecordService;
import com.ly.pet.service.IPetProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 主动健康提醒 Agent
 * 每天定时检查 3 天内即将到期的健康记录，为每条到期记录生成提醒写入站内信：
 * - AI 可用时：按宠物品种/月龄/记录详情由大模型生成个性化提醒文案
 * - AI 不可用或生成失败：自动降级为模板文案
 * 标题保持稳定格式以复用既有去重机制，避免同一事项重复推送
 */
@Component
public class HealthReminderTask {

    private static final Logger log = LoggerFactory.getLogger(HealthReminderTask.class);

    /** 单轮最多生成的个性化提醒条数，控制模型调用量 */
    private static final int MAX_AI_REMINDERS_PER_RUN = 10;

    @Resource
    private IHealthRecordService healthRecordService;
    @Resource
    private IPetProfileService petProfileService;
    @Resource
    private IHealthAdviceService healthAdviceService;
    @Resource
    private GlmClient glmClient;
    @Resource
    private AiConfig aiConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 定时健康提醒（cron 可在 application.yml 的 health.reminder.cron 配置，默认每天凌晨1点）
     */
    @Scheduled(cron = "${health.reminder.cron:0 0 1 * * ?}")
    public void checkHealthReminders() {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 查询next_date在3天内的健康记录（疫苗、驱虫、体检）
        QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("record_type", "疫苗");
        queryWrapper.or(wrapper -> wrapper.eq("record_type", "驱虫").or().eq("record_type", "体检"));
        queryWrapper.between("next_date", today, threeDaysLater);
        List<HealthRecord> dueList = healthRecordService.list(queryWrapper);

        boolean aiEnabled = aiConfig.isConfigured();
        int aiUsed = 0;
        int created = 0;
        for (HealthRecord record : dueList) {
            // 检查是否已经生成过健康建议（避免重复，标题格式保持稳定）
            String title = "健康提醒：宠物「" + getPetName(record.getPetId()) + "」" + record.getRecordType() + "即将到期";
            QueryWrapper<HealthAdvice> adviceQuery = new QueryWrapper<>();
            adviceQuery.eq("pet_id", record.getPetId());
            adviceQuery.eq("title", title);
            if (healthAdviceService.count(adviceQuery) > 0) {
                continue; // 已生成过，跳过
            }

            PetProfile pet = petProfileService.getById(record.getPetId());
            if (pet == null) continue;

            // 模板文案（兜底基线）
            String content = String.format("您的宠物「%s」有一项健康记录即将到期：\n" +
                            "类型：%s\n" +
                            "项目：%s\n" +
                            "上次时间：%s\n" +
                            "下次时间：%s\n" +
                            "请及时安排！",
                    pet.getPetName(),
                    record.getRecordType(),
                    record.getItemName(),
                    record.getRecordDate() == null ? "未知" : record.getRecordDate().format(formatter),
                    record.getNextDate() == null ? "未知" : record.getNextDate().format(formatter));

            // AI 个性化文案（限量 + 失败降级）
            if (aiEnabled && aiUsed < MAX_AI_REMINDERS_PER_RUN) {
                try {
                    String aiContent = generateAiReminderContent(pet, record, formatter);
                    if (aiContent != null && !aiContent.trim().isEmpty()) {
                        content = aiContent;
                        aiUsed++;
                    }
                } catch (Exception e) {
                    log.warn("个性化提醒生成失败，降级为模板文案。recordId={}", record.getId(), e);
                }
            }

            HealthAdvice advice = new HealthAdvice();
            advice.setPetId(pet.getId());
            advice.setPetName(pet.getPetName());
            advice.setBreed(pet.getBreed());
            advice.setAge(pet.getAge());
            advice.setAdviceType(convertToAdviceType(record.getRecordType()));
            advice.setTitle(title);
            advice.setContent(content);
            advice.setPriority("MEDIUM");
            advice.setIsRead(0);
            advice.setIsHandled(0);
            advice.setCreateTime(java.time.LocalDateTime.now());
            healthAdviceService.save(advice);
            created++;
        }
        log.info("[主动提醒Agent] 到期记录 {} 条，新增提醒 {} 条（其中 AI 个性化文案 {} 条）", dueList.size(), created, aiUsed);
    }

    /**
     * 调用大模型生成个性化提醒正文（异常由调用方捕获后降级为模板文案）
     */
    private String generateAiReminderContent(PetProfile pet, HealthRecord record, DateTimeFormatter formatter) throws Exception {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请以宠物服务平台的口吻，为以下即将到期的健康事项写一条个性化提醒消息：\n")
              .append("宠物：").append(pet.getPetName())
              .append("（品种：").append(pet.getBreed() == null ? "未知" : pet.getBreed())
              .append("，月龄：").append(pet.getAge() == null ? "未知" : pet.getAge()).append("）\n")
              .append("事项类型：").append(record.getRecordType()).append("\n")
              .append("项目：").append(record.getItemName()).append("\n")
              .append("上次日期：").append(record.getRecordDate() == null ? "未知" : record.getRecordDate().format(formatter)).append("\n")
              .append("到期日期：").append(record.getNextDate() == null ? "未知" : record.getNextDate().format(formatter)).append("\n\n")
              .append("要求：\n")
              .append("1. 结合宠物品种与月龄给出针对性提醒（如幼宠疫苗程序、老年宠物体检重点）\n")
              .append("2. 语气亲切自然，80-150字，结尾给出1-2条实用小贴士\n")
              .append("3. 只返回 JSON 对象，格式：{\"content\":\"提醒正文\"}");
        List<GlmMessage> messages = new ArrayList<>();
        messages.add(GlmMessage.system("你是宠物服务平台的健康提醒助手，回答必须是纯 JSON。"));
        messages.add(GlmMessage.user(prompt.toString()));
        String resp = glmClient.chat(messages).getContent();
        if (resp == null || resp.trim().isEmpty()) {
            return null;
        }
        String json = resp.trim().replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
        int start = json.indexOf('{');
        int end = json.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return null;
        }
        JsonNode node = objectMapper.readTree(json.substring(start, end + 1));
        String content = node.path("content").asText(null);
        return content == null || content.trim().isEmpty() ? null : content.trim();
    }

    /**
     * 根据记录类型转换为建议类型
     */
    private String convertToAdviceType(String recordType) {
        switch (recordType) {
            case "疫苗":
                return "VACCINE";
            case "驱虫":
                return "HEALTH";
            case "体检":
                return "CHECKUP";
            default:
                return "OTHER";
        }
    }

    /**
     * 获取宠物名称（用于避免重复检查时的查询）
     */
    private String getPetName(Integer petId) {
        PetProfile pet = petProfileService.getById(petId);
        return pet != null ? pet.getPetName() : "未知";
    }
}
