package com.ly.pet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.pet.entity.AlgorithmConfig;

import java.util.List;
import java.util.Map;

/**
 * 算法配置 服务类
 */
public interface IAlgorithmConfigService extends IService<AlgorithmConfig> {

    /**
     * 根据算法类型获取配置列表
     * @param algorithmType 算法类型
     * @return 配置列表
     */
    List<AlgorithmConfig> getByAlgorithmType(String algorithmType);

    /**
     * 根据算法类型和配置键获取配置值
     * @param algorithmType 算法类型
     * @param configKey 配置键
     * @return 配置对象
     */
    AlgorithmConfig getByAlgorithmTypeAndKey(String algorithmType, String configKey);

    /**
     * 根据算法类型获取配置Map（key-value形式）
     * @param algorithmType 算法类型
     * @return 配置Map
     */
    Map<String, String> getConfigMapByAlgorithmType(String algorithmType);

    /**
     * 获取配置值并转换为整数
     * @param algorithmType 算法类型
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 整数值
     */
    Integer getIntValue(String algorithmType, String configKey, Integer defaultValue);

    /**
     * 获取配置值并转换为浮点数
     * @param algorithmType 算法类型
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 浮点数值
     */
    Double getDoubleValue(String algorithmType, String configKey, Double defaultValue);

    /**
     * 获取配置值并转换为字符串
     * @param algorithmType 算法类型
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 字符串值
     */
    String getStringValue(String algorithmType, String configKey, String defaultValue);
}
