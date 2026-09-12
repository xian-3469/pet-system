package com.ly.pet.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.pet.common.Result;
import com.ly.pet.entity.AlgorithmConfig;
import com.ly.pet.service.IAlgorithmConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 算法配置管理 Controller
 */
@Api(tags = "算法配置管理")
@RestController
@RequestMapping("/algorithm-config")
public class AlgorithmConfigController {

    @Resource
    private IAlgorithmConfigService algorithmConfigService;

    /**
     * 新增或更新算法配置
     */
    @ApiOperation("新增或更新算法配置")
    @PostMapping
    public Result save(@RequestBody AlgorithmConfig config) {
        algorithmConfigService.saveOrUpdate(config);
        return Result.success();
    }

    /**
     * 删除算法配置
     */
    @ApiOperation("删除算法配置")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        algorithmConfigService.removeById(id);
        return Result.success();
    }

    /**
     * 批量删除算法配置
     */
    @ApiOperation("批量删除算法配置")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        algorithmConfigService.removeByIds(ids);
        return Result.success();
    }

    /**
     * 查询所有算法配置
     */
    @ApiOperation("查询所有算法配置")
    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "") String algorithmType) {
        QueryWrapper<AlgorithmConfig> queryWrapper = new QueryWrapper<>();
        if (!"".equals(algorithmType)) {
            queryWrapper.eq("algorithm_type", algorithmType);
        }
        queryWrapper.orderByAsc("algorithm_type", "id");
        return Result.success(algorithmConfigService.list(queryWrapper));
    }

    /**
     * 根据ID查询算法配置
     */
    @ApiOperation("根据ID查询算法配置")
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(algorithmConfigService.getById(id));
    }

    /**
     * 分页查询算法配置
     */
    @ApiOperation("分页查询算法配置")
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "") String algorithmType,
                           @RequestParam(defaultValue = "") String configKey,
                           @RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        QueryWrapper<AlgorithmConfig> queryWrapper = new QueryWrapper<>();
        if (!"".equals(algorithmType)) {
            queryWrapper.eq("algorithm_type", algorithmType);
        }
        if (!"".equals(configKey)) {
            queryWrapper.like("config_key", configKey);
        }
        queryWrapper.orderByAsc("algorithm_type", "id");
        return Result.success(algorithmConfigService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }

    /**
     * 根据算法类型获取配置列表
     */
    @ApiOperation("根据算法类型获取配置列表")
    @GetMapping("/type/{algorithmType}")
    public Result getByAlgorithmType(@PathVariable String algorithmType) {
        return Result.success(algorithmConfigService.getByAlgorithmType(algorithmType));
    }

    /**
     * 根据算法类型获取配置Map
     */
    @ApiOperation("根据算法类型获取配置Map")
    @GetMapping("/map/{algorithmType}")
    public Result getConfigMap(@PathVariable String algorithmType) {
        return Result.success(algorithmConfigService.getConfigMapByAlgorithmType(algorithmType));
    }

    /**
     * 获取所有算法类型
     */
    @ApiOperation("获取所有算法类型")
    @GetMapping("/types")
    public Result getAlgorithmTypes() {
        QueryWrapper<AlgorithmConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT algorithm_type");
        List<AlgorithmConfig> list = algorithmConfigService.list(queryWrapper);
        return Result.success(list.stream().map(AlgorithmConfig::getAlgorithmType).distinct());
    }
}
