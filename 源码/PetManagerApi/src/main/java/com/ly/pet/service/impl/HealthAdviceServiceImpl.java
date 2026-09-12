package com.ly.pet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.pet.entity.HealthAdvice;
import com.ly.pet.entity.HealthRecord;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.mapper.HealthAdviceMapper;
import com.ly.pet.mapper.HealthRecordMapper;
import com.ly.pet.mapper.PetProfileMapper;
import com.ly.pet.service.IHealthAdviceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康建议 Service 实现
 */
@Service
public class HealthAdviceServiceImpl extends ServiceImpl<HealthAdviceMapper, HealthAdvice> implements IHealthAdviceService {

    @Resource
    private PetProfileMapper petProfileMapper;

    @Resource
    private HealthRecordMapper healthRecordMapper;

    @Override
    public Object getMyPetAdvices(Integer pageNum, Integer pageSize, String adviceType, Integer isRead) {
        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        if (adviceType != null && !adviceType.isEmpty()) {
            queryWrapper.eq("advice_type", adviceType);
        }
        if (isRead != null) {
            queryWrapper.eq("is_read", isRead);
        }

        return this.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public void generateAdvice(Integer petId) {
        PetProfile pet = petProfileMapper.selectById(petId);
        if (pet == null) {
            return;
        }

        // 删除该宠物之前的建议
        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        this.remove(queryWrapper);

        generateAdviceRules(pet);
    }

    @Override
    public void generateAllAdvice() {
        // 查询所有已审核通过（公开）的宠物档案
        QueryWrapper<PetProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_public", 1);
        List<PetProfile> allPets = petProfileMapper.selectList(queryWrapper);

        for (PetProfile pet : allPets) {
            generateAdviceRules(pet);
        }
    }

    private void generateAdviceRules(PetProfile pet) {
        Integer petId = pet.getId();

        // 删除该宠物之前的建议
        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        this.remove(queryWrapper);

        String breed = pet.getBreed() != null ? pet.getBreed().toLowerCase() : "";
        Integer age = pet.getAge() != null ? pet.getAge() : 0;

        // 基于品种和年龄生成建议
        // 1. 疫苗建议
        if (age < 12) {
            this.saveAdvice(pet, "VACCINE", "幼宠疫苗接种", "建议带宠物进行疫苗接种，幼年期需要完成基础免疫程序。", "HIGH");
        } else if (age < 36) {
            this.saveAdvice(pet, "VACCINE", "年度疫苗加强", "宠物已到接种加强针的时间，请联系兽医预约疫苗接种。", "MEDIUM");
        }

        // 2. 驱虫建议
        if (age < 12) {
            this.saveAdvice(pet, "DIET", "幼宠驱虫护理", "幼宠建议每月进行一次体内驱虫，注意观察排便情况。", "HIGH");
        } else {
            this.saveAdvice(pet, "DIET", "定期驱虫提醒", "建议每3个月进行一次体内驱虫，每1-2个月进行一次体外驱虫。", "MEDIUM");
        }

        // 3. 体检建议
        if (age < 12) {
            this.saveAdvice(pet, "CHECKUP", "幼宠首次体检", "建议进行全面的幼宠体检，包括传染病筛查和寄生虫检查。", "HIGH");
        } else if (age > 84) {
            this.saveAdvice(pet, "CHECKUP", "老年宠物深度体检", "宠物已进入老年期，建议每半年进行一次全面体检，关注肝肾功能和关节健康。", "HIGH");
        } else {
            this.saveAdvice(pet, "CHECKUP", "年度体检提醒", "建议每年进行一次全面体检，早发现早治疗。", "MEDIUM");
        }

        // 4. 饮食建议
        if (age < 6) {
            this.saveAdvice(pet, "DIET", "幼宠营养配方", "建议使用专门的幼宠粮，提供充足的蛋白质和钙质支持生长发育。", "HIGH");
        } else if (age > 84) {
            this.saveAdvice(pet, "DIET", "老年宠物饮食调整", "建议选择老年宠物专用粮，控制热量摄入，保护关节和肾脏。", "MEDIUM");
        }

        // 5. 运动建议
        if (breed.contains("边牧") || breed.contains("金毛") || breed.contains("拉布拉多")) {
            this.saveAdvice(pet, "EXERCISE", "大型犬运动建议", breed.contains("边牧") ? "边牧是运动量很大的犬种，每天需要至少2小时的高强度运动。" : "建议每天进行适量的户外运动，保持良好的体态。", "LOW");
        } else if (breed.contains("猫")) {
            this.saveAdvice(pet, "EXERCISE", "猫咪运动建议", "建议每天陪猫咪玩耍15-30分钟，使用逗猫棒或激光笔保持其活力。", "LOW");
        }

        // 6. 美容建议
        if (breed.contains("萨摩") || breed.contains("金毛") || breed.contains("阿拉斯加") || breed.contains("长毛")) {
            this.saveAdvice(pet, "GROOMING", "长毛宠物美容建议", "建议每周至少梳理毛发2-3次，定期洗澡美容，预防毛发打结。", "MEDIUM");
        }

        // 7. 牙齿护理建议
        if (age > 36) {
            this.saveAdvice(pet, "DENTAL", "宠物牙齿护理", "宠物进入中年期，建议开始定期刷牙或使用漱口水，预防牙结石和口腔疾病。", "MEDIUM");
        }

        // 8. 绝育建议
        if (pet.getNeutered() != null && pet.getNeutered() == 0 && age > 6 && age < 24) {
            this.saveAdvice(pet, "OTHER", "绝育建议", "建议在合适的年龄进行绝育手术，可以预防一些生殖系统疾病。", "LOW");
        }

        // 检查健康记录中的下次提醒
        QueryWrapper<HealthRecord> recordWrapper = new QueryWrapper<>();
        recordWrapper.eq("pet_id", petId);
        recordWrapper.isNotNull("next_date");
        List<HealthRecord> records = healthRecordMapper.selectList(recordWrapper);
        for (HealthRecord record : records) {
            if (record.getNextDate() != null) {
                this.saveAdvice(pet, record.getRecordType(), record.getItemName() + "复诊提醒",
                    "根据上次记录，建议在 " + record.getNextDate() + " 进行复诊或复查。", "MEDIUM");
            }
        }
    }

    private void saveAdvice(PetProfile pet, String adviceType, String title, String content, String priority) {
        HealthAdvice advice = new HealthAdvice();
        advice.setPetId(pet.getId());
        advice.setPetName(pet.getPetName());
        advice.setBreed(pet.getBreed());
        advice.setAge(pet.getAge());
        advice.setAdviceType(adviceType);
        advice.setTitle(title);
        advice.setContent(content);
        advice.setPriority(priority);
        advice.setIsRead(0);
        advice.setIsHandled(0);
        advice.setCreateTime(LocalDateTime.now());
        this.save(advice);
    }

    @Override
    public void markAsRead(Integer adviceId) {
        HealthAdvice advice = this.getById(adviceId);
        if (advice != null) {
            advice.setIsRead(1);
            this.updateById(advice);
        }
    }

    @Override
    public void markAsHandled(Integer adviceId) {
        HealthAdvice advice = this.getById(adviceId);
        if (advice != null) {
            advice.setIsHandled(1);
            this.updateById(advice);
        }
    }

    @Override
    public Long getUnreadCount(Integer petId) {
        QueryWrapper<HealthAdvice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pet_id", petId);
        queryWrapper.eq("is_read", 0);
        return this.count(queryWrapper);
    }
}
