package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ly.pet.entity.RecommendedService;
import com.ly.pet.entity.ServiceItem;
import com.ly.pet.entity.ServiceOrder;
import com.ly.pet.entity.ServiceReview;
import com.ly.pet.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 个性化推荐服务实现类
 * 采用混合推荐算法，结合多种策略为用户提供精准推荐
 */
@Service
public class ServiceRecommendationServiceImpl implements IServiceRecommendationService {

    @Resource
    private IServiceItemService serviceItemService;

    @Resource
    private IServiceOrderService serviceOrderService;

    @Resource
    private IServiceReviewService serviceReviewService;

    @Resource
    private IPetProfileService petProfileService;

    @Resource
    private IAlgorithmConfigService algorithmConfigService;

    // 热门度计算权重（从数据库配置读取）
    private double getRatingWeight() {
        return algorithmConfigService.getDoubleValue("SERVICE_RECOMMENDATION", "rating_weight", 0.4);
    }
    
    private double getOrderCountWeight() {
        return algorithmConfigService.getDoubleValue("SERVICE_RECOMMENDATION", "order_count_weight", 0.3);
    }
    
    private double getRecencyWeight() {
        return algorithmConfigService.getDoubleValue("SERVICE_RECOMMENDATION", "recency_weight", 0.2);
    }
    
    private double getReviewCountWeight() {
        return algorithmConfigService.getDoubleValue("SERVICE_RECOMMENDATION", "review_count_weight", 0.1);
    }
    
    // 新鲜度得分配置（从数据库配置读取）
    private double getRecencyScore7Days() {
        return algorithmConfigService.getDoubleValue("SERVICE_RECOMMENDATION", "recency_days_7", 100.0);
    }
    
    private double getRecencyScore30Days() {
        return algorithmConfigService.getDoubleValue("SERVICE_RECOMMENDATION", "recency_days_30", 80.0);
    }
    
    private double getRecencyScore90Days() {
        return algorithmConfigService.getDoubleValue("SERVICE_RECOMMENDATION", "recency_days_90", 60.0);
    }
    
    private double getRecencyScoreOther() {
        return algorithmConfigService.getDoubleValue("SERVICE_RECOMMENDATION", "recency_days_other", 40.0);
    }

    @Override
    public List<RecommendedService> getRecommendations(Integer userId, int limit) {
        List<RecommendedService> recommendations = new ArrayList<>();

        if (userId == null) {
            // 未登录用户，返回热门推荐
            return getHotRecommendations(limit);
        }

        // 获取用户历史行为
        List<ServiceOrder> userOrders = getUserOrders(userId);
        Set<Integer> orderedServiceIds = userOrders.stream()
                .map(ServiceOrder::getServiceId)
                .collect(Collectors.toSet());

        // 策略1：协同过滤推荐（基于相似用户）
        List<RecommendedService> collaborativeRecs = getCollaborativeFilteringRecommendations(userId, orderedServiceIds, limit / 3);
        recommendations.addAll(collaborativeRecs);

        // 策略2：基于用户偏好的内容推荐
        List<RecommendedService> contentRecs = getContentBasedRecommendations(userId, orderedServiceIds, limit / 3);
        recommendations.addAll(contentRecs);

        // 策略3：热门服务补充
        List<RecommendedService> hotRecs = getHotRecommendations(limit / 3);
        recommendations.addAll(hotRecs);

        // 去重并排序
        return deduplicateAndSort(recommendations, limit);
    }

