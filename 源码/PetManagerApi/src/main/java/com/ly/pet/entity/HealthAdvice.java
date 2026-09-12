package com.ly.pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 智能健康建议实体
 */
@TableName("health_advice")
@ApiModel(value = "HealthAdvice对象", description = "智能健康建议")
public class HealthAdvice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("建议ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("宠物ID")
    private Integer petId;

    @ApiModelProperty("宠物名称")
    private String petName;

    @ApiModelProperty("宠物品种")
    private String breed;

    @ApiModelProperty("宠物年龄（月）")
    private Integer age;

    @ApiModelProperty("建议类型：DIET饮食 EXERCISE运动 VACCINE疫苗 CHECKUP体检 DENTAL牙齿 GROOMING美容 OTHER其他")
    private String adviceType;

    @ApiModelProperty("建议标题")
    private String title;

    @ApiModelProperty("建议内容")
    private String content;

    @ApiModelProperty("优先级：LOW普通 MEDIUM重要 HIGH紧急")
    private String priority;

    @ApiModelProperty("是否已读：0未读 1已读")
    private Integer isRead;

    @ApiModelProperty("是否已处理：0否 1是")
    private Integer isHandled;

    @ApiModelProperty("建议来源：RULE规则引擎 AI大模型")
    private String source;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPetId() {
        return petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAdviceType() {
        return adviceType;
    }

    public void setAdviceType(String adviceType) {
        this.adviceType = adviceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Integer getIsHandled() {
        return isHandled;
    }

    public void setIsHandled(Integer isHandled) {
        this.isHandled = isHandled;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
