package com.ly.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.pet.common.Constants;
import com.ly.pet.common.Result;
import com.ly.pet.entity.HealthAdvice;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.service.IHealthAdviceService;
import com.ly.pet.service.IPetProfileService;
import com.ly.pet.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 智能健康建议 Controller
 */
@Api(tags = "智能健康建议管理")
@RestController
@RequestMapping("/health-advice")
public class HealthAdviceController {

    @Resource
    private IHealthAdviceService healthAdviceService;

    @Resource
    private IPetProfileService petProfileService;

    /**
     * 获取我的宠物的健康建议
     */
    @ApiOperation("获取我的宠物健康建议")
    @GetMapping("/my")
    public Result getMyPetAdvices(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                  @RequestParam(required = false) String adviceType,
                                  @RequestParam(required = false) Integer isRead) {
        Integer userId = TokenUtils.getCurrentUser().getId();

        // 获取用户的所有宠物
        QueryWrapper<PetProfile> petQueryWrapper = new QueryWrapper<>();
        petQueryWrapper.eq("owner_id", userId);
        List<PetProfile> myPets = petProfileService.list(petQueryWrapper);

        if (myPets.isEmpty()) {
            return Result.success(new Page<>(pageNum, pageSize));
        }

        // 获取宠物ID列表
        List<Integer> petIds = myPets.stream().map(PetProfile::getId).collect(java.util.stream.Collectors.toList());

        // 查询建议
        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pet_id", petIds);
        queryWrapper.orderByDesc("create_time");

        if (adviceType != null && !adviceType.isEmpty()) {
            queryWrapper.eq("advice_type", adviceType);
        }
        if (isRead != null) {
            queryWrapper.eq("is_read", isRead);
        }

        return Result.success(healthAdviceService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 为指定宠物添加健康建议（管理员手动输入）
     */
    @ApiOperation("添加健康建议（管理员手动输入）")
    @PostMapping("/add")
    public Result addAdvice(@RequestBody HealthAdvice healthAdvice) {
        // 权限校验 - 只有管理员可以添加建议
        String role = TokenUtils.getCurrentUser().getRole();
        if (!role.equals("ROLE_ADMIN")
                && !role.equals("ROLE_ORG_ADMIN")
                && !role.equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作，仅管理员可以添加健康建议");
        }

        // 检查宠物是否存在
        PetProfile pet = petProfileService.getById(healthAdvice.getPetId());
        if (pet == null) {
            throw new ServiceException(Constants.CODE_400, "宠物不存在");
        }

        // 只设置必要的字段
        healthAdvice.setIsRead(0);
        healthAdvice.setIsHandled(0);
        healthAdvice.setCreateTime(java.time.LocalDateTime.now());

        healthAdviceService.save(healthAdvice);
        return Result.success("健康建议已添加");
    }

    /**
     * 为所有宠物一键生成健康建议
     */
    @ApiOperation("一键生成所有宠物健康建议")
    @PostMapping("/generate-all")
    public Result generateAllAdvice() {
        String role = TokenUtils.getCurrentUser().getRole();
        if (!role.equals("ROLE_ADMIN")
                && !role.equals("ROLE_ORG_ADMIN")
                && !role.equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作，仅管理员可以生成健康建议");
        }

        healthAdviceService.generateAllAdvice();
        return Result.success("已为所有宠物生成健康建议");
    }

    /**
     * 为指定宠物生成健康建议（管理员自动生成）
     */
    @ApiOperation("生成健康建议（管理员自动生成）")
    @PostMapping("/generate/{petId}")
    public Result generateAdvice(@PathVariable Integer petId) {
        // 权限校验 - 只有管理员可以生成建议
        String role = TokenUtils.getCurrentUser().getRole();
        if (!role.equals("ROLE_ADMIN")
                && !role.equals("ROLE_ORG_ADMIN")
                && !role.equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作，仅管理员可以生成健康建议");
        }

        // 检查宠物是否存在
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException(Constants.CODE_400, "宠物不存在");
        }

        healthAdviceService.generateAdvice(petId);
        return Result.success("健康建议已生成");
    }

    /**
     * 标记建议为已读
     */
    @ApiOperation("标记建议为已读")
    @PutMapping("/read/{id}")
    public Result markAsRead(@PathVariable Integer id) {
        Integer userId = TokenUtils.getCurrentUser().getId();

        HealthAdvice advice = healthAdviceService.getById(id);
        if (advice == null) {
            throw new ServiceException(Constants.CODE_400, "建议不存在");
        }

        // 检查宠物是否属于当前用户
        PetProfile pet = petProfileService.getById(advice.getPetId());
        if (pet != null && !pet.getOwnerId().equals(userId)) {
            throw new ServiceException(Constants.CODE_401, "无权操作");
        }

        healthAdviceService.markAsRead(id);
        return Result.success();
    }

    /**
     * 标记建议为已处理
     */
    @ApiOperation("标记建议为已处理")
    @PutMapping("/handle/{id}")
    public Result markAsHandled(@PathVariable Integer id) {
        Integer userId = TokenUtils.getCurrentUser().getId();

        HealthAdvice advice = healthAdviceService.getById(id);
        if (advice == null) {
            throw new ServiceException(Constants.CODE_400, "建议不存在");
        }

        // 检查宠物是否属于当前用户
        PetProfile pet = petProfileService.getById(advice.getPetId());
        if (pet != null && !pet.getOwnerId().equals(userId)) {
            throw new ServiceException(Constants.CODE_401, "无权操作");
        }

        healthAdviceService.markAsHandled(id);
        return Result.success();
    }

    /**
     * 获取未读建议数量
     */
    @ApiOperation("获取未读建议数量")
    @GetMapping("/unread-count")
    public Result getUnreadCount(@RequestParam Integer petId) {
        Long count = healthAdviceService.getUnreadCount(petId);
        return Result.success(count);
    }

    /**
     * 分页查询所有建议（管理员）
     */
    @ApiOperation("分页查询所有建议（管理员）")
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(required = false) Integer petId,
                           @RequestParam(required = false) String adviceType) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        if (petId != null) {
            queryWrapper.eq("pet_id", petId);
        }
        if (adviceType != null && !adviceType.isEmpty()) {
            queryWrapper.eq("advice_type", adviceType);
        }

        return Result.success(healthAdviceService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 删除建议（管理员）
     */
    @ApiOperation("删除建议")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        // 权限校验
        if (!TokenUtils.getCurrentUser().getRole().equals("ROLE_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_ORG_ADMIN")
                && !TokenUtils.getCurrentUser().getRole().equals("ROLE_SYS_ADMIN")) {
            throw new ServiceException(Constants.CODE_401, "无权限操作");
        }

        return Result.success(healthAdviceService.removeById(id));
    }
}
