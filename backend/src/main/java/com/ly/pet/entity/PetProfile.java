package com.ly.pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宠物档案实体（增强版）
 */
@TableName("pet_profile")
@ApiModel(value = "PetProfile对象", description = "宠物档案")
public class PetProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("宠物ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("主人ID，关联sys_user.id")
    private Integer ownerId;

    @ApiModelProperty("宠物名称")
    private String petName;

    @ApiModelProperty("品种")
    private String breed;

    @ApiModelProperty("宠物大类：cat/dog/other")
    private String petType;

    @ApiModelProperty("年龄（月）")
    private Integer age;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("体重（kg）")
    private BigDecimal weight;

    @ApiModelProperty("绝育状态：0未绝育 1已绝育")
    private Integer neutered;

    @ApiModelProperty("领养日期")
    private LocalDate adoptDate;

    @ApiModelProperty("头像URL")
    private String avatar;

    @ApiModelProperty("紧急联系人")
    private String emergencyContact;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("宠物简介/动态")
    private String intro;

    @ApiModelProperty("话题标签，多个用逗号分隔")
    private String tags;

    @ApiModelProperty("是否公开：0不公开 1公开")
    private Integer isPublic;

    @ApiModelProperty("二维码内容（宠物ID+主人手机号）")
    private String qrCodeContent = "";

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
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

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getNeutered() {
        return neutered;
    }

    public void setNeutered(Integer neutered) {
        this.neutered = neutered;
    }

    public LocalDate getAdoptDate() {
        return adoptDate;
    }

    public void setAdoptDate(LocalDate adoptDate) {
        this.adoptDate = adoptDate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }

    public String getQrCodeContent() {
        return qrCodeContent;
    }

    public void setQrCodeContent(String qrCodeContent) {
        this.qrCodeContent = qrCodeContent;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
