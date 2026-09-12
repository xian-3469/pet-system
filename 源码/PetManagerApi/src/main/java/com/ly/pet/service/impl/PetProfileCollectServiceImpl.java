package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.PetProfileCollect;
import com.ly.pet.mapper.PetProfileCollectMapper;
import com.ly.pet.service.IPetProfileCollectService;
import org.springframework.stereotype.Service;

/**
 * 宠物动态收藏 Service 实现
 */
@Service
public class PetProfileCollectServiceImpl extends ServiceImpl<PetProfileCollectMapper, PetProfileCollect> implements IPetProfileCollectService {
}
