package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.common.Constants;
import com.ly.pet.controller.dto.ServiceOrderDTO;
import com.ly.pet.entity.ServiceItem;
import com.ly.pet.entity.ServiceOrder;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.mapper.ServiceOrderMapper;
import com.ly.pet.service.IServiceItemService;
import com.ly.pet.service.IServiceOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 服务预约订单 Service 实现
 * 核心功能：使用数据库悲观锁防止预约超售（无需Redis）
 */
@Service
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements IServiceOrderService {

    @Resource
    private IServiceItemService serviceItemService;

    private static final DateTimeFormatter ORDER_NO_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * 提交预约订单（数据库悲观锁防止超售）
     * 使用 synchronized + 数据库唯一约束双重保险
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized String submitOrder(ServiceOrderDTO dto, Integer userId) {
        Integer serviceId = dto.getServiceId();
        LocalDateTime appointmentTime = dto.getAppointmentTime();

        // 1. 检查数据库是否已有同一服务同一时段的订单（使用悲观锁）
        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("service_id", serviceId);
        queryWrapper.eq("appointment_time", appointmentTime);
        queryWrapper.ne("status", "CANCELLED"); // 排除已取消的订单
        long count = this.count(queryWrapper);

        if (count > 0) {
            throw new ServiceException(Constants.CODE_600, "该时间段已被预约，请选择其他时间");
        }

        // 2. 生成订单号：时间戳 + 4位随机数
        String orderNo = generateOrderNo();

        // 3. 创建订单
        ServiceOrder order = new ServiceOrder();
        order.setOrderNo(orderNo);
        order.setServiceId(serviceId);
        order.setPetId(dto.getPetId());
        order.setUserId(userId);
        order.setAppointmentTime(appointmentTime);
        order.setStatus("PENDING"); // 待审核
        order.setRemark(dto.getRemark());

        // 4. 自动从服务项目获取门店信息
        ServiceItem serviceItem = serviceItemService.getById(serviceId);
        if (serviceItem != null) {
            order.setStoreName(serviceItem.getStoreName());
            order.setStorePhone(serviceItem.getStorePhone());
        }

        this.save(order);

        return orderNo;
    }

    /**
     * 用户取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Integer orderId, Integer userId, String reason) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            throw new ServiceException(Constants.CODE_400, "订单不存在");
        }

        // 校验是否为本人订单
        if (!order.getUserId().equals(userId)) {
            throw new ServiceException(Constants.CODE_401, "无权取消该订单");
        }

        // 只有待审核和已确认的订单可以取消
        if (!"PENDING".equals(order.getStatus()) && !"CONFIRMED".equals(order.getStatus())) {
            throw new ServiceException(Constants.CODE_400, "当前状态不允许取消");
        }

        order.setStatus("CANCELLED");
        order.setCancelTime(LocalDateTime.now());
        order.setRemark(reason);
        this.updateById(order);
    }

    /**
     * 管理员确认订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Integer orderId) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            throw new ServiceException(Constants.CODE_400, "订单不存在");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new ServiceException(Constants.CODE_400, "只能确认待审核状态的订单");
        }

        order.setStatus("CONFIRMED");
        order.setConfirmTime(LocalDateTime.now());
        this.updateById(order);
    }

    /**
     * 管理员完成订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Integer orderId) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            throw new ServiceException(Constants.CODE_400, "订单不存在");
        }

        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new ServiceException(Constants.CODE_400, "只能完成已确认状态的订单");
        }

        order.setStatus("COMPLETED");
        order.setCompleteTime(LocalDateTime.now());
        this.updateById(order);
    }

    /**
     * 管理员确认订单并设置门店信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrderWithStore(Integer orderId, String storeName, String storePhone) {
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            throw new ServiceException(Constants.CODE_400, "订单不存在");
        }

        if (!"PENDING".equals(order.getStatus())) {
            throw new ServiceException(Constants.CODE_400, "只能确认待审核状态的订单");
        }

        order.setStatus("CONFIRMED");
        order.setConfirmTime(LocalDateTime.now());
        order.setStoreName(storeName);
        order.setStorePhone(storePhone);
        this.updateById(order);
    }

    /**
     * 生成订单号：时间戳 + 4位随机数
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(ORDER_NO_FORMATTER);
        int random = (int) ((Math.random() * 9 + 1) * 1000);
        return timestamp + random;
    }

    /**
     * 获取高峰时段列表
     * 算法说明：
     * 1. 统计过去30天所有预约订单的时间，按小时分组统计每天每个小时的预约数量
     * 2. 找出每天预约量排名前3的时段（排除预约量<=3的时段）
     * 3. 统计所有高峰时段的出现频率，出现次数>=5天的时段标记为真正的高峰时段
     * 4. 返回高峰小时列表
     */
    @Override
    public java.util.List<Integer> getPeakHours() {
        // 1. 获取过去30天的预约数据
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("appointment_time", startDate);
        queryWrapper.ne("status", "CANCELLED"); // 排除已取消的订单
        queryWrapper.isNotNull("appointment_time");
        java.util.List<ServiceOrder> orders = this.list(queryWrapper);

        if (orders == null || orders.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        // 2. 按日期分组，统计每天每小时的预约数量
        // Map<日期字符串, Map<小时, 数量>>
        java.util.Map<String, java.util.Map<Integer, Integer>> dailyHourlyCount = new java.util.HashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (ServiceOrder order : orders) {
            if (order.getAppointmentTime() == null) continue;
            String dateStr = order.getAppointmentTime().format(dateFormatter);
            int hour = order.getAppointmentTime().getHour();

            dailyHourlyCount.computeIfAbsent(dateStr, k -> new java.util.HashMap<>())
                    .merge(hour, 1, Integer::sum);
        }

        // 3. 找出每天的前3高峰时段（预约量>3才算高峰）
        // Map<小时, 出现天数>
        java.util.Map<Integer, Integer> peakHourFrequency = new java.util.HashMap<>();

        for (java.util.Map<Integer, Integer> hourlyCount : dailyHourlyCount.values()) {
            // 获取当前天各小时的预约量并排序
            java.util.List<java.util.Map.Entry<Integer, Integer>> sortedHours = new java.util.ArrayList<>(hourlyCount.entrySet());
            sortedHours.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            // 取前3名（预约量>3的才计入）
            int topCount = 0;
            for (java.util.Map.Entry<Integer, Integer> entry : sortedHours) {
                if (topCount >= 3 || entry.getValue() <= 3) break;
                peakHourFrequency.merge(entry.getKey(), 1, Integer::sum);
                topCount++;
            }
        }

        // 4. 出现次数>=5天的时段标记为真正的高峰时段
        java.util.List<Integer> peakHours = new java.util.ArrayList<>();
        for (java.util.Map.Entry<Integer, Integer> entry : peakHourFrequency.entrySet()) {
            if (entry.getValue() >= 5) {
                peakHours.add(entry.getKey());
            }
        }

        // 排序返回
        java.util.Collections.sort(peakHours);
        return peakHours;
    }
}
