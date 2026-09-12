package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.HealthRecord;
import com.ly.pet.mapper.HealthRecordMapper;
import com.ly.pet.service.IHealthRecordService;
import org.springframework.stereotype.Service;

/**
 * 宠物健康记录Service实现
 */
@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements IHealthRecordService {
}
