package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.mapper.PetProfileMapper;
import com.ly.pet.service.IPetProfileService;
import org.springframework.stereotype.Service;

/**
 * 宠物档案 Service 实现
 */
@Service
public class PetProfileServiceImpl extends ServiceImpl<PetProfileMapper, PetProfile> implements IPetProfileService {
}
