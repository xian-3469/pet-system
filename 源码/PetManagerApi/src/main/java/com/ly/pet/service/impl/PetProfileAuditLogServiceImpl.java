package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.PetProfileAuditLog;
import com.ly.pet.mapper.PetProfileAuditLogMapper;
import com.ly.pet.service.IPetProfileAuditLogService;
import org.springframework.stereotype.Service;

/**
 * 宠物档案审核日志 Service 实现
 */
@Service
public class PetProfileAuditLogServiceImpl extends ServiceImpl<PetProfileAuditLogMapper, PetProfileAuditLog> implements IPetProfileAuditLogService {
}
