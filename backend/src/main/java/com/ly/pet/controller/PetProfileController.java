package com.ly.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.pet.common.Result;
import com.ly.pet.common.RoleEnum;
import com.ly.pet.controller.dto.PetPublicInfoDTO;
import com.ly.pet.entity.PetProfileAuditLog;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.entity.PetProfileCollect;
import com.ly.pet.entity.PetProfileComment;
import com.ly.pet.entity.PetProfileLike;
import com.ly.pet.entity.PetTopic;
import com.ly.pet.entity.User;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.service.*;
import com.ly.pet.utils.RoleCheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 宠物档案管理（含宠物广场社区功能）
 */
@RestController
@RequestMapping("/pet-profile")
public class PetProfileController {

    @Resource
    private IPetProfileService petProfileService;
    @Resource
    private IPetProfileAuditLogService petProfileAuditLogService;
    @Autowired
    private IPetProfileLikeService petProfileLikeService;
    @Autowired
    private IPetProfileCollectService petProfileCollectService;
    @Autowired
    private IPetProfileCommentService petProfileCommentService;
    @Autowired
    private IPetTopicService petTopicService;

    /**
     * 新增宠物档案（仅宠物主人）
     */
    @PostMapping
    public Result create(@RequestBody PetProfile petProfile) {
        User currentUser = RoleCheckUtils.assertLogin();
        validateBaseField(petProfile);
        petProfile.setId(null);
        petProfile.setOwnerId(currentUser.getId());
        if (petProfile.getIsPublic() == null) {
            petProfile.setIsPublic(1);
        }
        petProfileService.save(petProfile);
        return Result.success(petProfile);
    }

