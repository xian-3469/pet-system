package com.ly.pet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.pet.entity.ServiceReview;

/**
 * 服务评价 Service 接口
 */
public interface IServiceReviewService extends IService<ServiceReview> {

    /**
     * 提交评价
     */
    void submitReview(ServiceReview review, Integer userId);

    /**
     * 根据服务项目ID获取评价列表
     */
    Object getReviewsByServiceId(Integer serviceId, Integer pageNum, Integer pageSize);

    /**
     * 获取服务项目的平均评分
     */
    Double getAverageRating(Integer serviceId);

    /**
     * 回复评价（管理员）
     */
    void replyReview(Integer reviewId, String reply);

    /**
     * 获取订单的评价状态
     */
    Boolean hasReviewed(Integer orderId);

    /**
     * 获取服务项目的前N条评价（用于卡片展示）
     */
    Object getServiceReviews(Integer serviceId, Integer limit);
}
