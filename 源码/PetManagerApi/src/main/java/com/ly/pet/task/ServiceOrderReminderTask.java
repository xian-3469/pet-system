package com.ly.pet.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ly.pet.entity.HealthAdvice;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.entity.ServiceItem;
import com.ly.pet.entity.ServiceOrder;
import com.ly.pet.entity.User;
import com.ly.pet.service.IHealthAdviceService;
import com.ly.pet.service.IPetProfileService;
import com.ly.pet.service.IServiceItemService;
import com.ly.pet.service.IServiceOrderService;
import com.ly.pet.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 服务预约提醒定时任务
 * 每10分钟执行一次，检查即将到期的预约订单并发送健康建议提醒
 */
@Component
public class ServiceOrderReminderTask {

    private static final Logger log = LoggerFactory.getLogger(ServiceOrderReminderTask.class);

    @Resource
    private IServiceOrderService serviceOrderService;
    @Resource
    private IHealthAdviceService healthAdviceService;
    @Resource
    private IUserService userService;
    @Resource
    private IPetProfileService petProfileService;
    @Resource
    private IServiceItemService serviceItemService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

    /**
     * 每10分钟执行一次，检查即将到期的预约订单
     */
    @Scheduled(fixedRate = 600000) // 10分钟 = 600000毫秒
    public void checkServiceOrderReminders() {
        log.info("========== 开始执行预约提醒定时任务 ==========");
        LocalDateTime now = LocalDateTime.now();

        // 任务1：自动将已过期的CONFIRMED订单更新为COMPLETED状态
        autoCompleteExpiredOrders(now);

        // 任务2：发送即将到期的提醒健康建议
        sendUpcomingReminders(now);

        log.info("========== 预约提醒定时任务执行完成 ==========");
    }

    /**
     * 自动将已过期的已确认订单更新为已完成状态
     */
    private void autoCompleteExpiredOrders(LocalDateTime now) {
        log.info("检查已过期的预约订单...");

        // 查询所有已确认但预约时间已过的订单
        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "CONFIRMED");
        queryWrapper.lt("appointment_time", now);
        List<ServiceOrder> expiredOrders = serviceOrderService.list(queryWrapper);

        if (expiredOrders.isEmpty()) {
            log.info("没有需要自动完成的过期订单");
            return;
        }

        log.info("发现 {} 个过期订单需要自动完成", expiredOrders.size());

        for (ServiceOrder order : expiredOrders) {
            order.setStatus("COMPLETED");
            order.setCompleteTime(now);
            serviceOrderService.updateById(order);
            log.info("订单[{}]已自动更新为已完成状态", order.getOrderNo());
        }
    }

    /**
     * 发送即将到期的提醒健康建议
     * 检查12小时内的待审核和已确认订单
     */
    private void sendUpcomingReminders(LocalDateTime now) {
        LocalDateTime twelveHoursLater = now.plusHours(12);

        // 查询12小时内待审核和已确认的预约订单
        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "PENDING");
        queryWrapper.or(wrapper -> wrapper.eq("status", "CONFIRMED"));
        queryWrapper.between("appointment_time", now, twelveHoursLater);
        List<ServiceOrder> upcomingOrders = serviceOrderService.list(queryWrapper);

        log.info("发现 {} 个即将到期的预约订单", upcomingOrders.size());

        for (ServiceOrder order : upcomingOrders) {
            // 检查是否已经发送过提醒（避免重复发送）
            // 通过检查content中是否包含该订单号来判断
            QueryWrapper<HealthAdvice> adviceQuery = new QueryWrapper<>();
            adviceQuery.eq("advice_type", "SERVICE_REMINDER");
            adviceQuery.like("content", order.getOrderNo());
            long existingCount = healthAdviceService.count(adviceQuery);

            if (existingCount == 0) {
                log.info("为订单[{}]发送健康建议提醒", order.getOrderNo());
                sendReminderAdvice(order);
            } else {
                log.info("订单[{}]已发送过提醒，跳过", order.getOrderNo());
            }
        }
    }

    /**
     * 发送预约提醒健康建议
     */
    private void sendReminderAdvice(ServiceOrder order) {
        // 获取用户信息
        User user = userService.getById(order.getUserId());
        String userName = (user != null && user.getNickname() != null) ? user.getNickname() : "用户";

        // 获取宠物信息
        String petName = "宠物";
        if (order.getPetId() != null) {
            PetProfile pet = petProfileService.getById(order.getPetId());
            if (pet != null && pet.getPetName() != null) {
                petName = pet.getPetName();
            }
        }

        // 获取服务项目信息
        String serviceName = "服务";
        String storeName = order.getStoreName();
        String storePhone = order.getStorePhone();
        if (order.getServiceId() != null) {
            ServiceItem service = serviceItemService.getById(order.getServiceId());
            if (service != null) {
                serviceName = service.getName();
                if (storeName == null || storeName.isEmpty()) {
                    storeName = service.getStoreName();
                }
                if (storePhone == null || storePhone.isEmpty()) {
                    storePhone = service.getStorePhone();
                }
            }
        }

        // 格式化时间
        String appointmentDate = order.getAppointmentTime().format(DATE_FORMATTER).substring(0, 11);
        String appointmentDateTime = order.getAppointmentTime().format(DATE_FORMATTER);

        // 默认门店信息
        if (storeName == null || storeName.isEmpty()) {
            storeName = "本平台";
        }
        if (storePhone == null || storePhone.isEmpty()) {
            storePhone = "暂无";
        }

        // 构建健康建议内容（加入订单号用于去重）
        String content = String.format(
                "【订单号：%s】\n" +
                "尊敬的【%s】：\n" +
                "您好！您预约的【%s】的【%s】，将于【%s】开始。\n\n" +
                "服务门店：%s\n" +
                "联系电话：%s\n\n" +
                "请您按时前往门店，为保障服务体验，建议提前10分钟到店。",
                order.getOrderNo(),
                userName,
                petName,
                serviceName,
                appointmentDateTime,
                storeName,
                storePhone
        );

        // 创建健康建议
        HealthAdvice advice = new HealthAdvice();
        advice.setPetId(order.getPetId());
        advice.setPetName(petName);
        advice.setAdviceType("SERVICE_REMINDER");
        advice.setTitle("服务预约到期提醒");
        advice.setContent(content);
        advice.setPriority("HIGH");
        advice.setIsRead(0);
        advice.setIsHandled(0);
        healthAdviceService.save(advice);
    }
}
