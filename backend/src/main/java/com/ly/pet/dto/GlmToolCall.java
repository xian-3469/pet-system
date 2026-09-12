package com.ly.pet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GLM 工具调用（OpenAI 兼容格式）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlmToolCall {

    private String id;
    /** 固定为 function */
    private String type;
    private FunctionCall function;

    public static GlmToolCall of(String id, String name, String arguments) {
        GlmToolCall toolCall = new GlmToolCall();
        toolCall.setId(id);
        toolCall.setType("function");
        toolCall.setFunction(new FunctionCall(name, arguments));
        return toolCall;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FunctionCall getFunction() {
        return function;
    }

    public void setFunction(FunctionCall function) {
        this.function = function;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FunctionCall {

        private String name;
        /** JSON 字符串形式的参数 */
        private String arguments;

        public FunctionCall() {
        }

        public FunctionCall(String name, String arguments) {
            this.name = name;
            this.arguments = arguments;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getArguments() {
            return arguments;
        }

        public void setArguments(String arguments) {
            this.arguments = arguments;
        }
    }
}
