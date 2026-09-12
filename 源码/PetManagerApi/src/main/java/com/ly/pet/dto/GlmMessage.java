package com.ly.pet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * GLM 对话消息（OpenAI 兼容格式）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlmMessage {

    /** 角色：system / user / assistant / tool */
    private String role;
    private String content;
    /** role=tool 时必填，对应工具调用的 id */
    private String toolCallId;
    /** role=assistant 发起的工具调用 */
    private List<GlmToolCall> toolCalls;

    public GlmMessage() {
    }

    public GlmMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public static GlmMessage system(String content) {
        return new GlmMessage("system", content);
    }

    public static GlmMessage user(String content) {
        return new GlmMessage("user", content);
    }

    public static GlmMessage assistant(String content) {
        return new GlmMessage("assistant", content);
    }

    public static GlmMessage tool(String toolCallId, String content) {
        GlmMessage message = new GlmMessage("tool", content);
        message.setToolCallId(toolCallId);
        return message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToolCallId() {
        return toolCallId;
    }

    public void setToolCallId(String toolCallId) {
        this.toolCallId = toolCallId;
    }

    public List<GlmToolCall> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<GlmToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }
}
