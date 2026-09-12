package com.ly.pet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.pet.entity.HealthAdvice;

/**
 * 健康建议 Service 接口
 */
public interface IHealthAdviceService extends IService<HealthAdvice> {

    /**
     * 获取我的宠物的健康建议
     */
    Object getMyPetAdvices(Integer pageNum, Integer pageSize, String adviceType, Integer isRead);

    /**
     * 为所有宠物一键生成健康建议
     */
    void generateAllAdvice();

    /**
     * 为指定宠物生成健康建议
     */
    void generateAdvice(Integer petId);

    /**
     * 标记建议为已读
     */
    void markAsRead(Integer adviceId);

    /**
     * 标记建议为已处理
     */
    void markAsHandled(Integer adviceId);

    /**
     * 获取未读建议数量
     */
    Long getUnreadCount(Integer petId);
}
