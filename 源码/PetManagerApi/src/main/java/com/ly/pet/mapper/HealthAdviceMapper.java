package com.ly.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.pet.entity.HealthAdvice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康建议 Mapper
 */
@Mapper
public interface HealthAdviceMapper extends BaseMapper<HealthAdvice> {
}
