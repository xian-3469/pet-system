package com.ly.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.pet.common.Constants;
import com.ly.pet.common.Result;
import com.ly.pet.entity.ServiceItem;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.service.IServiceItemService;
import com.ly.pet.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务项目 Controller
 * 管理员：新增、编辑、删除服务项目
 */
@Api(tags = "服务项目管理")
@RestController
@RequestMapping("/service-item")
public class ServiceItemController {

    @Resource
    private IServiceItemService serviceItemService;

    /**
     * 新增或更新服务项目（管理员）
     */
    @ApiOperation("新增或更新服务项目")
    @PostMapping
    public Result save(@RequestBody ServiceItem serviceItem) {
        // 权限校验：仅管理员可操作
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        if (serviceItem.getId() == null) {
            serviceItem.setStatus(1); // 默认启用
        }
        return Result.success(serviceItemService.saveOrUpdate(serviceItem));
    }

    /**
     * 删除服务项目（管理员）
     */
    @ApiOperation("删除服务项目")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        return Result.success(serviceItemService.removeById(id));
    }

    /**
     * 批量删除服务项目（管理员）
     */
    @ApiOperation("批量删除服务项目")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        return Result.success(serviceItemService.removeByIds(ids));
    }

    /**
     * 获取所有服务项目（分页，管理员）
     */
    @ApiOperation("分页查询服务项目")
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String type) {

        QueryWrapper<ServiceItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        if (!"".equals(type)) {
            queryWrapper.eq("type", type);
        }

        return Result.success(serviceItemService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 获取所有启用的服务项目（下拉选择）
     */
    @ApiOperation("获取所有启用的服务项目")
    @GetMapping("/list")
    public Result findAll() {
        QueryWrapper<ServiceItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只查询启用的
        queryWrapper.orderByDesc("id");
        return Result.success(serviceItemService.list(queryWrapper));
    }

    /**
     * 根据ID获取服务项目详情
     */
    @ApiOperation("获取服务项目详情")
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(serviceItemService.getById(id));
    }

    /**
     * 更新服务项目状态（启用/禁用）
     */
    @ApiOperation("更新服务项目状态")
    @PutMapping("/status/{id}")
    public Result updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        ServiceItem item = serviceItemService.getById(id);
        if (item == null) {
            throw new ServiceException(Constants.CODE_400, "服务项目不存在");
        }
        item.setStatus(status);
        return Result.success(serviceItemService.updateById(item));
    }
}
