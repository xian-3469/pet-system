package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.PetProfileComment;
import com.ly.pet.mapper.PetProfileCommentMapper;
import com.ly.pet.service.IPetProfileCommentService;
import org.springframework.stereotype.Service;

/**
 * 宠物动态评论 Service 实现
 */
@Service
public class PetProfileCommentServiceImpl extends ServiceImpl<PetProfileCommentMapper, PetProfileComment> implements IPetProfileCommentService {
}
