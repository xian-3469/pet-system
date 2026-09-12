package com.ly.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.pet.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宠物健康记录Mapper
 */
@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
