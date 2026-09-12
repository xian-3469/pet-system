package com.ly.pet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.pet.config.AiConfig;
import com.ly.pet.dto.GlmChatResult;
import com.ly.pet.dto.GlmMessage;
import com.ly.pet.dto.GlmTool;
import com.ly.pet.dto.GlmToolCall;
import com.ly.pet.entity.User;
import com.ly.pet.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Agent 对话服务
 * 主循环：system prompt -> LLM -> 若发起工具调用则执行并回填 -> 循环直至产出最终答复
 */
@Service
public class AgentChatService {

    private static final Logger log = LoggerFactory.getLogger(AgentChatService.class);

    @Resource
    private GlmClient glmClient;

    @Resource
    private AgentToolRegistry toolRegistry;

    @Resource
    private AiConfig aiConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 前端展示用的一条工具调用轨迹 */
    public static class ToolTrace {
        private final String tool;
        private final String brief;

        public ToolTrace(String tool, String brief) {
            this.tool = tool;
            this.brief = brief;
        }

        public String getTool() {
            return tool;
        }

        public String getBrief() {
            return brief;
        }
    }

    /**
     * 执行一轮完整的 Agent 对话
     *
     * @param history      前端传入的历史消息（user/assistant）
     * @param userMessage  本轮用户输入
     * @return reply 最终回复；trace 本次使用的工具轨迹
     */
    public Map<String, Object> chat(List<Map<String, String>> history, String userMessage) {
        User user = TokenUtils.getCurrentUser();

        List<GlmMessage> messages = new ArrayList<>();
        messages.add(GlmMessage.system(buildSystemPrompt(user)));
        if (history != null && !history.isEmpty()) {
            // 按时间正序取最近 12 条历史，保证对话顺序正确
            int from = Math.max(0, history.size() - 12);
            int historyCount = 0;
            for (Map<String, String> item : history.subList(from, history.size())) {
                String role = item.get("role");
                String content = item.get("content");
                if (content == null || content.trim().isEmpty()) {
                    continue;
                }
                if ("user".equals(role)) {
                    messages.add(GlmMessage.user(content));
                    historyCount++;
                } else if ("assistant".equals(role)) {
                    messages.add(GlmMessage.assistant(content));
                    historyCount++;
                }
            }
        }
        messages.add(GlmMessage.user(userMessage));

        List<GlmTool> tools = toolRegistry.listTools();
        List<ToolTrace> trace = new ArrayList<>();

        GlmChatResult result = null;
        for (int round = 0; round <= aiConfig.getMaxToolRounds(); round++) {
            result = glmClient.chat(messages, tools);
            if (!result.hasToolCalls()) {
                log.info("[Agent] 第 {} 轮：模型产出最终回答（本次对话累计 {} 轮工具循环）", round + 1, round);
                break;
            }
            // 回填 assistant 的工具调用消息
            GlmMessage assistantMessage = new GlmMessage("assistant", result.getContent() == null ? "" : result.getContent());
            assistantMessage.setToolCalls(result.getToolCalls());
            messages.add(assistantMessage);
            // 执行每个工具并回填 tool 消息
            List<String> calledNames = new ArrayList<>();
            for (GlmToolCall call : result.getToolCalls()) {
                String toolName = call.getFunction() == null ? "unknown" : call.getFunction().getName();
                calledNames.add(toolName);
                String payload;
                try {
                    AgentToolRegistry.ToolExecutionResult execution = toolRegistry.execute(toolName, call.getFunction().getArguments());
                    payload = execution.getPayload();
                    trace.add(new ToolTrace(toolName, execution.getBrief()));
                } catch (Exception e) {
                    log.warn("Agent 工具执行失败: {}", toolName, e);
                    payload = "{\"error\":\"" + (e.getMessage() == null ? "工具执行失败" : e.getMessage()) + "\"}";
                }
                messages.add(GlmMessage.tool(call.getId(), payload));
            }
            log.info("[Agent] 第 {} 轮：模型自主决策调用工具 {}，结果回填后继续推理", round + 1, calledNames);
        }

        String reply = result != null && result.getContent() != null && !result.getContent().trim().isEmpty()
                ? result.getContent()
                : "抱歉，我这次没能生成回答，请换个问法试试。";
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("reply", reply);
        data.put("trace", trace);
        return data;
    }

