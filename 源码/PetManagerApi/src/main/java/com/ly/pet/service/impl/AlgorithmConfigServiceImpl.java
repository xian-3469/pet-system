package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ly.pet.entity.AlgorithmConfig;
import com.ly.pet.mapper.AlgorithmConfigMapper;
import com.ly.pet.service.IAlgorithmConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 算法配置 服务实现类
 */
@Service
public class AlgorithmConfigServiceImpl extends ServiceImpl<AlgorithmConfigMapper, AlgorithmConfig> implements IAlgorithmConfigService {

    @Override
    public List<AlgorithmConfig> getByAlgorithmType(String algorithmType) {
        QueryWrapper<AlgorithmConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("algorithm_type", algorithmType);
        queryWrapper.orderByAsc("id");
        return list(queryWrapper);
    }

    @Override
    public AlgorithmConfig getByAlgorithmTypeAndKey(String algorithmType, String configKey) {
        QueryWrapper<AlgorithmConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("algorithm_type", algorithmType);
        queryWrapper.eq("config_key", configKey);
        return getOne(queryWrapper);
    }

    @Override
    public Map<String, String> getConfigMapByAlgorithmType(String algorithmType) {
        List<AlgorithmConfig> configs = getByAlgorithmType(algorithmType);
        Map<String, String> configMap = new HashMap<>();
        for (AlgorithmConfig config : configs) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }

    @Override
    public Integer getIntValue(String algorithmType, String configKey, Integer defaultValue) {
        AlgorithmConfig config = getByAlgorithmTypeAndKey(algorithmType, configKey);
        if (config == null || config.getConfigValue() == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(config.getConfigValue());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public Double getDoubleValue(String algorithmType, String configKey, Double defaultValue) {
        AlgorithmConfig config = getByAlgorithmTypeAndKey(algorithmType, configKey);
        if (config == null || config.getConfigValue() == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(config.getConfigValue());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public String getStringValue(String algorithmType, String configKey, String defaultValue) {
        AlgorithmConfig config = getByAlgorithmTypeAndKey(algorithmType, configKey);
        if (config == null || config.getConfigValue() == null) {
            return defaultValue;
        }
        return config.getConfigValue();
    }
}
