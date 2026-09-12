package com.ly.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.pet.common.Result;
import com.ly.pet.entity.HealthAdvice;
import com.ly.pet.entity.HealthRecord;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.entity.User;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.service.IHealthAdviceService;
import com.ly.pet.service.IHealthRecordService;
import com.ly.pet.service.IPetProfileService;
import com.ly.pet.utils.RoleCheckUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 宠物健康记录管理
 */
@RestController
@RequestMapping("/health-record")
public class HealthRecordController {

    @Resource
    private IHealthRecordService healthRecordService;
    @Resource
    private IPetProfileService petProfileService;
    @Resource
    private IHealthAdviceService healthAdviceService;

    @Value("${spring.file.upload.base-url:}")
    private String baseUrl;

    /**
     * 新增健康记录
     */
    @PostMapping
    public Result create(@RequestBody HealthRecord healthRecord) {
        User currentUser = RoleCheckUtils.assertLogin();
        // 权限校验：只能为自己的宠物添加记录
        PetProfile pet = petProfileService.getById(healthRecord.getPetId());
        if (pet == null) {
            throw new ServiceException("404", "宠物不存在");
        }
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能为自己的宠物添加健康记录");
        }
        validateHealthRecord(healthRecord);
        healthRecord.setId(null);
        healthRecordService.save(healthRecord);
        return Result.success(healthRecord);
    }

    /**
     * 更新健康记录
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody HealthRecord request) {
        User currentUser = RoleCheckUtils.assertLogin();
        HealthRecord db = getExist(id);
        // 权限校验
        PetProfile pet = petProfileService.getById(db.getPetId());
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能修改自己的健康记录");
        }
        validateHealthRecord(request);
        request.setId(id);
        request.setPetId(db.getPetId());
        healthRecordService.updateById(request);
        return Result.success();
    }

    /**
     * 删除健康记录
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        User currentUser = RoleCheckUtils.assertLogin();
        HealthRecord db = getExist(id);
        // 权限校验
        PetProfile pet = petProfileService.getById(db.getPetId());
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能删除自己的健康记录");
        }
        healthRecordService.removeById(id);
        return Result.success();
    }

    /**
     * 我的宠物健康记录分页查询
     */
    @GetMapping("/my/page")
    public Result myPage(@RequestParam Integer petId,
                         @RequestParam(required = false) String recordType,
                         @RequestParam(defaultValue = "1") Integer pageNum,
                         @RequestParam(defaultValue = "10") Integer pageSize) {
        User currentUser = RoleCheckUtils.assertLogin();
        // 权限校验
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException("404", "宠物不存在");
        }
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能查看自己的宠物健康记录");
        }
        QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        if (recordType != null && !"".equals(recordType)) {
            queryWrapper.eq("record_type", recordType);
        }
        queryWrapper.orderByDesc("record_date", "id");
        return Result.success(healthRecordService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 查看健康记录详情
     */
    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        User currentUser = RoleCheckUtils.assertLogin();
        HealthRecord db = getExist(id);
        PetProfile pet = petProfileService.getById(db.getPetId());
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能查看自己的宠物健康记录");
        }
        return Result.success(db);
    }

    /**
     * 健康数据看板：体重变化数据
     */
    @GetMapping("/dashboard/weight")
    public Result weightData(@RequestParam Integer petId) {
        User currentUser = RoleCheckUtils.assertLogin();
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException("404", "宠物不存在");
        }
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能查看自己的宠物健康数据");
        }
        QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.isNotNull("weight");
        queryWrapper.orderByAsc("record_date");
        List<HealthRecord> list = healthRecordService.list(queryWrapper);
        List<Map<String, Object>> chartData = list.stream().map(r -> {
            Map<String, Object> point = new HashMap<>();
            point.put("date", r.getRecordDate());
            point.put("weight", r.getWeight());
            point.put("itemName", r.getItemName());
            return point;
        }).collect(Collectors.toList());
        return Result.success(chartData);
    }

    /**
     * 健康数据看板：各类记录数量统计
     */
    @GetMapping("/dashboard/type-stats")
    public Result typeStats(@RequestParam Integer petId) {
        User currentUser = RoleCheckUtils.assertLogin();
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException("404", "宠物不存在");
        }
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能查看自己的宠物健康数据");
        }
        QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        List<HealthRecord> all = healthRecordService.list(queryWrapper);

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) all.size());
        stats.put("疫苗", all.stream().filter(r -> "疫苗".equals(r.getRecordType())).count());
        stats.put("驱虫", all.stream().filter(r -> "驱虫".equals(r.getRecordType())).count());
        stats.put("体检", all.stream().filter(r -> "体检".equals(r.getRecordType())).count());
        stats.put("治疗", all.stream().filter(r -> "治疗".equals(r.getRecordType())).count());

        // 体重趋势：最新体重及变化
        Map<String, Object> weightTrend = new HashMap<>();
        List<HealthRecord> withWeight = all.stream()
                .filter(r -> r.getWeight() != null)
                .sorted((a, b) -> {
                    if (a.getRecordDate() == null || b.getRecordDate() == null) return 0;
                    return b.getRecordDate().compareTo(a.getRecordDate());
                })
                .collect(Collectors.toList());
        if (!withWeight.isEmpty()) {
            weightTrend.put("latestWeight", withWeight.get(0).getWeight());
            weightTrend.put("latestDate", withWeight.get(0).getRecordDate());
            weightTrend.put("latestItem", withWeight.get(0).getItemName());
            if (withWeight.size() >= 2 && withWeight.get(1).getWeight() != null) {
                double change = withWeight.get(0).getWeight().doubleValue()
                        - withWeight.get(1).getWeight().doubleValue();
                weightTrend.put("change", Math.round(change * 100.0) / 100.0);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("typeStats", stats);
        result.put("weightTrend", weightTrend);
        return Result.success(result);
    }

    /**
     * 健康数据看板：即将到期的提醒（7天内）
     */
    @GetMapping("/dashboard/reminders")
    public Result reminders(@RequestParam Integer petId) {
        User currentUser = RoleCheckUtils.assertLogin();
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException("404", "宠物不存在");
        }
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能查看自己的宠物健康数据");
        }
        QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.isNotNull("next_date");
        queryWrapper.ge("next_date", LocalDate.now());
        queryWrapper.le("next_date", LocalDate.now().plusDays(30));
        queryWrapper.orderByAsc("next_date");
        List<HealthRecord> list = healthRecordService.list(queryWrapper);

        List<Map<String, Object>> reminders = list.stream().map(r -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("recordType", r.getRecordType());
            item.put("itemName", r.getItemName());
            item.put("nextDate", r.getNextDate());
            item.put("hospital", r.getHospital());
            item.put("doctor", r.getDoctor());
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), r.getNextDate());
            item.put("daysLeft", daysLeft);
            if (daysLeft < 0) {
                item.put("status", "已过期");
            } else if (daysLeft == 0) {
                item.put("status", "今天");
            } else if (daysLeft <= 3) {
                item.put("status", "即将到期");
            } else {
                item.put("status", "待处理");
            }
            return item;
        }).collect(Collectors.toList());
        return Result.success(reminders);
    }

    /**
     * 健康数据看板：统计摘要
     */
    @GetMapping("/dashboard/summary")
    public Result dashboardSummary(@RequestParam Integer petId) {
        User currentUser = RoleCheckUtils.assertLogin();
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException("404", "宠物不存在");
        }
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能查看自己的宠物健康数据");
        }
        QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        List<HealthRecord> all = healthRecordService.list(queryWrapper);

        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRecords", all.size());
        summary.put("vaccineCount", all.stream().filter(r -> "疫苗".equals(r.getRecordType())).count());
        summary.put("dewormCount", all.stream().filter(r -> "驱虫".equals(r.getRecordType())).count());
        summary.put("checkupCount", all.stream().filter(r -> "体检".equals(r.getRecordType())).count());
        summary.put("treatmentCount", all.stream().filter(r -> "治疗".equals(r.getRecordType())).count());
        summary.put("overdueCount",
                all.stream().filter(r -> r.getNextDate() != null && r.getNextDate().isBefore(today)).count());
        summary.put("dueSoonCount",
                all.stream().filter(r -> r.getNextDate() != null &&
                        !r.getNextDate().isBefore(today) && !r.getNextDate().isAfter(threeDaysLater)).count());
        summary.put("upcomingCount",
                all.stream().filter(r -> r.getNextDate() != null &&
                        r.getNextDate().isAfter(threeDaysLater) &&
                        !r.getNextDate().isAfter(today.plusDays(30))).count());
        return Result.success(summary);
    }

    /**
     * 健康数据看板：疫苗接种进度
     */
    @GetMapping("/dashboard/vaccine")
    public Result vaccineData(@RequestParam Integer petId) {
        User currentUser = RoleCheckUtils.assertLogin();
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException("404", "宠物不存在");
        }
        if (!Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能查看自己的宠物健康数据");
        }
        QueryWrapper<HealthRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.eq("record_type", "疫苗");
        queryWrapper.orderByDesc("record_date");
        List<HealthRecord> vaccines = healthRecordService.list(queryWrapper);

        // 统计已接种和即将到期的疫苗
        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);
        long expired = vaccines.stream().filter(v -> v.getNextDate() != null && v.getNextDate().isBefore(today)).count();
        long dueSoon = vaccines.stream().filter(v -> v.getNextDate() != null &&
                !v.getNextDate().isBefore(today) && !v.getNextDate().isAfter(threeDaysLater)).count();
        long valid = vaccines.stream().filter(v -> v.getNextDate() == null || v.getNextDate().isAfter(threeDaysLater)).count();

        Map<String, Object> result = new HashMap<>();
        result.put("total", vaccines.size());
        result.put("expired", expired);
        result.put("dueSoon", dueSoon);
        result.put("valid", valid);
        return Result.success(result);
    }

    /**
     * 我的站内信（健康建议）- 获取全部消息列表
     */
    @GetMapping("/notice/my")
    public Result myNotice() {
        User currentUser = RoleCheckUtils.assertLogin();
        // 获取当前用户的所有宠物
        QueryWrapper<PetProfile> petQuery = new QueryWrapper<>();
        petQuery.eq("owner_id", currentUser.getId());
        List<PetProfile> pets = petProfileService.list(petQuery);
        if (pets.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        List<Integer> petIds = pets.stream().map(PetProfile::getId).collect(Collectors.toList());

        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pet_id", petIds);
        queryWrapper.orderByDesc("create_time");
        List<HealthAdvice> list = healthAdviceService.list(queryWrapper);
        return Result.success(list);
    }

    /**
     * 我的站内信（健康建议）分页查询
     */
    @GetMapping("/notice/page")
    public Result noticePage(@RequestParam(required = false) Integer isRead,
                            @RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        User currentUser = RoleCheckUtils.assertLogin();
        // 获取当前用户的所有宠物
        QueryWrapper<PetProfile> petQuery = new QueryWrapper<>();
        petQuery.eq("owner_id", currentUser.getId());
        List<PetProfile> pets = petProfileService.list(petQuery);
        if (pets.isEmpty()) {
            return Result.success(new Page<>(pageNum, pageSize));
        }
        List<Integer> petIds = pets.stream().map(PetProfile::getId).collect(Collectors.toList());

        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pet_id", petIds);
        if (isRead != null) {
            queryWrapper.eq("is_read", isRead);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.success(healthAdviceService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/notice/unread-count")
    public Result unreadCount() {
        User currentUser = RoleCheckUtils.assertLogin();
        // 获取当前用户的所有宠物
        QueryWrapper<PetProfile> petQuery = new QueryWrapper<>();
        petQuery.eq("owner_id", currentUser.getId());
        List<PetProfile> pets = petProfileService.list(petQuery);
        if (pets.isEmpty()) {
            return Result.success(0);
        }
        List<Integer> petIds = pets.stream().map(PetProfile::getId).collect(Collectors.toList());

        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pet_id", petIds);
        queryWrapper.eq("is_read", 0);
        long count = healthAdviceService.count(queryWrapper);
        return Result.success(count);
    }

    /**
     * 标记消息已读
     */
    @PutMapping("/notice/read/{id}")
    public Result markRead(@PathVariable Integer id) {
        User currentUser = RoleCheckUtils.assertLogin();
        HealthAdvice advice = healthAdviceService.getById(id);
        if (advice == null) {
            throw new ServiceException("404", "消息不存在");
        }
        // 权限校验
        PetProfile pet = petProfileService.getById(advice.getPetId());
        if (pet == null || !Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能操作自己的消息");
        }
        advice.setIsRead(1);
        healthAdviceService.updateById(advice);
        return Result.success();
    }

    /**
     * 批量标记已读
     */
    @PutMapping("/notice/read-all")
    public Result markAllRead() {
        User currentUser = RoleCheckUtils.assertLogin();
        // 获取当前用户的所有宠物
        QueryWrapper<PetProfile> petQuery = new QueryWrapper<>();
        petQuery.eq("owner_id", currentUser.getId());
        List<PetProfile> pets = petProfileService.list(petQuery);
        if (pets.isEmpty()) {
            return Result.success();
        }
        List<Integer> petIds = pets.stream().map(PetProfile::getId).collect(Collectors.toList());

        HealthAdvice advice = new HealthAdvice();
        advice.setIsRead(1);
        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pet_id", petIds);
        queryWrapper.eq("is_read", 0);
        healthAdviceService.update(advice, queryWrapper);
        return Result.success();
    }

    /**
     * 删除站内信（健康建议）
     */
    @DeleteMapping("/notice/{id}")
    public Result deleteNotice(@PathVariable Integer id) {
        User currentUser = RoleCheckUtils.assertLogin();
        HealthAdvice advice = healthAdviceService.getById(id);
        if (advice == null) {
            throw new ServiceException("404", "消息不存在");
        }
        // 权限校验
        PetProfile pet = petProfileService.getById(advice.getPetId());
        if (pet == null || !Objects.equals(currentUser.getId(), pet.getOwnerId())) {
            throw new ServiceException("403", "只能删除自己的消息");
        }
        healthAdviceService.removeById(id);
        return Result.success();
    }

    private HealthRecord getExist(Integer id) {
        HealthRecord db = healthRecordService.getById(id);
        if (db == null) {
            throw new ServiceException("404", "健康记录不存在");
        }
        return db;
    }

    private void validateHealthRecord(HealthRecord healthRecord) {
        if (healthRecord.getPetId() == null) {
            throw new ServiceException("400", "请选择宠物");
        }
        if (healthRecord.getRecordType() == null || "".equals(healthRecord.getRecordType().trim())) {
            throw new ServiceException("400", "请选择记录类型");
        }
        if (healthRecord.getItemName() == null || "".equals(healthRecord.getItemName().trim())) {
            throw new ServiceException("400", "请填写项目名称");
        }
        if (healthRecord.getRecordDate() == null) {
            throw new ServiceException("400", "请选择记录时间");
        }
    }
}
