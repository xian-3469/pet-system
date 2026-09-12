package com.ly.pet.dto;

import java.util.List;

/**
 * GLM 一次对话调用的结果
 */
public class GlmChatResult {

    /** 助手回复内容（可能为空，同时存在 toolCalls） */
    private String content;
    /** 助手发起的工具调用 */
    private List<GlmToolCall> toolCalls;

    public GlmChatResult(String content, List<GlmToolCall> toolCalls) {
        this.content = content;
        this.toolCalls = toolCalls;
    }

    public boolean hasToolCalls() {
        return toolCalls != null && !toolCalls.isEmpty();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<GlmToolCall> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<GlmToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }
}
