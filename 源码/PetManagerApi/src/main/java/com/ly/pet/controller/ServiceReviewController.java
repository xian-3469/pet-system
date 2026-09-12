package com.ly.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.pet.common.Constants;
import com.ly.pet.common.Result;
import com.ly.pet.entity.ServiceOrder;
import com.ly.pet.entity.ServiceReview;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.service.IServiceOrderService;
import com.ly.pet.service.IServiceReviewService;
import com.ly.pet.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 服务评价 Controller
 */
@Api(tags = "服务评价管理")
@RestController
@RequestMapping("/service-review")
public class ServiceReviewController {

    @Resource
    private IServiceReviewService serviceReviewService;

    @Resource
    private IServiceOrderService serviceOrderService;

    /**
     * 提交服务评价
     */
    @ApiOperation("提交服务评价")
    @PostMapping
    public Result submitReview(@RequestBody ServiceReview review) {
        if (review.getOrderId() == null) {
            throw new ServiceException(Constants.CODE_400, "订单ID不能为空");
        }
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new ServiceException(Constants.CODE_400, "请选择1-5星评分");
        }

        // 检查订单是否已完成且属于当前用户
        ServiceOrder order = serviceOrderService.getById(review.getOrderId());
        if (order == null) {
            throw new ServiceException(Constants.CODE_400, "订单不存在");
        }
        if (!"COMPLETED".equals(order.getStatus())) {
            throw new ServiceException(Constants.CODE_400, "只能评价已完成的订单");
        }
        if (!order.getUserId().equals(TokenUtils.getCurrentUser().getId())) {
            throw new ServiceException(Constants.CODE_401, "无权评价此订单");
        }

        // 检查是否已评价
        if (serviceReviewService.hasReviewed(review.getOrderId())) {
            throw new ServiceException(Constants.CODE_400, "该订单已评价");
        }

        Integer userId = TokenUtils.getCurrentUser().getId();
        String nickname = TokenUtils.getCurrentUser().getNickname();
        review.setNickname(nickname);
        serviceReviewService.submitReview(review, userId);

        return Result.success();
    }

    /**
     * 获取服务项目的评价列表
     */
    @ApiOperation("获取服务项目评价列表")
    @GetMapping("/service/{serviceId}")
    public Result getReviewsByService(@PathVariable Integer serviceId,
                                       @RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(serviceReviewService.getReviewsByServiceId(serviceId, pageNum, pageSize));
    }

    /**
     * 获取服务项目的平均评分
     */
    @ApiOperation("获取服务项目平均评分")
    @GetMapping("/rating/{serviceId}")
    public Result getAverageRating(@PathVariable Integer serviceId) {
        Double avgRating = serviceReviewService.getAverageRating(serviceId);
        return Result.success(avgRating);
    }

    /**
     * 获取服务项目的前N条评价（用于卡片展示）
     */
    @ApiOperation("获取服务项目评价（卡片展示用）")
    @GetMapping("/service/preview/{serviceId}")
    public Result getServiceReviewsPreview(@PathVariable Integer serviceId,
                                          @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(serviceReviewService.getServiceReviews(serviceId, limit));
    }

    /**
     * 回复评价（管理员）
     */
    @ApiOperation("回复评价")
    @PutMapping("/reply/{id}")
    public Result replyReview(@PathVariable Integer id, @RequestParam String reply) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        serviceReviewService.replyReview(id, reply);
        return Result.success();
    }

    /**
     * 分页查询所有评价（管理员）
     */
    @ApiOperation("分页查询所有评价（管理员）")
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(required = false) Integer serviceId,
                           @RequestParam(required = false) Integer rating) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        QueryWrapper<ServiceReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        if (serviceId != null) {
            queryWrapper.eq("service_id", serviceId);
        }
        if (rating != null) {
            queryWrapper.eq("rating", rating);
        }

        return Result.success(serviceReviewService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
}
