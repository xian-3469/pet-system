package com.ly.pet.controller;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.pet.common.Result;
import com.ly.pet.common.RoleEnum;
import com.ly.pet.entity.Applcation;
import com.ly.pet.entity.User;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.utils.RoleCheckUtils;
import com.ly.pet.entity.Animal;
import com.ly.pet.service.IAnimalService;
import com.ly.pet.service.IApplcationService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2023-04-02
 */
@RestController
@RequestMapping("/applcation")
public class ApplcationController {

    @Resource
    private IApplcationService applcationService;

    @Resource
    private IAnimalService animalService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Applcation applcation) {
        User currentUser = RoleCheckUtils.assertLogin();
        RoleCheckUtils.assertAnyRole(currentUser, RoleEnum.ROLE_USER.name());
        if (applcation.getAnimalId() == null) {
            throw new ServiceException("400", "动物ID不能为空");
        }
        
        // 使用 Redis 锁防止同一用户重复提交同一宠物的预约申请
        try {
            String lockKey = "lock:applcation:create:" + currentUser.getId() + ":" + applcation.getAnimalId();
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            Boolean locked = operations.setIfAbsent(lockKey, "1", 5, TimeUnit.SECONDS);
            if (!BooleanUtil.isTrue(locked)) {
                throw new ServiceException("429", "请勿重复提交，请稍后再试");
            }
        } catch (Exception e) {
            // Redis 不可用时跳过锁，继续执行业务逻辑
        }
        boolean isNew = applcation.getId() == null;
        applcation.setUserId(currentUser.getId());
        if (isNew) {
            // 检查该用户是否已经对该宠物提交过待审核的申请，防止重复申请
            QueryWrapper<Applcation> duplicateWrapper = new QueryWrapper<>();
            duplicateWrapper.eq("user_id", currentUser.getId());
            duplicateWrapper.eq("animal_id", applcation.getAnimalId());
            duplicateWrapper.eq("state", "待审核");
            Applcation existing = applcationService.getOne(duplicateWrapper);
            if (existing != null) {
                throw new ServiceException("400", "您已提交过该宠物的领养申请，正在审核中，请勿重复提交");
            }
            applcation.setState("待审核");
        }
        applcationService.saveOrUpdate(applcation);
        if (isNew && applcation.getId() != null) {
            try {
                // 利用 Redis TTL 标识预约待审核窗口，供定时任务判断是否超时
                stringRedisTemplate.opsForValue().set("applcation:pending:" + applcation.getId(), "1", 48, TimeUnit.HOURS);
            } catch (Exception e) {
                // Redis 不可用时跳过
            }
        }
        return Result.success();
    }

    @PostMapping("/state/{id}/{state}")
    public Result state(@PathVariable Integer id, @PathVariable String state) {
        User currentUser = RoleCheckUtils.assertLogin();
        RoleCheckUtils.assertManageOrderRole(currentUser);
        Applcation applcation = applcationService.getById(id);
        if (applcation == null) {
            throw new ServiceException("404", "预约记录不存在");
        }
        String lockKey = "lock:applcation:state:" + applcation.getAnimalId();
        String lockValue = applcation.getId() + ":" + System.currentTimeMillis();
        boolean lockAcquired = false;
        try {
            try {
                lockAcquired = Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                        .setIfAbsent(lockKey, lockValue, 3, TimeUnit.SECONDS));
                if (!lockAcquired) {
                    throw new ServiceException("429", "该宠物的申请正在被其他管理员处理中，请稍后再试");
                }
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                // Redis 不可用时跳过锁，继续执行业务逻辑
            }
            applcation.setState(state);

            if ("审核通过".equals(state)) {
                QueryWrapper<Applcation> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("animal_id", applcation.getAnimalId());
                queryWrapper.ne("id", applcation.getId());
                List<Applcation> list = applcationService.list(queryWrapper);
                for (Applcation app : list) {
                    app.setState("审核不通过");
                    applcationService.updateById(app);
                }
            }
            applcationService.updateById(applcation);

            try {
                stringRedisTemplate.delete("applcation:pending:" + applcation.getId());
                stringRedisTemplate.delete("lock:applcation:create:" + applcation.getUserId() + ":" + applcation.getAnimalId());
            } catch (Exception e) {
                // Redis 不可用时跳过
            }

            Animal animal = animalService.getById(applcation.getAnimalId());
            animal.setIsAdopt("是");
            animal.setAdopt("不可领养");
            animalService.updateById(animal);
            return Result.success();
        } finally {
            if (lockAcquired) {
                try {
                    // 只删除自己加的锁，防止误删其他请求的锁
                    String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
                    if (lockValue.equals(currentValue)) {
                        stringRedisTemplate.delete(lockKey);
                    }
                } catch (Exception e) {
                    // Redis 不可用时跳过
                }
            }
        }
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        User currentUser = RoleCheckUtils.assertLogin();
        Applcation applcation = applcationService.getById(id);
        if (applcation == null) {
            return Result.success();
        }
        // 普通用户仅允许删除自己的预约；管理角色可删除任意预约
        if (RoleEnum.ROLE_USER.name().equals(currentUser.getRole()) && !currentUser.getId().equals(applcation.getUserId())) {
            throw new ServiceException("403", "无权限删除该预约");
        }
        // 清理 Redis 相关键
        try {
            stringRedisTemplate.delete("applcation:pending:" + id);
            stringRedisTemplate.delete("lock:applcation:create:" + applcation.getUserId() + ":" + applcation.getAnimalId());
        } catch (Exception e) {
            // Redis 不可用时跳过
        }
        applcationService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        User currentUser = RoleCheckUtils.assertLogin();
        RoleCheckUtils.assertManageOrderRole(currentUser);
        applcationService.removeByIds(ids);
        for (Integer id : ids) {
            try {
                stringRedisTemplate.delete("applcation:pending:" + id);
            } catch (Exception e) {
                // Redis 不可用时跳过
            }
        }
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        User currentUser = RoleCheckUtils.assertLogin();
        RoleCheckUtils.assertManageOrderRole(currentUser);
        return Result.success(applcationService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(applcationService.getById(id));
    }

    @GetMapping("/my")
    public Result my() {
        List<Animal> animals = animalService.list();
        QueryWrapper<Applcation> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        User currentUser = RoleCheckUtils.assertLogin();
        queryWrapper.eq("user_id", currentUser.getId());
        List<Applcation> applcations = applcationService.list(queryWrapper);
        for (Applcation record : applcations) {
            animals.stream().filter(animal -> animal.getId().equals(record.getAnimalId())).findFirst().ifPresent(record::setAnimal);
        }
        return Result.success(applcations);
    }

    /**
     * 根据用户ID查询领养申请列表
     */
    @GetMapping("/user/{userId}")
    public Result findByUserId(@PathVariable Integer userId) {
        QueryWrapper<Applcation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return Result.success(applcationService.list(queryWrapper));
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        User currentUser = RoleCheckUtils.assertLogin();
        List<Animal> animals = animalService.list();
        QueryWrapper<Applcation> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        // 普通用户仅可查看自己的预约记录，机构管理员与系统管理员可查看全部记录
        if (RoleEnum.ROLE_USER.name().equals(currentUser.getRole())) {
            queryWrapper.eq("user_id", currentUser.getId());
        } else {
            RoleCheckUtils.assertManageOrderRole(currentUser);
        }
        Page<Applcation> page = applcationService.page(new Page<>(pageNum, pageSize), queryWrapper);
        for (Applcation record : page.getRecords()) {
            animals.stream().filter(animal -> animal.getId().equals(record.getAnimalId())).findFirst().ifPresent(record::setAnimal);
        }
        return Result.success(page);
    }

}

