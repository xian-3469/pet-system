package com.ly.pet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 宠物档案审核日志
 */
@TableName("pet_profile_audit_log")
@ApiModel(value = "PetProfileAuditLog对象", description = "宠物档案审核日志")
public class PetProfileAuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Integer id;

    @ApiModelProperty("宠物档案ID")
    private Integer petProfileId;

    @ApiModelProperty("操作人ID")
    private Integer operatorId;

    @ApiModelProperty("操作人昵称")
    private String operatorName;

    @ApiModelProperty("操作类型")
    private String actionType;

    @ApiModelProperty("审核前公开状态")
    private Integer beforePublic;

    @ApiModelProperty("审核后公开状态")
    private Integer afterPublic;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

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

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Integer getBeforePublic() {
        return beforePublic;
    }

    public void setBeforePublic(Integer beforePublic) {
        this.beforePublic = beforePublic;
    }

    public Integer getAfterPublic() {
        return afterPublic;
    }

    public void setAfterPublic(Integer afterPublic) {
        this.afterPublic = afterPublic;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