    @Override
    public List<RecommendedService> getHotRecommendations(int limit) {
        List<ServiceItem> allServices = getEnabledServices();
        if (allServices.isEmpty()) {
            return Collections.emptyList();
        }

        // 计算每个服务的热门度得分
        List<RecommendedService> hotServices = new ArrayList<>();
        for (ServiceItem service : allServices) {
            double hotScore = calculateHotScore(service);
            String reason = generateHotReason(service);

            RecommendedService rs = convertToRecommendedService(service);
            rs.setScore(hotScore);
            rs.setReason(reason);
            rs.setRecommendType("HOT");
            hotServices.add(rs);
        }

        // 按热门度排序
        hotServices.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return hotServices.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<RecommendedService> getNewRecommendations(int limit) {
        QueryWrapper<ServiceItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("create_time");
        List<ServiceItem> newServices = serviceItemService.list(queryWrapper);

        return newServices.stream().limit(limit).map(service -> {
            RecommendedService rs = convertToRecommendedService(service);
            rs.setScore(100.0); // 新服务默认高分
            rs.setReason("新上架的服务项目");
            rs.setRecommendType("NEW");
            return rs;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RecommendedService> getSimilarServices(Integer serviceId, int limit) {
        if (serviceId == null) {
            return Collections.emptyList();
        }

        ServiceItem targetService = serviceItemService.getById(serviceId);
        if (targetService == null) {
            return Collections.emptyList();
        }

        // 查找同类型服务
        QueryWrapper<ServiceItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.eq("type", targetService.getType());
        queryWrapper.ne("id", serviceId);
        queryWrapper.orderByDesc("create_time");
        List<ServiceItem> similarServices = serviceItemService.list(queryWrapper);

        return similarServices.stream().limit(limit).map(service -> {
            RecommendedService rs = convertToRecommendedService(service);
            rs.setScore(calculateSimilarity(targetService, service));
            rs.setReason("与「" + targetService.getName() + "」同类型服务");
            rs.setRecommendType("SIMILAR");
            return rs;
        }).collect(Collectors.toList());
    }

    /**
     * 协同过滤推荐算法
     * 分析用户行为矩阵，找到相似用户群体，推荐他们喜欢但当前用户未使用过的服务
     */
    private List<RecommendedService> getCollaborativeFilteringRecommendations(Integer userId, Set<Integer> excludeIds, int limit) {
        List<RecommendedService> recommendations = new ArrayList<>();

        // 获取所有已完成的订单
        QueryWrapper<ServiceOrder> orderQuery = new QueryWrapper<>();
        orderQuery.eq("status", "COMPLETED");
        List<ServiceOrder> allCompletedOrders = serviceOrderService.list(orderQuery);

        if (allCompletedOrders.isEmpty()) {
            return recommendations;
        }

        // 构建用户-服务矩阵
        Map<Integer, Set<Integer>> userServiceMatrix = new HashMap<>();
        for (ServiceOrder order : allCompletedOrders) {
            userServiceMatrix.computeIfAbsent(order.getUserId(), k -> new HashSet<>()).add(order.getServiceId());
        }

        // 找到与当前用户最相似的用户群体（至少使用了3个相同服务）
        Set<Integer> targetUserServices = userServiceMatrix.get(userId);
        if (targetUserServices == null || targetUserServices.isEmpty()) {
            return recommendations;
        }

        // 计算用户相似度，找到相似用户
        Map<Integer, Integer> similarUserCount = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : userServiceMatrix.entrySet()) {
            if (entry.getKey().equals(userId)) continue;
            Set<Integer> commonServices = new HashSet<>(entry.getValue());
            commonServices.retainAll(targetUserServices);
            if (commonServices.size() >= 2) {
                similarUserCount.put(entry.getKey(), commonServices.size());
            }
        }

        if (similarUserCount.isEmpty()) {
            return recommendations;
        }

        // 获取相似用户喜欢的服务
        Map<Integer, Double> candidateScores = new HashMap<>();
        for (Map.Entry<Integer, Integer> similarUser : similarUserCount.entrySet()) {
            Set<Integer> userServices = userServiceMatrix.get(similarUser.getKey());
            for (Integer serviceId : userServices) {
                if (excludeIds.contains(serviceId)) continue;
                double currentScore = candidateScores.getOrDefault(serviceId, 0.0);
                // 相似用户越多，该服务推荐分越高
                candidateScores.put(serviceId, currentScore + similarUser.getValue() * 10.0);
            }
        }

        // 获取推荐服务详情
        List<Integer> topServiceIds = candidateScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (Integer serviceId : topServiceIds) {
            ServiceItem service = serviceItemService.getById(serviceId);
            if (service != null) {
                RecommendedService rs = convertToRecommendedService(service);
                rs.setScore(candidateScores.get(serviceId));
                rs.setReason("与您品味相似的用户也在使用");
                rs.setRecommendType("PERSONALIZED");
                recommendations.add(rs);
            }
        }

        return recommendations;
    }

    /**
     * 基于内容的推荐算法
     * 分析用户历史使用的服务类型偏好，推荐同类型服务
     */
    private List<RecommendedService> getContentBasedRecommendations(Integer userId, Set<Integer> excludeIds, int limit) {
        List<RecommendedService> recommendations = new ArrayList<>();

        // 获取用户历史订单
        List<ServiceOrder> userOrders = getUserOrders(userId);
        if (userOrders.isEmpty()) {
            return recommendations;
        }

        // 统计用户偏好服务类型
        Map<String, Integer> typePreference = new HashMap<>();
        for (ServiceOrder order : userOrders) {
            ServiceItem service = serviceItemService.getById(order.getServiceId());
            if (service != null) {
                String type = service.getType();
                typePreference.put(type, typePreference.getOrDefault(type, 0) + 1);
            }
        }

        // 获取用户最偏好的服务类型
        String favoriteType = typePreference.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (favoriteType == null) {
            return recommendations;
        }

        // 推荐同类型服务
        QueryWrapper<ServiceItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.eq("type", favoriteType);
        List<ServiceItem> sameTypeServices = serviceItemService.list(queryWrapper);

        // 过滤掉用户已使用过的服务
        List<ServiceItem> candidates = sameTypeServices.stream()
                .filter(s -> !excludeIds.contains(s.getId()))
                .collect(Collectors.toList());

        // 按热门度排序
        candidates.sort((a, b) -> {
            double scoreA = calculateHotScore(a);
            double scoreB = calculateHotScore(b);
            return Double.compare(scoreB, scoreA);
        });

        for (ServiceItem service : candidates.stream().limit(limit).collect(Collectors.toList())) {
            RecommendedService rs = convertToRecommendedService(service);
            rs.setScore(calculateHotScore(service));
            rs.setReason("您喜欢的「" + getTypeName(favoriteType) + "」类型服务");
            rs.setRecommendType("PERSONALIZED");
            recommendations.add(rs);
        }

        return recommendations;
    }

    /**
     * 计算热门度得分
     * 综合考虑评分、订单量、新鲜度、评价数量
     */
    private double calculateHotScore(ServiceItem service) {
        // 获取服务评分
        BigDecimal avgRating = getServiceAvgRating(service.getId());
        double ratingScore = avgRating != null ? avgRating.doubleValue() * 20 : 60.0; // 评分转100分制

        // 获取订单数量
        int orderCount = getServiceOrderCount(service.getId());
        double orderScore = Math.min(orderCount * 5, 50); // 最多50分

        // 计算新鲜度得分（越新得分越高）
        double recencyScore = calculateRecencyScore(service.getCreateTime());

        // 获取评价数量
        int reviewCount = getServiceReviewCount(service.getId());
        double reviewScore = Math.min(reviewCount * 2, 20); // 最多20分

        return ratingScore * getRatingWeight() +
                orderScore * getOrderCountWeight() +
                recencyScore * getRecencyWeight() +
                reviewScore * getReviewCountWeight();
    }

    /**
     * 计算新鲜度得分
     * 7天内的新服务得满分，随时间递减
     */
    private double calculateRecencyScore(LocalDateTime createTime) {
        if (createTime == null) return 50.0;

        long daysSinceCreation = java.time.Duration.between(createTime, LocalDateTime.now()).toDays();
        if (daysSinceCreation <= 7) {
            return getRecencyScore7Days();
        } else if (daysSinceCreation <= 30) {
            return getRecencyScore30Days();
        } else if (daysSinceCreation <= 90) {
            return getRecencyScore90Days();
        } else {
            return getRecencyScoreOther();
        }
    }

    /**
     * 计算服务相似度
     */
    private double calculateSimilarity(ServiceItem target, ServiceItem candidate) {
        double similarity = 0;

        // 类型相同加50分
        if (target.getType() != null && target.getType().equals(candidate.getType())) {
            similarity += 50;
        }

        // 价格相近加30分（价差20%以内）
        if (target.getPrice() != null && candidate.getPrice() != null) {
            double priceDiff = Math.abs(target.getPrice().subtract(candidate.getPrice())
                    .divide(target.getPrice(), 4, RoundingMode.HALF_UP).doubleValue());
            if (priceDiff <= 0.2) {
                similarity += 30;
            } else if (priceDiff <= 0.5) {
                similarity += 15;
            }
        }

        // 时长相近加20分
        if (target.getDuration() != null && candidate.getDuration() != null) {
            int durationDiff = Math.abs(target.getDuration() - candidate.getDuration());
            if (durationDiff <= 15) {
                similarity += 20;
            } else if (durationDiff <= 30) {
                similarity += 10;
            }
        }

        return similarity;
    }

    /**
     * 生成热门推荐理由
     */
    private String generateHotReason(ServiceItem service) {
        BigDecimal avgRating = getServiceAvgRating(service.getId());
        int orderCount = getServiceOrderCount(service.getId());
        int reviewCount = getServiceReviewCount(service.getId());

        StringBuilder reason = new StringBuilder();

        if (avgRating != null && avgRating.compareTo(new BigDecimal("4.5")) >= 0) {
            reason.append("高评分服务");
        } else if (orderCount >= 10) {
            reason.append("超人气服务");
        } else if (reviewCount >= 5) {
            reason.append("深受好评");
        } else {
            reason.append("热门推荐");
        }

        if (reason.length() > 0) {
            reason.append("，已有").append(orderCount).append("人预约");
        }

        return reason.toString();
    }

    /**
     * 获取用户订单列表
     */
    private List<ServiceOrder> getUserOrders(Integer userId) {
        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.ne("status", "CANCELLED"); // 排除已取消的订单
        return serviceOrderService.list(queryWrapper);
    }

    /**
     * 获取启用状态的服务列表
     */
    private List<ServiceItem> getEnabledServices() {
        QueryWrapper<ServiceItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        return serviceItemService.list(queryWrapper);
    }

    /**
     * 获取服务平均评分
     */
    private BigDecimal getServiceAvgRating(Integer serviceId) {
        QueryWrapper<ServiceReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_id", serviceId);
        List<ServiceReview> reviews = serviceReviewService.list(queryWrapper);
        if (reviews.isEmpty()) {
            return null;
        }
        double avg = reviews.stream()
                .mapToInt(ServiceReview::getRating)
                .average()
                .orElse(0);
        return BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * 获取服务订单数量
     */
    private int getServiceOrderCount(Integer serviceId) {
        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_id", serviceId);
        queryWrapper.ne("status", "CANCELLED");
        return (int) serviceOrderService.count(queryWrapper);
    }

    /**
     * 获取服务评价数量
     */
    private int getServiceReviewCount(Integer serviceId) {
        QueryWrapper<ServiceReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_id", serviceId);
        return (int) serviceReviewService.count(queryWrapper);
    }

    /**
     * 将ServiceItem转换为RecommendedService
     */
    private RecommendedService convertToRecommendedService(ServiceItem service) {
        RecommendedService rs = new RecommendedService();
        rs.setServiceId(service.getId());
        rs.setName(service.getName());
        rs.setType(service.getType());
        rs.setDescription(service.getDescription());
        rs.setPrice(service.getPrice());
        rs.setDuration(service.getDuration());
        rs.setImageUrl(service.getImageUrl());
        rs.setStoreName(service.getStoreName());
        rs.setStorePhone(service.getStorePhone());

        // 设置评分信息
        BigDecimal avgRating = getServiceAvgRating(service.getId());
        rs.setAvgRating(avgRating != null ? avgRating : BigDecimal.ZERO);
        rs.setReviewCount(getServiceReviewCount(service.getId()));

        return rs;
    }

    /**
     * 去重并按得分排序
     */
    private List<RecommendedService> deduplicateAndSort(List<RecommendedService> recommendations, int limit) {
        // 按serviceId去重，保留得分最高的
        Map<Integer, RecommendedService> uniqueMap = new LinkedHashMap<>();
        for (RecommendedService rs : recommendations) {
            if (!uniqueMap.containsKey(rs.getServiceId()) ||
                    (rs.getScore() != null && uniqueMap.get(rs.getServiceId()).getScore() != null &&
                            rs.getScore() > uniqueMap.get(rs.getServiceId()).getScore())) {
                uniqueMap.put(rs.getServiceId(), rs);
            }
        }

        return uniqueMap.values().stream()
                .sorted((a, b) -> Double.compare(
                        b.getScore() != null ? b.getScore() : 0,
                        a.getScore() != null ? a.getScore() : 0))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取服务类型中文名称
     */
    private String getTypeName(String type) {
        switch (type) {
            case "BATH": return "洗澡";
            case "BEAUTY": return "美容";
            case "FOSTER": return "寄养";
            case "MEDICAL": return "医疗";
            default: return type;
        }
    }
}
