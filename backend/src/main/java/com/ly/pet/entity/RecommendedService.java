package com.ly.pet.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 个性化推荐服务实体
 * 用于封装推荐算法返回的推荐服务项目及其推荐理由
 */
@ApiModel(value = "RecommendedService", description = "个性化推荐服务")
public class RecommendedService implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("服务项目ID")
    private Integer serviceId;

    @ApiModelProperty("服务名称")
    private String name;

    @ApiModelProperty("服务类型：BATH-洗澡 BEAUTY-美容 FOSTER-寄养 MEDICAL-医疗")
    private String type;

    @ApiModelProperty("服务描述")
    private String description;

    @ApiModelProperty("服务价格")
    private BigDecimal price;

    @ApiModelProperty("服务时长（分钟）")
    private Integer duration;

    @ApiModelProperty("服务图片")
    private String imageUrl;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店联系电话")
    private String storePhone;

    @ApiModelProperty("平均评分")
    private BigDecimal avgRating;

    @ApiModelProperty("评价数量")
    private Integer reviewCount;

    @ApiModelProperty("推荐得分（0-100）")
    private Double score;

    @ApiModelProperty("推荐理由")
    private String reason;

    @ApiModelProperty("推荐类型：PERSONALIZED-个性化推荐 HOT-热门推荐 SIMILAR-相似服务 NEW-新服务")
    private String recommendType;

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public BigDecimal getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(BigDecimal avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRecommendType() {
        return recommendType;
    }

    public void setRecommendType(String recommendType) {
        this.recommendType = recommendType;
    }
}
