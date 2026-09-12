package com.ly.pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2023-04-02
 */
@ApiModel(value = "Animal对象", description = "")
public class Animal implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("动物名字")
    private String nickname;

    @ApiModelProperty("动物性别")
    private String sex;

    @ApiModelProperty("种类")
    private String type;

    @ApiModelProperty("年龄")
    private String age;

    @ApiModelProperty("动物照片")
    private String img;

    @ApiModelProperty("活动范围")
    private String address;

    @ApiModelProperty("身体状态")
    private String status;

    @ApiModelProperty("是否绝育")
    private String sterilization;

    @ApiModelProperty("疫苗接种")
    private String vaccine;

    @ApiModelProperty("领养状态")
    private String adopt;

    @ApiModelProperty("其他描述")
    private String information;
    private String isAdopt;
    private Integer praise;

    @ApiModelProperty("体型：小型/中型/大型")
    private String bodyType;

    @ApiModelProperty("性格：温顺/活泼/独立/粘人")
    private String personality;

    @ApiModelProperty("聚类标签，如：小型幼猫、中型成犬、大型老年犬")
    private String clusterTag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSterilization() {
        return sterilization;
    }

    public void setSterilization(String sterilization) {
        this.sterilization = sterilization;
    }

    public String getVaccine() {
        return vaccine;
    }

    public void setVaccine(String vaccine) {
        this.vaccine = vaccine;
    }

    public String getAdopt() {
        return adopt;
    }

    public void setAdopt(String adopt) {
        this.adopt = adopt;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getIsAdopt() {
        return isAdopt;
    }

    public void setIsAdopt(String isAdopt) {
        this.isAdopt = isAdopt;
    }

    public Integer getPraise() {
        return praise;
    }

    public void setPraise(Integer praise) {
        this.praise = praise;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    public String getClusterTag() {
        return clusterTag;
    }

    public void setClusterTag(String clusterTag) {
        this.clusterTag = clusterTag;
    }
}
