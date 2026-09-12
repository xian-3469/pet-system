package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.PetTopic;
import com.ly.pet.mapper.PetTopicMapper;
import com.ly.pet.service.IPetTopicService;
import org.springframework.stereotype.Service;

/**
 * 宠物话题标签 Service 实现
 */
@Service
public class PetTopicServiceImpl extends ServiceImpl<PetTopicMapper, PetTopic> implements IPetTopicService {
}
