package com.ly.pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 宠物动态评论
 */
@TableName("pet_profile_comment")
@ApiModel(value = "PetProfileComment对象", description = "宠物动态评论")
public class PetProfileComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("评论ID")
    private Integer id;

    @ApiModelProperty("宠物档案ID")
    private Integer petProfileId;

    @ApiModelProperty("评论用户ID")
    private Integer userId;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评论时间")
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPetProfileId() {
        return petProfileId;
    }

    public void setPetProfileId(Integer petProfileId) {
        this.petProfileId = petProfileId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
