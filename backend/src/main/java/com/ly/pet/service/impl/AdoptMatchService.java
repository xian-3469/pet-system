package com.ly.pet.service.impl;

import com.ly.pet.entity.Animal;
import com.ly.pet.service.IAlgorithmConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 领养匹配度计算服务
 * 根据用户领养申请信息和宠物体征计算匹配度得分(0-100分)
 */
@Service
public class AdoptMatchService {

    @Resource
    private IAlgorithmConfigService algorithmConfigService;

    // ==================== 权重配置 ====================
    // 从数据库配置读取，默认值
    private int getWeightExperience() {
        return algorithmConfigService.getIntValue("ADOPT_MATCH", "weight_experience", 25);
    }
    
    private int getWeightHousing() {
        return algorithmConfigService.getIntValue("ADOPT_MATCH", "weight_housing", 20);
    }
    
    private int getWeightIncome() {
        return algorithmConfigService.getIntValue("ADOPT_MATCH", "weight_income", 20);
    }
    
    private int getWeightFamily() {
        return algorithmConfigService.getIntValue("ADOPT_MATCH", "weight_family", 15);
    }
    
    private int getWeightBodyType() {
        return algorithmConfigService.getIntValue("ADOPT_MATCH", "weight_body_type", 10);
    }
    
    private int getWeightAge() {
        return algorithmConfigService.getIntValue("ADOPT_MATCH", "weight_age", 10);
    }
    
    // 收入阈值配置
    private int getIncomeHigh() {
        return algorithmConfigService.getIntValue("ADOPT_MATCH", "income_high", 15000);
    }
    
    private int getIncomeMedium() {
        return algorithmConfigService.getIntValue("ADOPT_MATCH", "income_medium", 8000);
    }
    
    private int getIncomeLow() {
        return algorithmConfigService.getIntValue("ADOPT_MATCH", "income_low", 4000);
    }

    /**
     * 计算单只宠物的匹配度
     * @param animal 宠物信息
     * @param userProfile 用户领养申请信息
     * @return 匹配度分数 (0-100)
     */
    public int calculateMatchScore(Animal animal, Map<String, String> userProfile) {
        if (animal == null || userProfile == null) {
            return 0;
        }

        int totalScore = 0;

        // 1. 养宠经验匹配
        totalScore += calcExperienceScore(userProfile.get("experience"), animal.getType());

        // 2. 住房条件匹配
        totalScore += calcHousingScore(userProfile.get("housing"), animal.getBodyType(), animal.getAddress());

        // 3. 收入水平匹配
        totalScore += calcIncomeScore(userProfile.get("income"), animal.getBodyType());

        // 4. 家庭结构匹配
        totalScore += calcFamilyScore(userProfile.get("familyStructure"), animal.getPersonality());

        // 5. 体型匹配
        totalScore += calcBodyTypeScore(userProfile.get("housing"), animal.getBodyType());

        // 6. 宠物体型-年龄匹配
        totalScore += calcAgeScore(userProfile.get("familyStructure"), animal.getAge());

        return Math.min(100, Math.max(0, totalScore));
    }

    /**
     * 计算养宠经验得分
     * 有经验: 满分 | 无经验养猫/小型犬: 满分 | 无经验养大型犬: 1/3分数
     */
    private int calcExperienceScore(String experience, String animalType) {
        int weight = getWeightExperience();
        if (experience == null) return weight / 2;

        boolean hasExperience = experience.contains("有经验") || experience.contains("丰富");
        boolean isCat = animalType != null && (animalType.contains("猫") || animalType.contains("Cat"));
        boolean isLargeDog = animalType != null && !isCat && (
            animalType.contains("大型") || 
            animalType.contains("金毛") || 
            animalType.contains("拉布拉多") ||
            animalType.contains("哈士奇") ||
            animalType.contains("萨摩耶") ||
            animalType.contains("阿拉斯加")
        );

        if (hasExperience) {
            return weight; // 有经验满分
        } else if (isCat || !isLargeDog) {
            return weight; // 无经验养猫/小型犬也可
        } else {
            return weight / 3; // 无经验养大型犬扣分
        }
    }

