package com.ly.pet.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.pet.common.Constants;
import com.ly.pet.config.AiConfig;
import com.ly.pet.dto.GlmChatResult;
import com.ly.pet.dto.GlmMessage;
import com.ly.pet.dto.GlmTool;
import com.ly.pet.dto.GlmToolCall;
import com.ly.pet.exception.ServiceException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智谱 GLM 客户端（OpenAI 兼容接口封装）
 */
@Service
public class GlmClient {

    @Resource
    private AiConfig aiConfig;

    @Resource(name = "aiRestTemplate")
    private RestTemplate aiRestTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发起一次对话调用，可携带工具定义
     *
     * @param messages 对话消息列表
     * @param tools    工具定义，可为 null
     * @return 助手消息（含内容或工具调用）
     */
    public GlmChatResult chat(List<GlmMessage> messages, List<GlmTool> tools) {
        if (!aiConfig.isConfigured()) {
            throw new ServiceException(Constants.CODE_500, "AI 功能未配置，请先在 application.yml 中填入智谱 API Key");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", aiConfig.getModel());
        body.put("messages", messages);
        body.put("temperature", 0.7);
        if (tools != null && !tools.isEmpty()) {
            body.put("tools", tools);
            body.put("tool_choice", "auto");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + aiConfig.getApiKey().trim());

        String respBody;
        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            respBody = aiRestTemplate.postForObject(aiConfig.getBaseUrl() + "/chat/completions", entity, String.class);
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "AI 服务调用失败：" + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
        }

        try {
            JsonNode root = objectMapper.readTree(respBody);
            JsonNode errorNode = root.path("error");
            if (!errorNode.isMissingNode()) {
                throw new ServiceException(Constants.CODE_500, "AI 服务返回错误：" + errorNode.path("message").asText("未知错误"));
            }
            JsonNode messageNode = root.path("choices").path(0).path("message");
            String content = messageNode.path("content").asText(null);
            List<GlmToolCall> toolCalls = new ArrayList<>();
            JsonNode toolCallsNode = messageNode.path("tool_calls");
            if (toolCallsNode.isArray()) {
                for (JsonNode callNode : toolCallsNode) {
                    GlmToolCall toolCall = new GlmToolCall();
                    toolCall.setId(callNode.path("id").asText());
                    toolCall.setType(callNode.path("type").asText("function"));
                    JsonNode functionNode = callNode.path("function");
                    toolCall.setFunction(new GlmToolCall.FunctionCall(
                            functionNode.path("name").asText(),
                            functionNode.path("arguments").asText("{}")));
                    toolCalls.add(toolCall);
                }
            }
            return new GlmChatResult(content, toolCalls);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "AI 服务响应解析失败");
        }
    }

    /**
     * 简单对话：不携带工具
     */
    public GlmChatResult chat(List<GlmMessage> messages) {
        return chat(messages, null);
    }

    /**
     * 视觉识别：发送图片（base64 data URI）+ 提示词，返回模型文本（要求为 JSON）
     * 使用视觉模型（GLM-4V 系列，不支持 tools），消息 content 为多模态数组
     */
    public String recognizeImage(String imageDataUri, String prompt) {
        if (!aiConfig.isConfigured()) {
            throw new ServiceException(Constants.CODE_500, "AI 功能未配置，请先在 application.yml 中填入智谱 API Key");
        }
        List<Map<String, Object>> content = new ArrayList<>();
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("type", "text");
        textPart.put("text", prompt);
        content.add(textPart);
        Map<String, Object> imagePart = new HashMap<>();
        imagePart.put("type", "image_url");
        Map<String, Object> urlWrap = new HashMap<>();
        urlWrap.put("url", imageDataUri);
        imagePart.put("image_url", urlWrap);
        content.add(imagePart);

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", content);

        Map<String, Object> body = new HashMap<>();
        body.put("model", aiConfig.getVisionModel());
        body.put("messages", java.util.Collections.singletonList(userMessage));
        body.put("temperature", 0.3);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + aiConfig.getApiKey().trim());

        String respBody;
        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            respBody = aiRestTemplate.postForObject(aiConfig.getBaseUrl() + "/chat/completions", entity, String.class);
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "视觉模型调用失败：" + (e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage()));
        }
        try {
            JsonNode root = objectMapper.readTree(respBody);
            JsonNode errorNode = root.path("error");
            if (!errorNode.isMissingNode()) {
                throw new ServiceException(Constants.CODE_500, "视觉模型返回错误：" + errorNode.path("message").asText("未知错误"));
            }
            String text = root.path("choices").path(0).path("message").path("content").asText(null);
            if (text == null || text.trim().isEmpty()) {
                throw new ServiceException(Constants.CODE_500, "视觉模型未返回内容");
            }
            return text;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "视觉模型响应解析失败");
        }
    }

    /**
     * 从模型返回文本中宽容提取 JSON 对象
     * 处理 ```json 包裹、首尾杂讯，以及模型偶发把中文引号用作 JSON 定界符的问题
     */
    public static JsonNode extractJsonObject(com.fasterxml.jackson.databind.ObjectMapper om, String raw) throws Exception {
        String body = trimToJson(raw, '{', '}');
        try {
            return om.readTree(body);
        } catch (Exception first) {
            try {
                return om.readTree(fixCjkQuotes(body));
            } catch (Exception second) {
                throw first;
            }
        }
    }

    /**
     * 从模型返回文本中宽容提取 JSON 数组
     */
    public static JsonNode extractJsonArray(com.fasterxml.jackson.databind.ObjectMapper om, String raw) throws Exception {
        String body = trimToJson(raw, '[', ']');
        try {
            return om.readTree(body);
        } catch (Exception first) {
            try {
                return om.readTree(fixCjkQuotes(body));
            } catch (Exception second) {
                throw first;
            }
        }
    }

    private static String trimToJson(String raw, char open, char close) {
        String json = raw == null ? "" : raw.trim().replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
        int start = json.indexOf(open);
        int end = json.lastIndexOf(close);
        if (start < 0 || end <= start) {
            throw new IllegalStateException("LLM 返回内容不是 JSON");
        }
        return json.substring(start, end + 1);
    }

    private static String fixCjkQuotes(String body) {
        return body.replace('\u201c', '"').replace('\u201d', '"');
    }
}
