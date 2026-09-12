package com.ly.pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务项目实体（洗澡、美容、寄养、医疗）
 */
@TableName("service_item")
@ApiModel(value = "ServiceItem对象", description = "服务项目")
public class ServiceItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("服务项目ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("服务名称")
    private String name;

    @ApiModelProperty("服务类型：BATH-洗澡 BEAUTY-美容 FOSTER-寄养 MEDICAL-医疗")
    private String type;

    @ApiModelProperty("服务描述")
    private String description;

    @ApiModelProperty("服务价格（元）")
    private BigDecimal price;

    @ApiModelProperty("服务时长（分钟）")
    private Integer duration;

    @ApiModelProperty("服务图片")
    private String imageUrl;

    @ApiModelProperty("状态：0-禁用 1-启用")
    private Integer status;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("门店联系电话")
    private String storePhone;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