    /**
     * 计算住房条件得分
     * 别墅养任何体型: 满分 | 公寓养小型/中型: 2/3分数 | 公寓养大型: 1/4分数
     */
    private int calcHousingScore(String housing, String bodyType, String address) {
        int weight = getWeightHousing();
        if (housing == null && address == null) return weight / 2;

        boolean isVilla = housing != null && housing.contains("别墅");
        boolean isApartment = housing != null && housing.contains("公寓");
        boolean indoorOnly = address != null && address.contains("室内") && !address.contains("室外");

        String type = bodyType != null ? bodyType : "小型";

        if (isVilla) {
            return weight; // 别墅满分
        } else if (isApartment) {
            if ("大型".equals(type)) {
                return weight / 4; // 公寓养大型犬扣分严重
            } else if ("中型".equals(type)) {
                return weight * 2 / 3;
            } else {
                return weight; // 公寓养小型满分
            }
        } else if (indoorOnly) {
            return weight * 3 / 4;
        }

        return weight / 2;
    }

    /**
     * 计算收入水平得分
     * 高收入: 满分 | 中等收入: 满分/2(大型)或满分(其他) | 低收入: 满分/4(大型)或2/3(其他)
     */
    private int calcIncomeScore(String income, String bodyType) {
        int weight = getWeightIncome();
        int incomeHigh = getIncomeHigh();
        int incomeMedium = getIncomeMedium();
        int incomeLow = getIncomeLow();
        
        if (income == null) return weight / 2;

        try {
            int incomeValue = Integer.parseInt(income.replaceAll("[^0-9]", ""));
            boolean isLarge = bodyType != null && "大型".equals(bodyType);

            if (incomeValue >= incomeHigh) {
                return weight; // 高收入满分
            } else if (incomeValue >= incomeMedium) {
                if (isLarge) {
                    return weight / 2; // 中等收入养大型犬困难
                }
                return weight; // 中等收入养其他宠物
            } else if (incomeValue >= incomeLow) {
                if (isLarge) {
                    return weight / 4; // 低收入养大型犬困难
                }
                return weight * 2 / 3;
            } else {
                if (isLarge) {
                    return 0; // 低收入养大型犬几乎不可能
                }
                return weight / 2;
            }
        } catch (Exception e) {
            // 无法解析金额，按中等处理
            return weight * 2 / 3;
        }
    }

    /**
     * 计算家庭结构得分
     * 大家庭适合任何性格 | 单身/二人适合温顺/粘人 | 有小孩适合温顺
     */
    private int calcFamilyScore(String familyStructure, String personality) {
        int weight = getWeightFamily();
        if (familyStructure == null) return weight / 2;
        if (personality == null) return weight;

        boolean isGentle = personality.contains("温顺") || personality.contains("安静") || personality.contains("粘人");
        boolean isActive = personality.contains("活泼") || personality.contains("调皮");
        boolean isIndependent = personality.contains("独立");

        if (familyStructure.contains("大家庭") || familyStructure.contains("三口") || familyStructure.contains("孩子")) {
            return isGentle ? weight : weight * 2 / 3;
        } else if (familyStructure.contains("二人") || familyStructure.contains("单身")) {
            if (isGentle) return weight;
            if (isActive && !isIndependent) return weight * 2 / 3;
            return weight / 2;
        }

        return weight * 3 / 4;
    }

    /**
     * 计算体型匹配得分
     */
    private int calcBodyTypeScore(String housing, String bodyType) {
        int weight = getWeightBodyType();
        if (housing == null || bodyType == null) return weight / 2;

        if (housing.contains("别墅")) return weight;
        if (housing.contains("公寓") && "大型".equals(bodyType)) {
            return weight / 4;
        }
        return weight;
    }

    /**
     * 计算年龄匹配得分
     * 有小孩家庭适合成年宠物 | 单身/二人适合幼年宠物
     */
    private int calcAgeScore(String familyStructure, String age) {
        int weight = getWeightAge();
        if (familyStructure == null || age == null) return weight;

        boolean hasChild = familyStructure.contains("三口") || familyStructure.contains("孩子");
        boolean isYoung = age.contains("个月") || age.contains("幼年") || age.contains("1岁以下");

        if (hasChild) {
            return isYoung ? weight * 2 / 3 : weight;
        } else if (familyStructure.contains("单身") || familyStructure.contains("二人")) {
            return isYoung ? weight : weight * 2 / 3;
        }

        return weight;
    }

    /**
     * 获取匹配度等级描述
     */
    public String getMatchLevel(int score) {
        if (score >= 90) return "非常匹配";
        if (score >= 75) return "高度匹配";
        if (score >= 60) return "基本匹配";
        if (score >= 40) return "一般";
        return "不太适合";
    }
}
