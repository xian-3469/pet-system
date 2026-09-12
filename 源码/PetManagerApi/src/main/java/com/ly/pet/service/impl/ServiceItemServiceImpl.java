package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.ServiceItem;
import com.ly.pet.mapper.ServiceItemMapper;
import com.ly.pet.service.IServiceItemService;
import org.springframework.stereotype.Service;

/**
 * 服务项目 Service 实现
 */
@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements IServiceItemService {
}
