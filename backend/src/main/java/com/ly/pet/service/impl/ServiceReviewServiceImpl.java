package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.ServiceReview;
import com.ly.pet.mapper.ServiceReviewMapper;
import com.ly.pet.service.IServiceReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 服务评价 Service 实现
 */
@Service
public class ServiceReviewServiceImpl extends ServiceImpl<ServiceReviewMapper, ServiceReview> implements IServiceReviewService {

    @Override
    public void submitReview(ServiceReview review, Integer userId) {
        review.setUserId(userId);
        review.setCreateTime(LocalDateTime.now());
        this.save(review);
    }

    @Override
    public Object getReviewsByServiceId(Integer serviceId, Integer pageNum, Integer pageSize) {
        QueryWrapper<ServiceReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_id", serviceId);
        queryWrapper.orderByDesc("create_time");
        return this.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public Double getAverageRating(Integer serviceId) {
        QueryWrapper<ServiceReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_id", serviceId);
        Long count = this.count(queryWrapper);
        if (count == 0) {
            return 0.0;
        }
        Double sum = this.getBaseMapper().selectList(queryWrapper)
                .stream()
                .mapToInt(ServiceReview::getRating)
                .sum() * 1.0;
        return sum / count;
    }

    @Override
    public void replyReview(Integer reviewId, String reply) {
        ServiceReview review = this.getById(reviewId);
        if (review != null) {
            review.setReply(reply);
            review.setReplyTime(LocalDateTime.now());
            this.updateById(review);
        }
    }

    @Override
    public Boolean hasReviewed(Integer orderId) {
        QueryWrapper<ServiceReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public Object getServiceReviews(Integer serviceId, Integer limit) {
        QueryWrapper<ServiceReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_id", serviceId);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT " + limit);
        return this.list(queryWrapper);
    }
}
