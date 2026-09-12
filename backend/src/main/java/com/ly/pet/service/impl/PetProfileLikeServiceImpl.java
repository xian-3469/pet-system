package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.PetProfileLike;
import com.ly.pet.mapper.PetProfileLikeMapper;
import com.ly.pet.service.IPetProfileLikeService;
import org.springframework.stereotype.Service;

/**
 * 宠物动态点赞 Service 实现
 */
@Service
public class PetProfileLikeServiceImpl extends ServiceImpl<PetProfileLikeMapper, PetProfileLike> implements IPetProfileLikeService {
}
