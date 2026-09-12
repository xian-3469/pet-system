package com.ly.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.pet.entity.ServiceOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务预约订单 Mapper
 */
@Mapper
public interface ServiceOrderMapper extends BaseMapper<ServiceOrder> {
}
