package com.ly.pet.service.impl;

import com.ly.pet.entity.Animal;
import com.ly.pet.service.IAlgorithmConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 宠物特征聚类服务
 * 根据品种类型、年龄阶段、体型三个维度对宠物进行聚类分组
 */
@Service
public class PetClusteringService {

    @Resource
    private IAlgorithmConfigService algorithmConfigService;

    // ==================== 品种类型常量 ====================
    private static final String TYPE_CAT = "猫";           // 猫科
    private static final String TYPE_DOG = "狗";          // 犬科
    private static final String TYPE_OTHER = "其他";       // 其他

    // ==================== 年龄阶段常量 ====================
    private static final String AGE_YOUNG = "幼年";       // <1岁
    private static final String AGE_ADULT = "成年";        // 1-7岁
    private static final String AGE_SENIOR = "老年";       // >7岁

    // ==================== 体型常量 ====================
    private static final String SIZE_SMALL = "小型";      // <10kg
    private static final String SIZE_MEDIUM = "中型";      // 10-25kg
    private static final String SIZE_LARGE = "大型";      // >25kg

    // ==================== 体型与体重的对应关系（kg）====================
    // 从数据库配置读取，默认值
    private double getSmallMaxWeight() {
        return algorithmConfigService.getDoubleValue("CLUSTERING", "small_max_weight", 10.0);
    }
    
    private double getMediumMaxWeight() {
        return algorithmConfigService.getDoubleValue("CLUSTERING", "medium_max_weight", 25.0);
    }

    /**
     * 根据品种类型获取聚类标签
     */
    public String getTypeLabel(String type) {
        if (type == null) return TYPE_OTHER;
        type = type.trim();
        if (type.contains(TYPE_CAT) || type.toLowerCase().contains("cat")) {
            return TYPE_CAT;
        } else if (type.contains(TYPE_DOG) || type.toLowerCase().contains("dog")) {
            return TYPE_DOG;
        }
        return TYPE_OTHER;
    }

    /**
     * 根据年龄字符串解析年龄阶段
     * 支持格式：
     * - "2周岁" -> 成年
     * - "6个月" -> 幼年
     * - "8岁" -> 老年
     * - "12个月" -> 幼年
     * - "3岁" -> 成年
     */
    public String getAgeStage(String ageStr) {
        if (ageStr == null) return AGE_ADULT;

        // 提取数字
        Pattern pattern = Pattern.compile("(\\d+(?:\\.\\d+)?)");
        Matcher matcher = pattern.matcher(ageStr);

        if (matcher.find()) {
            double age = Double.parseDouble(matcher.group(1));

            // 判断单位：月还是年
            boolean isMonth = ageStr.contains("个月") || ageStr.contains("月");
            double ageInYears = isMonth ? age / 12.0 : age;

            // 从配置读取年龄分界点
            double youngMaxAge = algorithmConfigService.getDoubleValue("CLUSTERING", "young_max_age", 1.0);
            double seniorMinAge = algorithmConfigService.getDoubleValue("CLUSTERING", "senior_min_age", 7.0);

            if (ageInYears < youngMaxAge) {
                return AGE_YOUNG;
            } else if (ageInYears >= seniorMinAge) {
                return AGE_SENIOR;
            } else {
                return AGE_ADULT;
            }
        }

        return AGE_ADULT; // 默认成年
    }

    /**
     * 根据体型获取聚类标签
     * 优先级：bodyType字段 > 根据品种推断
     */
    public String getSizeLabel(Animal animal) {
        // 优先使用bodyType字段
        if (animal.getBodyType() != null) {
            String bt = animal.getBodyType().trim();
            if (bt.contains(SIZE_SMALL)) return SIZE_SMALL;
            if (bt.contains(SIZE_MEDIUM)) return SIZE_MEDIUM;
            if (bt.contains(SIZE_LARGE)) return SIZE_LARGE;
        }

        // 根据品种和年龄推断体型
        String type = animal.getType();
        String age = animal.getAge();
        String ageStage = getAgeStage(age);

        return inferSizeByBreed(type, ageStage);
    }

