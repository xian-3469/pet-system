package com.ly.pet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * GLM 工具（函数）定义，OpenAI 兼容格式
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlmTool {

    private String type = "function";
    private FunctionDef function;

    public static GlmTool of(String name, String description, Map<String, Object> parameters) {
        GlmTool tool = new GlmTool();
        tool.setFunction(new FunctionDef(name, description, parameters));
        return tool;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FunctionDef getFunction() {
        return function;
    }

    public void setFunction(FunctionDef function) {
        this.function = function;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FunctionDef {

        private String name;
        private String description;
        /** JSON Schema 形式的参数定义 */
        private Map<String, Object> parameters;

        public FunctionDef() {
        }

        public FunctionDef(String name, String description, Map<String, Object> parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
    }
}