    private String buildSystemPrompt(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是「宠物服务平台」的 AI 智能助手小宠，一个流浪动物救助与宠物服务平台的助手。")
          .append("平台功能包括：流浪动物领养、救助站与喂食点查询、宠物档案、健康记录与健康建议、宠物服务预约（洗护/美容/寄养/医疗）、宠物论坛、公益活动。\n");
        if (user != null) {
            sb.append("\n当前登录用户：").append(user.getNickname() != null ? user.getNickname() : user.getUsername())
              .append("。回答与用户相关的问题时，务必先调用对应工具查询真实数据，不要凭空编造。\n");
        }
        sb.append("\n你可以使用以下工具查询平台真实数据：搜索可领养动物、动物详情、我的领养申请、我的服务订单、我的宠物档案、宠物健康记录、待处理健康建议、服务推荐、救助站搜索、喂食点搜索、公益活动查询、为我智能推荐宠物（匹配算法）；写操作：提交领养申请、录入宠物健康记录、创建服务预约订单、取消服务订单、生成宠物健康建议、发布科普文章（管理员）；管理员还可查询领养/服务/用户三类平台统计数据。\n")
          .append("使用工具的准则：\n")
          .append("1. 涉及平台数据的问题（有哪些可领养的猫、我的申请进度、我的宠物疫苗情况等）必须先调用工具获取真实数据，再基于数据回答。\n")
          .append("2. 需要多个信息时可以连续调用多个工具，并根据上一步结果决定下一步。\n")
          .append("3. 工具返回为空时如实告知用户，并给出建议（例如引导用户去领养页看看）。\n")
          .append("4. 不涉及平台数据的问题（如养宠知识）直接回答，不必调用工具。\n")
          .append("5. 用户咨询宠物健康问题时，给出通用护理建议并提醒及时就医，不做诊疗诊断。\n")
          .append("6. 提交领养申请、录入健康记录、创建/取消服务订单、生成健康建议、发布科普文章都属于写操作，必须严格两步执行：先不带 confirm 调用做预检，把摘要展示给用户并询问确认；只有用户明确同意后才以 confirm=true 正式提交，绝不能代替用户做决定。预检发现字段缺失（如领养理由、预约时间、取消原因）时先向用户询问，不要编造。\n")
          .append("7. 引用动物、宠物等实体时，优先使用工具结果里给出的真实 ID；对话中记不清 ID 就重新调用查询工具或使用名字参数，严禁凭猜测编造 ID。\n")
          .append("8. 平台统计工具与科普文章发布仅管理员可用：普通用户请求时会收到无权限错误，请礼貌解释。\n")
          .append("9. 处理相对时间（如“明天”“周六下午三点”）时，以系统提示中给出的当前时间为基准换算成绝对日期时间，不要向用户确认你已可推算的时间。\n")
          .append("10. 用户问“推荐适合我的宠物”时调用 recommend_pets_for_me，基于算法打分结果解释推荐理由，不要脱离分数自行发挥。\n")
          .append("11. 关于科普文章写作：你没有联网搜索能力，但撰写科普文章不需要联网——用户说“搜索编写”“自己写”时，理解为基于你的自有知识直接撰写即可，严禁以“无法搜索/没有权限”为由拒绝创作。科普主题不限于猫狗，常见异宠（鸟类、兔子、仓鼠、爬宠如蛇/蜥蜴/龟等）都可以写。管理员身份时走 publish_knowledge_article 两阶段发布；非管理员身份时直接把写好的全文作为草稿展示给用户，并说明发布需要管理员账号，同样不得拒绝创作。\n")
          .append("\n回答要求：使用中文，友好亲切，可用少量 emoji；内容用 Markdown 组织，简洁有重点，重要信息（名字、价格、日期、地点）要明确；推荐动物/服务时给出对应的 ID 方便用户查找。")
          .append("\n当前时间：").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return sb.toString();
    }
}