    /**
     * 更新宠物档案（仅主人）
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody PetProfile request) {
        User currentUser = RoleCheckUtils.assertLogin();
        PetProfile db = getExist(id);
        assertOwner(currentUser, db);
        validateBaseField(request);

        request.setId(id);
        request.setOwnerId(db.getOwnerId());
        petProfileService.updateById(request);
        return Result.success();
    }

    /**
     * 删除宠物档案（仅主人）
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        User currentUser = RoleCheckUtils.assertLogin();
        PetProfile db = getExist(id);
        if (!(RoleEnum.ROLE_ADMIN.name().equals(currentUser.getRole())
                || RoleEnum.ROLE_ORG_ADMIN.name().equals(currentUser.getRole())
                || RoleEnum.ROLE_SYS_ADMIN.name().equals(currentUser.getRole()))) {
            assertOwner(currentUser, db);
        }
        petProfileService.removeById(id);
        return Result.success();
    }

    /**
     * 我的宠物档案分页查询
     */
    @GetMapping("/my/page")
    public Result myPage(@RequestParam(defaultValue = "") String petName,
                         @RequestParam Integer pageNum,
                         @RequestParam Integer pageSize) {
        User currentUser = RoleCheckUtils.assertLogin();
        QueryWrapper<PetProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id", currentUser.getId()).orderByDesc("id");
        if (!"".equals(petName)) {
            queryWrapper.like("pet_name", petName);
        }
        return Result.success(petProfileService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 查看宠物档案详情：主人可看全量，非主人仅能看公开信息
     */
    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        PetProfile db = getExist(id);
        User currentUser = RoleCheckUtils.assertLogin();
        if (RoleEnum.ROLE_ADMIN.name().equals(currentUser.getRole())
                || RoleEnum.ROLE_ORG_ADMIN.name().equals(currentUser.getRole())
                || RoleEnum.ROLE_SYS_ADMIN.name().equals(currentUser.getRole())) {
            return Result.success(db);
        }
        if (Objects.equals(currentUser.getId(), db.getOwnerId())) {
            return Result.success(db);
        }
        if (!Objects.equals(db.getIsPublic(), 1)) {
            throw new ServiceException("403", "该宠物档案未公开");
        }
        return Result.success(toPublicInfo(db, currentUser.getId()));
    }

    /**
     * 公开宠物档案分页查询（宠物广场，含排序、筛选、互动数据）
     */
    @GetMapping("/public/page")
    public Result publicPage(
            @RequestParam(defaultValue = "") String petName,
            @RequestParam(defaultValue = "latest") String orderBy,
            @RequestParam(defaultValue = "all") String petType,
            @RequestParam(defaultValue = "") String topic,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize) {
        User currentUser = RoleCheckUtils.assertLogin();
        Integer userId = currentUser.getId();

        QueryWrapper<PetProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_public", 1);

        if (!"".equals(petName)) {
            queryWrapper.like("pet_name", petName);
        }

        if (!"all".equals(petType)) {
            queryWrapper.eq("pet_type", petType);
        }

        if (!"".equals(topic)) {
            queryWrapper.like("tags", topic);
        }

        switch (orderBy) {
            case "most_liked":
                queryWrapper.last("ORDER BY (SELECT COUNT(*) FROM pet_profile_like pl WHERE pl.pet_profile_id = pet_profile.id) DESC, id DESC");
                break;
            case "most_commented":
                queryWrapper.last("ORDER BY (SELECT COUNT(*) FROM pet_profile_comment pc WHERE pc.pet_profile_id = pet_profile.id) DESC, id DESC");
                break;
            default:
                queryWrapper.orderByDesc("id");
        }

        Page<PetProfile> page = petProfileService.page(new Page<>(pageNum, pageSize), queryWrapper);
        Page<PetPublicInfoDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(page.getRecords().stream()
                .map(p -> toPublicInfo(p, userId))
                .collect(Collectors.toList()));
        return Result.success(dtoPage);
    }

    /**
     * 管理端全局分页查询（机构管理员/系统管理员）
     */
    @GetMapping("/admin/page")
    public Result adminPage(@RequestParam(defaultValue = "") String petName,
                            @RequestParam(defaultValue = "") String breed,
                            @RequestParam(required = false) Integer isPublic,
                            @RequestParam Integer pageNum,
                            @RequestParam Integer pageSize) {
        User currentUser = RoleCheckUtils.assertLogin();
        RoleCheckUtils.assertManageOrderRole(currentUser);
        QueryWrapper<PetProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(petName)) {
            queryWrapper.like("pet_name", petName);
        }
        if (!"".equals(breed)) {
            queryWrapper.like("breed", breed);
        }
        if (isPublic != null) {
            queryWrapper.eq("is_public", isPublic);
        }
        return Result.success(petProfileService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 管理端审核公开状态（机构管理员/系统管理员）
     */
    @PutMapping("/admin/audit/{id}/{isPublic}")
    public Result auditPublic(@PathVariable Integer id, @PathVariable Integer isPublic) {
        User currentUser = RoleCheckUtils.assertLogin();
        RoleCheckUtils.assertManageOrderRole(currentUser);
        if (!(isPublic == 0 || isPublic == 1)) {
            throw new ServiceException("400", "审核状态非法");
        }
        PetProfile db = getExist(id);
        Integer before = db.getIsPublic();
        db.setIsPublic(isPublic);
        petProfileService.updateById(db);
        PetProfileAuditLog log = new PetProfileAuditLog();
        log.setPetProfileId(db.getId());
        log.setOperatorId(currentUser.getId());
        log.setOperatorName(currentUser.getNickname());
        log.setActionType("PUBLIC_AUDIT");
        log.setBeforePublic(before);
        log.setAfterPublic(isPublic);
        log.setRemark(isPublic == 1 ? "审核公开" : "设为私密");
        petProfileAuditLogService.save(log);
        return Result.success();
    }

    /**
     * 管理端查询审核日志（按宠物档案ID）
     */
    @GetMapping("/admin/log/page")
    public Result adminLogPage(@RequestParam Integer petProfileId,
                               @RequestParam Integer pageNum,
                               @RequestParam Integer pageSize) {
        User currentUser = RoleCheckUtils.assertLogin();
        RoleCheckUtils.assertManageOrderRole(currentUser);
        QueryWrapper<PetProfileAuditLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_profile_id", petProfileId).orderByDesc("id");
        return Result.success(petProfileAuditLogService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    // ============ 宠物广场社区互动接口 ============

    /**
     * 点赞/取消点赞宠物动态
     */
    @PostMapping("/public/like/{petProfileId}")
    public Result toggleLike(@PathVariable Integer petProfileId) {
        User currentUser = RoleCheckUtils.assertLogin();
        Integer userId = currentUser.getId();

        QueryWrapper<PetProfileLike> wrapper = new QueryWrapper<>();
        wrapper.eq("pet_profile_id", petProfileId).eq("user_id", userId);
        PetProfileLike existing = petProfileLikeService.getOne(wrapper);

        if (existing != null) {
            petProfileLikeService.remove(wrapper);
            return Result.success("cancel");
        } else {
            PetProfileLike like = new PetProfileLike();
            like.setPetProfileId(petProfileId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            petProfileLikeService.save(like);
            return Result.success("like");
        }
    }

    /**
     * 收藏/取消收藏宠物动态
     */
    @PostMapping("/public/collect/{petProfileId}")
    public Result toggleCollect(@PathVariable Integer petProfileId) {
        User currentUser = RoleCheckUtils.assertLogin();
        Integer userId = currentUser.getId();

        QueryWrapper<PetProfileCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("pet_profile_id", petProfileId).eq("user_id", userId);
        PetProfileCollect existing = petProfileCollectService.getOne(wrapper);

        if (existing != null) {
            petProfileCollectService.remove(wrapper);
            return Result.success("cancel");
        } else {
            PetProfileCollect collect = new PetProfileCollect();
            collect.setPetProfileId(petProfileId);
            collect.setUserId(userId);
            collect.setCreateTime(LocalDateTime.now());
            petProfileCollectService.save(collect);
            return Result.success("collect");
        }
    }

    /**
     * 评论宠物动态
     */
    @PostMapping("/public/comment")
    public Result comment(@RequestBody Map<String, Object> params) {
        User currentUser = RoleCheckUtils.assertLogin();
        Integer petProfileId = (Integer) params.get("petProfileId");
        String content = (String) params.get("content");

        if (petProfileId == null) {
            throw new ServiceException("400", "宠物档案ID不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new ServiceException("400", "评论内容不能为空");
        }
        if (content.trim().length() > 500) {
            throw new ServiceException("400", "评论内容不能超过500字");
        }

        PetProfileComment comment = new PetProfileComment();
        comment.setPetProfileId(petProfileId);
        comment.setUserId(currentUser.getId());
        comment.setContent(content.trim());
        comment.setCreateTime(LocalDateTime.now());
        petProfileCommentService.save(comment);
        return Result.success(comment);
    }

    /**
     * 获取宠物动态评论列表
     */
    @GetMapping("/public/comments/{petProfileId}")
    public Result getComments(@PathVariable Integer petProfileId,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "20") Integer pageSize) {
        QueryWrapper<PetProfileComment> wrapper = new QueryWrapper<>();
        wrapper.eq("pet_profile_id", petProfileId).orderByDesc("id");
        return Result.success(petProfileCommentService.page(new Page<>(pageNum, pageSize), wrapper));
    }

    /**
     * 获取热门话题标签列表
     */
    @GetMapping("/public/topics")
    public Result getTopics() {
        QueryWrapper<PetTopic> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("use_count").last("LIMIT 20");
        return Result.success(petTopicService.list(wrapper));
    }

    // ============ 私有方法 ============

    private PetProfile getExist(Integer id) {
        PetProfile db = petProfileService.getById(id);
        if (db == null) {
            throw new ServiceException("404", "宠物档案不存在");
        }
        return db;
    }

    private void assertOwner(User currentUser, PetProfile petProfile) {
        if (!Objects.equals(currentUser.getId(), petProfile.getOwnerId())) {
            throw new ServiceException("403", "仅档案主人可操作");
        }
    }

    private void validateBaseField(PetProfile petProfile) {
        if (petProfile.getPetName() == null || "".equals(petProfile.getPetName().trim())) {
            throw new ServiceException("400", "宠物名称不能为空");
        }
        if (petProfile.getBreed() == null || "".equals(petProfile.getBreed().trim())) {
            throw new ServiceException("400", "品种不能为空");
        }
    }

    /**
     * 将 PetProfile 转换为公开 DTO（含互动数据）
     */
    private PetPublicInfoDTO toPublicInfo(PetProfile db, Integer currentUserId) {
        PetPublicInfoDTO dto = new PetPublicInfoDTO();
        dto.setId(db.getId());
        dto.setPetName(db.getPetName());
        dto.setBreed(db.getBreed());
        dto.setPetType(db.getPetType());
        dto.setAge(db.getAge());
        dto.setGender(db.getGender());
        dto.setGenderText(formatGender(db.getGender()));
        dto.setWeight(db.getWeight());
        dto.setNeutered(db.getNeutered());
        dto.setAdoptDate(db.getAdoptDate());
        dto.setAdoptDateText(formatAdoptDate(db.getAdoptDate()));
        dto.setAvatar(db.getAvatar());
        dto.setRemark(db.getRemark());
        dto.setIntro(db.getIntro());
        dto.setTags(db.getTags());

        // 发布时间 & 相对时间
        LocalDateTime createTime = db.getUpdateTime() != null ? db.getUpdateTime() : LocalDateTime.now();
        dto.setCreateTime(createTime);
        dto.setTimeAgo(formatTimeAgo(createTime));

        // 互动数据
        dto.setLikeCount((int) petProfileLikeService.count(new QueryWrapper<PetProfileLike>()
                .eq("pet_profile_id", db.getId())));
        dto.setCommentCount((int) petProfileCommentService.count(new QueryWrapper<PetProfileComment>()
                .eq("pet_profile_id", db.getId())));
        dto.setCollectCount((int) petProfileCollectService.count(new QueryWrapper<PetProfileCollect>()
                .eq("pet_profile_id", db.getId())));

        if (currentUserId != null) {
            dto.setLiked(petProfileLikeService.count(new QueryWrapper<PetProfileLike>()
                    .eq("pet_profile_id", db.getId()).eq("user_id", currentUserId)) > 0);
            dto.setCollected(petProfileCollectService.count(new QueryWrapper<PetProfileCollect>()
                    .eq("pet_profile_id", db.getId()).eq("user_id", currentUserId)) > 0);
        }

        // 发布者信息（通过 ownerId 查询，这里简化处理，直接用ID占位，
        // 实际项目中建议通过 UserService 或缓存获取昵称和头像）
        dto.setOwnerId(db.getOwnerId());
        // 昵称和头像将在前端通过 userId 展示，或后端补充 UserMapper 查询
        return dto;
    }

    private String formatGender(String gender) {
        if ("FEMALE".equalsIgnoreCase(gender) || "母".equals(gender)) {
            return "♀ 母";
        }
        if ("MALE".equalsIgnoreCase(gender) || "公".equals(gender)) {
            return "♂ 公";
        }
        return gender != null ? gender : "";
    }

    private String formatAdoptDate(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return date.toString();
    }

    private String formatTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        if (minutes < 1) {
            return "刚刚";
        }
        if (minutes < 60) {
            return minutes + "分钟前";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "小时前";
        }
        long days = hours / 24;
        if (days < 30) {
            return days + "天前";
        }
        long weeks = days / 7;
        if (weeks < 4) {
            return weeks + "周前";
        }
        long months = days / 30;
        return months + "个月前";
    }
}
