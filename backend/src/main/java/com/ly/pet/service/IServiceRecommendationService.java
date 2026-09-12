package com.ly.pet.service;

import com.ly.pet.entity.RecommendedService;

import java.util.List;

/**
 * 个性化推荐服务接口
 * 基于混合推荐算法（协同过滤 + 内容推荐）为用户推荐服务
 */
public interface IServiceRecommendationService {

    /**
     * 为指定用户获取个性化推荐服务列表
     * 算法说明：
     * 1. 协同过滤：分析用户历史行为，找到相似用户喜欢但该用户未使用的服务
     * 2. 内容推荐：基于用户常用服务类型推荐同类型服务
     * 3. 热门推荐：结合服务评分和订单量计算热门度
     *
     * @param userId 用户ID（可以为null，返回热门推荐）
     * @param limit 返回推荐数量，默认为10
     * @return 推荐服务列表
     */
    List<RecommendedService> getRecommendations(Integer userId, int limit);

    /**
     * 获取热门推荐服务列表
     * 结合服务评分和订单数量计算热门度
     *
     * @param limit 返回数量
     * @return 热门服务列表
     */
    List<RecommendedService> getHotRecommendations(int limit);

    /**
     * 获取新服务推荐
     * 返回最近添加的服务项目
     *
     * @param limit 返回数量
     * @return 新服务列表
     */
    List<RecommendedService> getNewRecommendations(int limit);

    /**
     * 获取相似服务推荐
     * 基于指定服务ID，推荐同类型的其他服务
     *
     * @param serviceId 服务ID
     * @param limit 返回数量
     * @return 相似服务列表
     */
    List<RecommendedService> getSimilarServices(Integer serviceId, int limit);
}