    /**
     * 根据品种和年龄推断体型
     */
    private String inferSizeByBreed(String type, String ageStage) {
        if (type == null) return SIZE_SMALL;

        // 小型犬品种
        String[] smallDogs = {"泰迪", "比熊", "吉娃娃", "博美", "约克夏", "马尔济斯", "巴哥", "柯基", "法斗"};
        // 中型犬品种
        String[] mediumDogs = {"柴犬", "边境牧羊犬", "哈士奇", "萨摩耶", "松狮"};
        // 大型犬品种
        String[] largeDogs = {"金毛", "拉布拉多", "阿拉斯加", "德牧", "罗威纳", "藏獒", "大丹"};

        for (String breed : smallDogs) {
            if (type.contains(breed)) {
                return ageStage.equals(AGE_SENIOR) ? SIZE_MEDIUM : SIZE_SMALL;
            }
        }
        for (String breed : mediumDogs) {
            if (type.contains(breed)) {
                return SIZE_MEDIUM;
            }
        }
        for (String breed : largeDogs) {
            if (type.contains(breed)) {
                return ageStage.equals(AGE_YOUNG) ? SIZE_MEDIUM : SIZE_LARGE;
            }
        }

        // 默认为猫或其他小型宠物
        if (type.contains("猫") || type.toLowerCase().contains("cat")) {
            return SIZE_SMALL;
        }

        return SIZE_SMALL;
    }

    /**
     * 生成完整的聚类标签
     * 格式：{体型}{年龄阶段}{品种}
     * 例如：小型幼猫、中型成犬、大型老年犬
     */
    public String generateClusterTag(Animal animal) {
        String size = getSizeLabel(animal);
        String ageStage = getAgeStage(animal.getAge());
        String typeLabel = getTypeLabel(animal.getType());

        // 品种简称
        String breedShort = getBreedShortName(animal.getType(), typeLabel);

        return size + ageStage + breedShort;
    }

    /**
     * 获取品种简称
     */
    private String getBreedShortName(String type, String typeLabel) {
        if (type == null) return typeLabel;

        if (typeLabel.equals(TYPE_CAT)) {
            return "猫";
        } else if (typeLabel.equals(TYPE_DOG)) {
            return "犬";
        }
        return "宠";
    }

    /**
     * 获取体型对应的体重范围描述
     */
    public String getWeightRange(String sizeLabel) {
        double smallMax = getSmallMaxWeight();
        double mediumMax = getMediumMaxWeight();
        
        switch (sizeLabel) {
            case SIZE_SMALL:
                return "<" + smallMax + "kg";
            case SIZE_MEDIUM:
                return smallMax + "-" + mediumMax + "kg";
            case SIZE_LARGE:
                return ">" + mediumMax + "kg";
            default:
                return "未知";
        }
    }

    /**
     * 为单个宠物生成聚类标签（直接修改animal对象）
     */
    public void setClusterTag(Animal animal) {
        if (animal != null) {
            animal.setClusterTag(generateClusterTag(animal));
        }
    }

    /**
     * 为宠物列表批量生成聚类标签
     */
    public void setClusterTags(java.util.List<Animal> animals) {
        if (animals != null) {
            for (Animal animal : animals) {
                setClusterTag(animal);
            }
        }
    }

    /**
     * 获取所有聚类标签选项（用于前端筛选）
     */
    public Map<String, Object> getClusterOptions() {
        Map<String, Object> options = new HashMap<>();

        // 体型选项
        options.put("sizes", new String[]{SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE});

        // 年龄阶段选项
        options.put("ages", new String[]{AGE_YOUNG, AGE_ADULT, AGE_SENIOR});

        // 品种类型选项
        options.put("types", new String[]{TYPE_CAT, TYPE_DOG, TYPE_OTHER});

        // 体型-年龄阶段组合（常见组合）
        java.util.List<String[]> combinations = new java.util.ArrayList<>();
        for (String size : new String[]{SIZE_SMALL, SIZE_MEDIUM, SIZE_LARGE}) {
            for (String age : new String[]{AGE_YOUNG, AGE_ADULT, AGE_SENIOR}) {
                combinations.add(new String[]{size, age});
            }
        }
        options.put("sizeAgeCombinations", combinations);

        return options;
    }
}
