package com.ly.pet.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ly.pet.entity.Applcation;
import com.ly.pet.service.IApplcationService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 预约超时自动关闭任务
 */
@Component
public class ApplcationTimeoutTask {

    @Resource
    private IApplcationService applcationService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 每小时扫描一次待审核预约：如果 Redis 超时标记过期，视为超过48小时未处理并自动关闭
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void closeTimeoutApplcation() {
        QueryWrapper<Applcation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state", "待审核");
        List<Applcation> pendingList = applcationService.list(queryWrapper);
        for (Applcation applcation : pendingList) {
            String pendingKey = "applcation:pending:" + applcation.getId();
            if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(pendingKey))) {
                applcation.setState("已超时关闭");
                applcationService.updateById(applcation);
            }
        }
    }
}
