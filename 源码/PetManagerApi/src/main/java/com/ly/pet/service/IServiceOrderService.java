package com.ly.pet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.pet.controller.dto.ServiceOrderDTO;
import com.ly.pet.entity.ServiceOrder;

/**
 * 服务预约订单 Service 接口
 */
public interface IServiceOrderService extends IService<ServiceOrder> {

    /**
     * 提交预约订单（使用 Redis 分布式锁防止超售）
     *
     * @param dto 预约信息
     * @param userId 当前登录用户ID
     * @return 生成的订单号
     */
    String submitOrder(ServiceOrderDTO dto, Integer userId);

    /**
     * 用户取消订单
     *
     * @param orderId 订单ID
     * @param userId 当前用户ID（校验权限）
     * @param reason 取消原因
     */
    void cancelOrder(Integer orderId, Integer userId, String reason);

    /**
     * 管理员确认订单
     *
     * @param orderId 订单ID
     */
    void confirmOrder(Integer orderId);

    /**
     * 管理员完成订单
     *
     * @param orderId 订单ID
     */
    void completeOrder(Integer orderId);

    /**
     * 管理员确认订单并设置门店信息
     *
     * @param orderId 订单ID
     * @param storeName 门店名称
     * @param storePhone 联系电话
     */
    void confirmOrderWithStore(Integer orderId, String storeName, String storePhone);

    /**
     * 获取高峰时段列表
     * 统计过去30天所有预约订单的时间，按小时分组
     * 找出每天预约量排名前3的时段，标记为高峰时段
     *
     * @return 高峰时段列表，每个元素为小时值(0-23)
     */
    java.util.List<Integer> getPeakHours();
}
