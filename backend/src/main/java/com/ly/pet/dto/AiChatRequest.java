package com.ly.pet.dto;

import java.util.List;
import java.util.Map;

/**
 * AI 助手对话请求
 */
public class AiChatRequest {

    /** 历史消息，元素含 role(user/assistant) 与 content */
    private List<Map<String, String>> history;

    /** 本轮用户输入 */
    private String message;

    public List<Map<String, String>> getHistory() {
        return history;
    }

    public void setHistory(List<Map<String, String>> history) {
        this.history = history;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
