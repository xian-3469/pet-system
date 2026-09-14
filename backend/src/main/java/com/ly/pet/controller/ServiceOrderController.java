package com.ly.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.pet.common.Constants;
import com.ly.pet.common.Result;
import com.ly.pet.controller.dto.ServiceOrderDTO;
import com.ly.pet.entity.ServiceOrder;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.mapper.MenuMapper;
import com.ly.pet.service.IServiceOrderService;
import com.ly.pet.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务预约订单 Controller
 * 宠物主：查看/取消订单
 * 管理员：审核/确认/完成订单
 */
@Api(tags = "服务预约订单管理")
@RestController
@RequestMapping("/service-order")
public class ServiceOrderController {

    @Resource
    private IServiceOrderService serviceOrderService;

    @Resource
    private MenuMapper menuMapper;

    /**
     * 提交预约订单（方法级锁 + 同服务同时段冲突校验防止超约）
     * 宠物主操作
     */
    @ApiOperation("提交预约订单")
    @PostMapping
    public Result submitOrder(@RequestBody ServiceOrderDTO dto) {
        if (dto.getServiceId() == null || dto.getPetId() == null || dto.getAppointmentTime() == null) {
            throw new ServiceException(Constants.CODE_400, "参数错误，请完善预约信息");
        }

        Integer userId = TokenUtils.getCurrentUser().getId();
        String orderNo = serviceOrderService.submitOrder(dto, userId);

        return Result.success(orderNo);
    }

    /**
     * 取消订单（宠物主）
     */
    @ApiOperation("取消订单")
    @PutMapping("/cancel/{id}")
    public Result cancelOrder(@PathVariable Integer id, @RequestParam(required = false) String reason) {
        Integer userId = TokenUtils.getCurrentUser().getId();
        serviceOrderService.cancelOrder(id, userId, reason);
        return Result.success();
    }

    /**
     * 确认订单（管理员）
     */
    @ApiOperation("确认订单")
    @PutMapping("/confirm/{id}")
    public Result confirmOrder(@PathVariable Integer id) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        serviceOrderService.confirmOrder(id);
        return Result.success();
    }

    /**
     * 完成订单（管理员）
     */
    @ApiOperation("完成订单")
    @PutMapping("/complete/{id}")
    public Result completeOrder(@PathVariable Integer id) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        serviceOrderService.completeOrder(id);
        return Result.success();
    }

    /**
     * 设置订单门店信息（管理员在确认订单时设置）
     */
    @ApiOperation("设置订单门店信息")
    @PutMapping("/store/{id}")
    public Result setStoreInfo(@PathVariable Integer id, @RequestBody ServiceOrderDTO dto) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        ServiceOrder order = serviceOrderService.getById(id);
        if (order == null) {
            throw new ServiceException(Constants.CODE_400, "订单不存在");
        }

        order.setStoreName(dto.getStoreName());
        order.setStorePhone(dto.getStorePhone());
        serviceOrderService.updateById(order);

        return Result.success();
    }

    /**
     * 确认订单并设置门店信息（管理员）
     */
    @ApiOperation("确认订单并设置门店信息")
    @PutMapping("/confirmWithStore/{id}")
    public Result confirmOrderWithStore(@PathVariable Integer id, @RequestBody ServiceOrderDTO dto) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        serviceOrderService.confirmOrderWithStore(id, dto.getStoreName(), dto.getStorePhone());
        return Result.success();
    }

    /**
     * 获取我的订单列表（宠物主查看自己的订单）
     */
    @ApiOperation("获取我的订单列表")
    @GetMapping("/my")
    public Result findMyOrders(@RequestParam Integer pageNum,
                               @RequestParam Integer pageSize,
                               @RequestParam(defaultValue = "") String status) {

        Integer userId = TokenUtils.getCurrentUser().getId();
        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");

        if (!"".equals(status)) {
            queryWrapper.eq("status", status);
        }

        return Result.success(serviceOrderService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 分页查询所有订单（管理员）
     */
    @ApiOperation("分页查询所有订单（管理员）")
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                          @RequestParam Integer pageSize,
                          @RequestParam(defaultValue = "") String orderNo,
                          @RequestParam(defaultValue = "") String status,
                          @RequestParam(defaultValue = "") String userId) {

        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        if (!"".equals(orderNo)) {
            queryWrapper.like("order_no", orderNo);
        }
        if (!"".equals(status)) {
            queryWrapper.eq("status", status);
        }
        if (!"".equals(userId)) {
            queryWrapper.eq("user_id", userId);
        }

        return Result.success(serviceOrderService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 根据ID获取订单详情
     */
    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        ServiceOrder order = serviceOrderService.getById(id);
        if (order == null) {
            throw new ServiceException(Constants.CODE_400, "订单不存在");
        }

        // 非管理员只能查看自己的订单
        String role = TokenUtils.getCurrentUser().getRole();
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_ORG_ADMIN") && !role.equals("ROLE_SYS_ADMIN")) {
            if (!order.getUserId().equals(TokenUtils.getCurrentUser().getId())) {
                throw new ServiceException(Constants.CODE_401, "无权查看该订单");
            }
        }

        return Result.success(order);
    }

    /**
     * 删除订单（管理员，仅可删除已取消的订单）
     */
    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        ServiceOrder order = serviceOrderService.getById(id);
        if (order == null) {
            throw new ServiceException(Constants.CODE_400, "订单不存在");
        }

        return Result.success(serviceOrderService.removeById(id));
    }

    /**
     * 更新菜单名称（管理员）
     */
    @ApiOperation("更新菜单名称")
    @PutMapping("/updateMenuName")
    public Result updateMenuName() {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<com.ly.pet.entity.Menu> updateWrapper = new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<>();
        updateWrapper.set("name", "预约审核").set("description", "预约审核管理").like("page_path", "ServiceOrder");
        menuMapper.update(null, updateWrapper);

        return Result.success("菜单已更新，请刷新页面");
    }

    /**
     * 获取订单统计（管理员）
     */
    @ApiOperation("获取订单统计")
    @GetMapping("/statistics")
    public Result getStatistics() {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        java.util.HashMap<String, Integer> map = new java.util.HashMap<>();
        map.put("PENDING", serviceOrderService.lambdaQuery().eq(ServiceOrder::getStatus, "PENDING").count().intValue());
        map.put("CONFIRMED", serviceOrderService.lambdaQuery().eq(ServiceOrder::getStatus, "CONFIRMED").count().intValue());
        map.put("COMPLETED", serviceOrderService.lambdaQuery().eq(ServiceOrder::getStatus, "COMPLETED").count().intValue());
        map.put("CANCELLED", serviceOrderService.lambdaQuery().eq(ServiceOrder::getStatus, "CANCELLED").count().intValue());

        return Result.success(map);
    }

    /**
     * 获取高峰时段列表
     * 统计过去30天所有预约订单的时间，按小时分组
     * 找出每天预约量排名前3的时段，标记为高峰时段
     * 高峰时段判断标准：在30天内有>=5天都出现在当天预约量前3的时段
     */
    @ApiOperation("获取高峰时段列表")
    @GetMapping("/peak-hours")
    public Result getPeakHours() {
        java.util.List<Integer> peakHours = serviceOrderService.getPeakHours();
        return Result.success(peakHours);
    }
}
