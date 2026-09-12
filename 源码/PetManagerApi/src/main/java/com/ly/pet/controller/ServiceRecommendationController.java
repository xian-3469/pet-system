package com.ly.pet.controller;

import com.ly.pet.common.Result;
import com.ly.pet.entity.RecommendedService;
import com.ly.pet.service.IServiceRecommendationService;
import com.ly.pet.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务推荐 Controller
 * 提供个性化推荐、热门推荐、新服务推荐、相似服务推荐等接口
 */
@Api(tags = "服务推荐管理")
@RestController
@RequestMapping("/service-recommendation")
public class ServiceRecommendationController {

    @Resource
    private IServiceRecommendationService recommendationService;

    /**
     * 获取个性化推荐服务
     * 综合使用协同过滤和内容推荐算法
     *
     * @param limit 返回数量，默认10
     * @return 推荐服务列表
     */
    @ApiOperation("获取个性化推荐服务")
    @GetMapping("/personalized")
    public Result getPersonalizedRecommendations(
            @RequestParam(defaultValue = "10") Integer limit) {
        Integer userId = null;
        try {
            userId = TokenUtils.getCurrentUser().getId();
        } catch (Exception e) {
            // 未登录用户，返回热门推荐
        }
        List<RecommendedService> recommendations = recommendationService.getRecommendations(userId, limit);
        return Result.success(recommendations);
    }

    /**
     * 获取热门推荐服务
     * 结合服务评分、订单量、评价数量计算热门度
     *
     * @param limit 返回数量，默认10
     * @return 热门服务列表
     */
    @ApiOperation("获取热门推荐服务")
    @GetMapping("/hot")
    public Result getHotRecommendations(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<RecommendedService> hotServices = recommendationService.getHotRecommendations(limit);
        return Result.success(hotServices);
    }

    /**
     * 获取新服务推荐
     * 返回最近添加的服务项目
     *
     * @param limit 返回数量，默认6
     * @return 新服务列表
     */
    @ApiOperation("获取新服务推荐")
    @GetMapping("/new")
    public Result getNewRecommendations(
            @RequestParam(defaultValue = "6") Integer limit) {
        List<RecommendedService> newServices = recommendationService.getNewRecommendations(limit);
        return Result.success(newServices);
    }

    /**
     * 获取相似服务推荐
     * 基于指定服务ID，推荐同类型服务
     *
     * @param serviceId 服务ID
     * @param limit 返回数量，默认6
     * @return 相似服务列表
     */
    @ApiOperation("获取相似服务推荐")
    @GetMapping("/similar/{serviceId}")
    public Result getSimilarServices(
            @PathVariable Integer serviceId,
            @RequestParam(defaultValue = "6") Integer limit) {
        List<RecommendedService> similarServices = recommendationService.getSimilarServices(serviceId, limit);
        return Result.success(similarServices);
    }

    /**
     * 获取综合推荐（包含多种类型）
     * 适合在首页展示，包含个性化推荐、热门推荐、新服务推荐
     *
     * @param personalizedLimit 个性化推荐数量
     * @param hotLimit 热门推荐数量
     * @param newLimit 新服务推荐数量
     * @return 综合推荐结果
     */
    @ApiOperation("获取综合推荐")
    @GetMapping("/comprehensive")
    public Result getComprehensiveRecommendations(
            @RequestParam(defaultValue = "6") Integer personalizedLimit,
            @RequestParam(defaultValue = "6") Integer hotLimit,
            @RequestParam(defaultValue = "4") Integer newLimit) {

        Integer userId = null;
        try {
            userId = TokenUtils.getCurrentUser().getId();
        } catch (Exception e) {
            // 未登录用户
        }

        java.util.Map<String, Object> result = new java.util.HashMap<>();

        // 个性化推荐
        result.put("personalized", recommendationService.getRecommendations(userId, personalizedLimit));

        // 热门推荐
        result.put("hot", recommendationService.getHotRecommendations(hotLimit));

        // 新服务推荐
        result.put("newServices", recommendationService.getNewRecommendations(newLimit));

        return Result.success(result);
    }
}
