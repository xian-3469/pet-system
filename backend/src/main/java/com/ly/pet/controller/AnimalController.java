package com.ly.pet.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.pet.common.Result;
import com.ly.pet.config.interceptor.AuthAccess;
import com.ly.pet.entity.Animal;
import com.ly.pet.entity.Applcation;
import com.ly.pet.service.IAnimalService;
import com.ly.pet.service.IApplcationService;
import com.ly.pet.service.impl.AdoptMatchService;
import com.ly.pet.service.impl.PetClusteringService;
import com.ly.pet.entity.User;
import com.ly.pet.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;

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
@RequestMapping("/animal")
public class AnimalController {

    @Resource
    private IAnimalService animalService;

    @Resource
    private IApplcationService applicationService;

    @Resource
    private PetClusteringService petClusteringService;

    @Resource
    private AdoptMatchService adoptMatchService;

    private final String now = DateUtil.now();

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Animal animal) {
        animalService.saveOrUpdate(animal);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        animalService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        animalService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(animalService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(animalService.getById(id));
    }

    @AuthAccess
    @GetMapping("/page/user")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String type,
                           @RequestParam(defaultValue = "") String clusterTag,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Animal> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("nickname", name);
        }
        if (!"".equals(type)) {
            queryWrapper.like("type", type);
        }
        if (!"".equals(clusterTag)) {
            queryWrapper.like("cluster_tag", clusterTag);
        }
        queryWrapper.eq("adopt", "可领养");
        queryWrapper.eq("is_adopt", "否");
        Page<Animal> page = animalService.page(new Page<>(pageNum, pageSize), queryWrapper);
        petClusteringService.setClusterTags(page.getRecords());
        return Result.success(page);
    }

    @AuthAccess
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String adopt,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<Animal> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("nickname", name);
        }
        if (!"".equals(adopt)) {
            queryWrapper.eq("adopt", adopt);
        }
        return Result.success(animalService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 获取匹配度最高的宠物推荐列表
     * 根据用户的领养申请信息，计算所有可领养宠物的匹配度并排序
     */
    @GetMapping("/recommend")
    public Result getRecommendedPets() {
        // 获取当前用户
        try {
            User currentUser = TokenUtils.getCurrentUser();
            if (currentUser == null) {
                return Result.error("400", "请先登录");
            }

            // 查询用户最新的领养申请
            QueryWrapper<Applcation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", currentUser.getId());
            queryWrapper.orderByDesc("create_time");
            queryWrapper.last("LIMIT 1");
            Applcation application = applicationService.getOne(queryWrapper);

            if (application == null) {
                return Result.error("400", "请先提交领养申请");
            }

            // 构建用户画像
            Map<String, String> userProfile = new HashMap<>();
            userProfile.put("experience", application.getExperience());
            userProfile.put("housing", application.getHousing());
            userProfile.put("income", application.getIncome());
            userProfile.put("familyStructure", application.getFamilyStructure());

            // 查询可领养的宠物
            QueryWrapper<Animal> animalQuery = new QueryWrapper<>();
            animalQuery.eq("adopt", "可领养");
            animalQuery.eq("is_adopt", "否");
            List<Animal> animals = animalService.list(animalQuery);

            // 计算每只宠物的匹配度
            List<Map<String, Object>> result = new ArrayList<>();
            for (Animal animal : animals) {
                int score = adoptMatchService.calculateMatchScore(animal, userProfile);
                Map<String, Object> item = new HashMap<>();
                item.put("animal", animal);
                item.put("matchScore", score);
                item.put("matchLevel", adoptMatchService.getMatchLevel(score));
                result.add(item);
            }

            // 按匹配度降序排序
            result.sort((a, b) -> (Integer) b.get("matchScore") - (Integer) a.get("matchScore"));

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("400", "请先登录后再试");
        }
    }

    /**
     * 获取聚类标签选项（用于前端筛选）
     */
    @GetMapping("/cluster/options")
    public Result getClusterOptions() {
        return Result.success(petClusteringService.getClusterOptions());
    }

    /**
     * 批量生成聚类标签
     * 为所有宠物生成聚类标签并保存到数据库
     */
    @PostMapping("/cluster/generate")
    public Result generateClusterTags() {
        List<Animal> animals = animalService.list();
        int count = 0;
        for (Animal animal : animals) {
            petClusteringService.setClusterTag(animal);
            animalService.updateById(animal);
            count++;
        }
        return Result.success("已为 " + count + " 只宠物生成聚类标签");
    }

    /**
     * 按聚类标签筛选宠物
     * @param size 体型：小型/中型/大型
     * @param ageStage 年龄阶段：幼年/成年/老年
     * @param type 品种类型：猫/狗/其他
     * @param clusterTag 完整聚类标签，如：小型幼猫
     */
    @GetMapping("/cluster/search")
    public Result searchByCluster(
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String ageStage,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String clusterTag,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        QueryWrapper<Animal> queryWrapper = new QueryWrapper<>();

        // 组合条件查询
        if (clusterTag != null && !"".equals(clusterTag)) {
            // 直接按完整聚类标签查询
            queryWrapper.like("cluster_tag", clusterTag);
        } else {
            // 按单个维度查询
            if (size != null && !"".equals(size)) {
                queryWrapper.and(wrapper -> wrapper
                    .like("cluster_tag", size)
                    .or()
                    .eq("body_type", size));
            }
            if (ageStage != null && !"".equals(ageStage)) {
                queryWrapper.like("cluster_tag", ageStage);
            }
            if (type != null && !"".equals(type)) {
                queryWrapper.eq("type", type);
            }
        }

        // 过滤条件：可领养状态 + 未被领养
        queryWrapper.eq("adopt", "可领养");
        queryWrapper.eq("is_adopt", "否");
        queryWrapper.orderByDesc("id");

        Page<Animal> page = animalService.page(new Page<>(pageNum, pageSize), queryWrapper);

        // 为返回的宠物设置聚类标签
        petClusteringService.setClusterTags(page.getRecords());

        return Result.success(page);
    }

    /**
     * 获取所有聚类标签及每个标签的宠物数量
     */
    @GetMapping("/cluster/stats")
    public Result getClusterStats() {
        QueryWrapper<Animal> queryWrapper = new QueryWrapper<>();
        // 只统计可领养且未被领养的宠物
        queryWrapper.eq("adopt", "可领养");
        queryWrapper.eq("is_adopt", "否");
        List<Animal> animals = animalService.list(queryWrapper);
        Map<String, Integer> stats = new HashMap<>();

        for (Animal animal : animals) {
            String tag = petClusteringService.generateClusterTag(animal);
            animal.setClusterTag(tag);
            stats.put(tag, stats.getOrDefault(tag, 0) + 1);
        }

        return Result.success(stats);
    }

}

