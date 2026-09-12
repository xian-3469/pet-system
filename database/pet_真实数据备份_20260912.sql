/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : localhost:3306
 Source Schema         : pet

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 12/09/2026 11:15:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时间',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面',
  `num` int(11) NULL DEFAULT 0 COMMENT '报名人数',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of activity
-- ----------------------------
INSERT INTO `activity` VALUES (6, '爱宠活动 | 共享快乐时光', '亲爱的狗狗爱好者们，我们诚挚邀请您参加我们举办的【欢乐狗狗嘉年华】活动！这将是一场充满乐趣和欢笑的聚会，让您的狗狗们度过一个难忘的时光。\n\n活动当天，您的狗狗将与众多可爱的小伙伴一起参加各类游戏和挑战。我们将设立专业游戏区域，供狗狗们展示智力、灵敏度和协作能力。还有缤纷的美食摊位，提供各种对狗狗友好的美食。\n\n此外，我们准备了精彩的表演节目，包括狗狗才艺秀、时尚模特走秀等等。您将有机会展示您和狗狗的默契和特殊技能。\n\n无论是参与游戏、观看表演，还是与其他狗狗爱好者进行交流和分享，这一天都将充满欢乐和友情。我们相信，通过这次活动，您和您的狗狗将建立更深厚的情感纽带，同时也会结识到更多热爱狗狗的朋友。\n\n让我们一起加入【欢乐狗狗嘉年华】，共享快乐时光，为我们可爱的伙伴们创造美好的回忆吧！', '2024-02-24 13:30:59', '/file/fcf2eb6127a141e7bb3e43288980b9da.png', 15, '广州市番禺区');
INSERT INTO `activity` VALUES (7, '🐾 萌宠嘉年华 —— 宠物运动会', '活动时间：2026年5月1日-3日 9:00-17:00\r\n\r\n活动内容：\r\n• 狗狗百米赛跑\r\n• 猫咪敏捷比赛\r\n• 宠物才艺表演\r\n• 萌宠时装秀\r\n• 免费体检咨询\r\n\r\n参赛要求：\r\n• 宠物年龄6个月以上\r\n• 已完成疫苗接种\r\n• 身体健康、性格温顺\r\n\r\n丰厚奖品：\r\n一等奖：价值2000元宠物大礼包\r\n二等奖：价值1000元宠物用品\r\n三等奖：精美宠物玩具套装\r\n\r\n报名即送萌宠礼包一份！', '2026-05-01 至 2026-05-03', '/file/6a099abc4e8b4cd8a1b1aaf45141d7d1.png', 128, '杭州国际会展中心');
INSERT INTO `activity` VALUES (8, '❤️ 流浪动物爱心领养日', '活动主题：用爱为流浪毛孩子找到温暖的家\r\n\r\n活动时间：2026年4月每周六 10:00-16:00\r\n\r\n活动亮点：\r\n• 30+只待领养宠物现场展示\r\n• 专业兽医现场义诊\r\n• 领养流程咨询\r\n• 宠物用品爱心义卖\r\n• 志愿者招募\r\n\r\n领养须知：\r\n• 年满18周岁，有稳定住所\r\n• 家人一致同意领养\r\n• 承诺科学喂养、定期体检\r\n• 接受回访，不离不弃\r\n\r\n所有领养宠物均已绝育、驱虫、接种疫苗', '2026-04-26', '/file/a197a07ed33b41d4ba519a0daa85b620.png', 89, '各区县宠物救助站');
INSERT INTO `activity` VALUES (9, '🏥 春季宠物健康义诊公益活动', '活动内容：\r\n• 免费基础体检（体重、牙齿、皮肤检查）\r\n• 疫苗接种优惠（8折）\r\n• 驱虫药特惠购\r\n• 养宠知识讲座\r\n\r\n义诊时间：2026年4月27日-30日\r\n\r\n合作医院：\r\n全市20家合作宠物医院同步开展\r\n\r\n参与方式：\r\n携带宠物到店即可享受服务\r\n无需预约，先到先得\r\n\r\n特别提示：\r\n• 请带好宠物疫苗本\r\n• 大型犬请系好牵引绳\r\n• 猫咪请装入便携笼', '2026-04-27 至 2026-04-30', '/file/03126de2ee824e53904367970304da8b.png', 256, '全市合作宠物医院');
INSERT INTO `activity` VALUES (10, '🎨 萌宠创意绘画大赛', '大赛主题：我心中的萌宠\r\n\r\n参赛对象：5-12岁儿童及亲子家庭\r\n\r\n活动形式：\r\n• 线上投稿：4月20日-5月10日\r\n• 线下作画：5月15日现场绘画\r\n\r\n作品要求：\r\n• 围绕宠物主题，材料不限\r\n• 体现人与动物的和谐友爱\r\n• 富有创意，童趣盎然\r\n\r\n奖项设置：\r\n金奖3名：1000元奖学金+荣誉证书\r\n银奖10名：500元文具卡+荣誉证书\r\n铜奖20名：200元礼品+荣誉证书\r\n优秀奖若干：精美礼品一份\r\n\r\n投稿邮箱：petart@petmanager.com', '2026-04-20 至 2026-05-15', '/file/386844f7a9634c179ccda3305885e1fc.png', 167, '线上+杭州美术馆');
INSERT INTO `activity` VALUES (11, '🍖 宠物美食DIY工作坊', '活动内容：\r\n教您亲手为爱宠制作健康美味的小零食！\r\n\r\n课程安排：\r\n• 第一课：鸡肉干制作\r\n• 第二课：狗狗生日蛋糕\r\n• 第三课：猫咪猫饭料理\r\n• 第四课：宠物饼干烘焙\r\n\r\n上课时间：每周日下午2:00-4:00\r\n\r\n费用包含：\r\n• 全部食材和工具\r\n• 专业老师指导\r\n• 成品可带走\r\n• 配方手册一份\r\n\r\n报名优惠：\r\n原价199元/课，首期体验价99元\r\n报名全部课程享6折优惠！\r\n\r\n小班授课，每期仅限10组家庭', '2026-04-26 起每周日', '/file/0f5917da669a4745a8c234984f8d0350.png', 45, '宠物生活馆 workshop区');
INSERT INTO `activity` VALUES (12, '📸 最美铲屎官摄影展', '活动主题：捕捉与宠物最温暖的瞬间\r\n\r\n征稿内容：\r\n• 宠物写真照\r\n• 人宠互动照\r\n• 宠物故事照片组\r\n\r\n投稿时间：2026年4月15日-5月20日\r\n\r\n作品要求：\r\n• 图片清晰，2MB以上\r\n• 原创作品，禁止抄袭\r\n• 附上简短文字说明\r\n\r\n线上投票：5月21日-28日\r\n结果公布：6月1日\r\n\r\n奖品：\r\n冠军1名：宠物摄影套餐（价值3000元）\r\n亚军3名：专业宠物写真（价值1000元）\r\n季军10名：宠物周边大礼包\r\n\r\n优秀作品将在线下商场公开展出', '2026-04-15 至 2026-06-01', '/file/4925c64ec4af4a30875176ab0c67fe97.png', 203, '线上征稿+线下巡展');

-- ----------------------------
-- Table structure for adopt
-- ----------------------------
DROP TABLE IF EXISTS `adopt`;
CREATE TABLE `adopt`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `animal_id` int(11) NOT NULL COMMENT '流浪动物id',
  `adopt_status` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '领养状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '领养状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of adopt
-- ----------------------------

-- ----------------------------
-- Table structure for algorithm_config
-- ----------------------------
DROP TABLE IF EXISTS `algorithm_config`;
CREATE TABLE `algorithm_config`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `algorithm_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '算法类型：CLUSTERING-聚类算法, ADOPT_MATCH-领养匹配算法, SERVICE_RECOMMENDATION-服务推荐算法',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置项键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置项值',
  `config_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'STRING' COMMENT '配置项类型：STRING-字符串, INTEGER-整数, DOUBLE-浮点数',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配置项描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_algorithm_key`(`algorithm_type`, `config_key`) USING BTREE,
  INDEX `idx_algorithm_type`(`algorithm_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '算法配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of algorithm_config
-- ----------------------------
INSERT INTO `algorithm_config` VALUES (1, 'CLUSTERING', 'small_max_weight', '10.0', 'DOUBLE', '小型宠物最大体重(kg)', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (2, 'CLUSTERING', 'medium_max_weight', '25.0', 'DOUBLE', '中型宠物最大体重(kg)', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (3, 'CLUSTERING', 'young_max_age', '1.0', 'DOUBLE', '幼年宠物最大年龄(岁)', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (4, 'CLUSTERING', 'senior_min_age', '7.0', 'DOUBLE', '老年宠物最小年龄(岁)', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (5, 'ADOPT_MATCH', 'weight_experience', '25', 'INTEGER', '养宠经验权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (6, 'ADOPT_MATCH', 'weight_housing', '20', 'INTEGER', '住房条件权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (7, 'ADOPT_MATCH', 'weight_income', '20', 'INTEGER', '收入水平权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (8, 'ADOPT_MATCH', 'weight_family', '15', 'INTEGER', '家庭结构权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (9, 'ADOPT_MATCH', 'weight_body_type', '10', 'INTEGER', '体型匹配权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (10, 'ADOPT_MATCH', 'weight_age', '10', 'INTEGER', '年龄匹配权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (11, 'ADOPT_MATCH', 'income_high', '15000', 'INTEGER', '高收入阈值(元)', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (12, 'ADOPT_MATCH', 'income_medium', '8000', 'INTEGER', '中等收入阈值(元)', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (13, 'ADOPT_MATCH', 'income_low', '4000', 'INTEGER', '低收入阈值(元)', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (14, 'SERVICE_RECOMMENDATION', 'rating_weight', '0.4', 'DOUBLE', '评分权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (15, 'SERVICE_RECOMMENDATION', 'order_count_weight', '0.3', 'DOUBLE', '订单量权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (16, 'SERVICE_RECOMMENDATION', 'recency_weight', '0.2', 'DOUBLE', '新鲜度权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (17, 'SERVICE_RECOMMENDATION', 'review_count_weight', '0.1', 'DOUBLE', '评价数量权重', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (18, 'SERVICE_RECOMMENDATION', 'recency_days_7', '100.0', 'DOUBLE', '7天内新鲜度得分', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (19, 'SERVICE_RECOMMENDATION', 'recency_days_30', '80.0', 'DOUBLE', '30天内新鲜度得分', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (20, 'SERVICE_RECOMMENDATION', 'recency_days_90', '60.0', 'DOUBLE', '90天内新鲜度得分', '2026-05-10 09:10:37', '2026-05-10 09:10:37');
INSERT INTO `algorithm_config` VALUES (21, 'SERVICE_RECOMMENDATION', 'recency_days_other', '40.0', 'DOUBLE', '其他时间新鲜度得分', '2026-05-10 09:10:37', '2026-05-10 09:10:37');

-- ----------------------------
-- Table structure for animal
-- ----------------------------
DROP TABLE IF EXISTS `animal`;
CREATE TABLE `animal`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '动物名字',
  `sex` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '动物性别',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '种类',
  `age` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '年龄',
  `body_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '体型：小型/中型/大型',
  `img` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '动物照片',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '活动范围',
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '身体状态',
  `sterilization` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '是否绝育',
  `vaccine` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '疫苗接种',
  `adopt` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '领养状态',
  `information` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '其他描述',
  `personality` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '性格：温顺/活泼/独立/粘人',
  `cluster_tag` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '聚类标签，如：小型幼猫、中型成犬',
  `is_adopt` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '是否被领养',
  `praise` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '流浪动物表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of animal
-- ----------------------------
INSERT INTO `animal` VALUES (1, '小橘', '公', '猫', '6个月', '小型', '/file/f4fe265f77744fca80750a8992942c37.png', '广州市天河区公园', '健康', '已绝育', '已接种', '可领养', '性格温顺亲人，喜欢撒娇', '温顺', '小型幼年猫', '否', 0);
INSERT INTO `animal` VALUES (2, '豆豆', '母', '狗', '2岁', '中型', '/file/7b5dcdffdb414cddaf2d45c8241beef8.png', '广州市白云区小区', '健康', '已绝育', '已接种', '可领养', '活泼好动，需要有院子', '活泼', '中型成年犬', '否', 0);
INSERT INTO `animal` VALUES (3, '咪咪', '母', '猫', '1岁', '小型', '/file/22a3694761474590be5705fa6f614ef7.png', '广州市越秀区老城区', '健康', '已绝育', '已接种', '不可领养', '独立自主，适合上班族', '独立', '小型成年猫', '是', 0);
INSERT INTO `animal` VALUES (4, '旺财', '公', '狗', '3岁', '大型', '/file/a38e04f190454a1397e481c93f0084c9.png', '广州市番禺区郊区', '健康', '已绝育', '已接种', '可领养', '忠诚护主，需要有经验的主人', '温顺', '大型成年犬', '否', 0);
INSERT INTO `animal` VALUES (5, '小白', '母', '猫', '8个月', '小型', '/file/07487468e70346d3abefee5f6188cca1.png', '广州市海珠区商业街', '健康', '未绝育', '已接种', '不可领养', '粘人爱撒娇', '粘人', '小型幼年猫', '是', 0);

-- ----------------------------
-- Table structure for applcation
-- ----------------------------
DROP TABLE IF EXISTS `applcation`;
CREATE TABLE `applcation`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `animal_id` int(11) NOT NULL COMMENT '动物ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '申请人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '领养理由',
  `state` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '待审核' COMMENT '状态',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `sex` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
  `age` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '年龄',
  `experience` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '养宠经验',
  `pet` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '现有宠物',
  `married` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '婚姻状况',
  `income` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收入水平',
  `housing` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '住房条件：公寓/别墅/其他',
  `family_structure` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '家庭结构：单身/二人世界/三口之家/大家庭',
  `profession` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '职业',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '住址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_animal_id`(`animal_id`) USING BTREE,
  INDEX `idx_state`(`state`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '领养申请表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of applcation
-- ----------------------------
INSERT INTO `applcation` VALUES (10, 7, 5, '小王', '13800001111', '我很喜欢猫咪，希望能领养一只陪伴我', '审核通过', '2026-04-29 18:37:21', '男', '25', '无经验', NULL, '未婚', '5000', '公寓', '单身', '设计师', '长沙市岳麓区');
INSERT INTO `applcation` VALUES (11, 8, 3, 'MM', '13800002222', '家里有小孩子，想养一只温顺的狗狗陪伴孩子成长', '审核通过', '2026-04-29 18:37:21', '女', '32', '有经验', '有狗', '已婚', '8000', '公寓', '三口之家', '美容师', '广州市天河区');

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发布时间',
  `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发布人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (1, '🐱 领养代替购买，给流浪猫一个温暖的家', '<p>今天我想和大家分享一个重要的话题：<strong>领养代替购买</strong>。</p><p>在这个城市里，有太多流浪的小动物在等待一个温暖的家。我家的猫咪\"橘子\"就是去年从流浪动物救助站领养的。</p><p>它刚来的时候瘦骨嶙峋，现在已经是10斤的小胖猪了！🐾</p><p>如果你也想要一个毛孩子，请考虑去当地的救助站看看吧，说不定你的命中注定就在那里等你。</p><p><strong>领养流程：</strong></p><p>1. 前往救助站选择心仪的宠物</p><p>2. 填写领养申请表</p><p>3. 等待审核（约3-5个工作日）</p><p>4. 签署领养协议</p><p>5. 带毛孩子回家！</p>', 7, '2026-04-15 10:30:00', 'user1');
INSERT INTO `article` VALUES (2, '🐶 狗狗第一次洗澡需要注意什么？', '<p>第一次带狗狗去宠物店洗澡，很多新手铲屎官都会紧张。今天来分享一些经验！</p><p>1️⃣ <strong>提前适应</strong>：去之前可以让狗狗闻闻宠物店的味道，不要太急躁</p><p>2️⃣ <strong>选择合适的时间</strong>：不要在狗狗刚吃完饭或非常疲惫的时候去</p><p>3️⃣ <strong>提前告知</strong>：告诉美容师这是狗狗第一次洗澡，有些店会特别照顾</p><p>4️⃣ <strong>准备好零食</strong>：奖励表现好的地方</p><p>5️⃣ <strong>不要离开太久</strong>：第一次可以在一旁陪着它</p><p>我家旺财第一次洗澡全程超级乖，还得到了美容师的夸奖呢！😄</p>', 8, '2026-04-18 14:20:00', 'user2');
INSERT INTO `article` VALUES (3, '🐰 兔兔饲养指南，新手必看！', '<p>很多朋友问我兔兔好不好养，今天来详细说说！</p><p>🐇 <strong>饮食方面</strong></p><p>- 干草占饮食的80%（提摩西草最佳）</p><p>- 新鲜蔬菜每天适量</p><p>- 专业兔粮少量</p><p>- 无限量清水</p><p>🐇 <strong>居住环境</strong></p><p>- 宽敞的活动空间</p><p>- 保持干燥清洁</p><p>- 提供磨牙玩具</p><p>🐇 <strong>注意事项</strong></p><p>- 不要给兔子洗澡！</p><p>- 定期检查牙齿</p><p>- 适龄绝育很重要</p><p>兔兔其实是很爱干净的动物，会自己清理毛发。只要科学喂养，它们可以活8-12年哦！</p>', 9, '2026-04-20 09:15:00', 'user3');
INSERT INTO `article` VALUES (4, '🐕 遛狗时遇到不牵绳的狗怎么办？', '<p>今天出门遛狗时遇到了一只没牵绳的大狗，差点出事。想和大家讨论一下这个话题。</p><p>遇到这种情况，我的经验是：</p><p>1️⃣ 保持冷静，不要大声尖叫</p><p>2️⃣ 尽量把自己的狗拉到身后</p><p>3️⃣ 用身体挡在两只狗之间</p><p>4️⃣ 可以用零食分散注意力</p><p>5️⃣ 必要时向对方主人喊话要求牵绳</p><p>💡 <strong>建议</strong></p><p>- 选择相对僻静的遛狗路线</p><p>- 随身携带驱狗喷雾（以防万一）</p><p>- 训练狗狗\"回来\"的指令</p><p>文明养犬，从我做起。希望大家遛狗都牵绳！</p>', 10, '2026-04-22 18:45:00', 'user4');
INSERT INTO `article` VALUES (5, '🐱 猫咪突然挑食怎么办？', '<p>我家猫咪最近突然不爱吃猫粮了，可急死我了！后来经过一番研究，终于找到了原因和解决办法。</p><p>🔍 <strong>可能的原因</strong></p><p>- 换季食欲下降（正常现象）</p><p>- 口腔问题（需要检查）</p><p>- 猫粮不新鲜</p><p>- 吃太多零食/罐头</p><p>- 心情不好或环境变化</p><p>✅ <strong>我的解决方案</p><p>1. 更换新鲜的猫粮</p><p>2. 减少零食罐头供应</p><p>3. 尝试不同品牌的猫粮</p><p>4. 加热食物增加香味</p><p>5. 增加互动游戏时间</p><p>现在我家猫咪已经恢复正常食欲啦！如果你家猫咪也有类似问题，可以试试这些方法哦～</p>', 11, '2026-04-24 11:00:00', 'user5');
INSERT INTO `article` VALUES (6, '🐾 宠物保险到底值不值得买？', '<p>最近给毛孩子买了宠物保险，来聊聊我的看法。</p><p>💰 <strong>费用参考</strong></p><p>- 基础版：约200-500元/年</p><p>- 中档版：约500-1000元/年</p><p>- 高端版：1000元以上/年</p><p>🏥 <strong>报销范围</strong></p><p>- 意外伤害</p><p>- 常见疾病</p><p>- 手术费用</p><p>- 住院费用</p><p>📊 <strong>我的建议</strong></p><p>如果你家宠物是以下情况，建议购买：</p><p>✅ 纯种宠物（遗传病风险较高）</p><p>✅ 年纪较大的宠物</p><p>✅ 活泼好动容易受伤的</p><p>如果是健康、年轻的混血宠物，可以先观望。</p><p>总的来说，保险是买个安心，希望大家都用不上！🐾</p>', 7, '2026-04-25 16:30:00', 'user1');
INSERT INTO `article` VALUES (7, '🐱 猫咪真的会记住自己的名字吗？', '<p>今天看到一个有趣的研究，说猫咪其实是可以记住自己名字的，只是大多数时候选择性地\"听不懂\"。</p><p>我家那只橘猫就是典型代表：叫它名字时，它耳朵会动一下，但身体纹丝不动。有时候真的怀疑它是不是在装聋...</p><p>不过有研究表明，猫咪确实能够区分自己的名字和其他单词。当它们听到自己的名字时，瞳孔会微微放大，这是无意识的反应哦！</p><p>所以各位铲屎官们，不要灰心！你的猫主子其实是认识你的，只是...</p><p>人家就是不想理你而已 🐱😏</p>', 8, '2026-04-26 08:30:00', 'user2');
INSERT INTO `article` VALUES (8, '🐶 盘点我家狗子的\"犯罪现场\"，笑死我了！', '<p>养狗之前：我要天天rua狗！</p><p>养狗之后：我的沙发呢？我的拖鞋呢？我的...我的花盆呢？！</p><p>今天来盘点一下我家二哈的\"辉煌战绩\"：</p><p>1️⃣ <strong>卷纸</strong>：满屋子都是纸屑，像下了一场雪</p><p>2️⃣ <strong>拖鞋</strong>：只剩下一只，另一只在狗窝里找到</p><p>3️⃣ <strong>遥控器</strong>：电池盖不见了，按键全被咬掉</p><p>4️⃣ <strong>沙发垫</strong>：被掏空，只剩下外壳</p><p>每次看它一脸无辜的样子，又气又想笑...</p><p>你们家狗狗有没有类似的\"犯罪记录\"？评论区来聊聊！😂</p>', 9, '2026-04-27 14:20:00', 'user3');
INSERT INTO `article` VALUES (9, '📝 第一次把狗狗送去寄养，这些经验分享给你！', '<p>因为工作原因要出差一周，不得已把毛孩子送去寄养。分享一下我的经验和注意事项，供大家参考~</p><p>🏠 <strong>选择寄养机构</strong></p><p>- 一定要实地考察，看看环境是否干净</p><p>- 观察工作人员是否专业、有爱心</p><p>- 最好选择有监控可以实时查看的</p><p>📋 <strong>准备物品</strong></p><p>- 带上狗狗平时用的小毯子，有熟悉的气味</p><p>- 准备专属狗粮，避免换粮引起肠胃不适</p><p>- 带上它喜欢的玩具</p><p>- 准备一份详细的喂养说明</p><p>⚠️ <strong>注意事项</strong></p><p>- 确认狗狗疫苗齐全</p><p>- 提前让狗狗适应一下环境</p><p>- 留下紧急联系方式</p><p>总体来说这次寄养体验还不错，每天都能看到狗狗的视频，算是安心了~</p>', 10, '2026-04-28 09:15:00', 'user4');
INSERT INTO `article` VALUES (10, '🍖 在家也能做的宠物零食，简单又健康！', '<p>今天分享几款在家就能做的宠物零食，安全健康，食材简单~</p><p>🐔 <strong>鸡肉干</strong></p><p>材料：鸡胸肉</p><p>做法：切成薄片，烤箱160度烤20分钟，翻面再烤10分钟</p><p>✅ 无添加，训练奖励必备！</p><p>🥕 <strong>胡萝卜干</strong></p><p>材料：胡萝卜</p><p>做法：切成薄片，烤箱100度烤1小时</p><p>✅ 低热量，富含维生素</p><p>🍠 <strong>紫薯片</strong></p><p>材料：紫薯</p><p>做法：蒸熟压扁，烤箱120度烤30分钟</p><p>✅ 颜色好看，富含花青素</p><p>⚠️ 注意：</p><p>- 不要加任何调味料</p><p>- 根据宠物大小调整零食大小</p><p>- 观察是否有过敏反应</p><p>自己做的零食，吃得放心！大家还有什么好配方，欢迎分享~</p>', 11, '2026-04-29 11:00:00', 'user5');

-- ----------------------------
-- Table structure for article_kp
-- ----------------------------
DROP TABLE IF EXISTS `article_kp`;
CREATE TABLE `article_kp`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时间',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面',
  `read1` int(11) NULL DEFAULT 0 COMMENT '阅读数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识科普文章表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of article_kp
-- ----------------------------
INSERT INTO `article_kp` VALUES (7, '狗狗：人类最忠实的朋友', '狗狗是人类最受欢迎的宠物之一，被誉为人类最忠实的朋友。它们以各种品种的形式存在，从小型狗到大型犬，从拥有长毛、短毛或无毛的外观，到拥有不同的颜色和花纹。\n\n狗狗是社交性动物，非常喜欢与家人互动和结交新朋友。它们可以成为温柔、忠诚和无条件爱的化身。并且，许多狗狗还能执行各种任务，例如导盲犬、搜救犬和警犬等。\n\n拥有狗狗需要投入时间和精力来满足它们的需求。定期的锻炼、散步和游戏可以保持它们健康和快乐。此外，提供适当的饮食、良好的卫生环境和定期的兽医检查对于维持狗狗的健康也至关重要。\n\n作为宠物主人，我们需要给予狗狗关爱、尊重和培养良好的行为习惯。通过训练和社交化，我们可以帮助它们成为社会上守法、友好和适应各种环境的成员。\n\n与狗狗相处将给我们带来许多快乐、温暖和无尽的爱。无论是健康时还是当它们需要特别关注和护理时，狗狗都会一直陪伴在我们身边。\n\n与狗狗的共处将极大地丰富我们的生活，让我们成为更好的人类。带给它们幸福，我们也将从中获得回报，一起度过美好的时光。', '2023-12-04 13:17:51', '/file/8ab488d9179b4b8ca1b146193a1e1d18.png', 21);
INSERT INTO `article_kp` VALUES (8, '🐶 狗狗疫苗接种全攻略，新手必看！', '一、疫苗接种时间表\r\n1. 6-8周龄：首次接种犬瘟热、犬细小病毒联合疫苗\r\n2. 10-12周龄：第二次接种（加强针）\r\n3. 14-16周龄：第三次接种+狂犬疫苗\r\n4. 之后每年加强一次\r\n\r\n二、接种注意事项\r\n• 接种前确保狗狗身体健康，无发热、咳嗽等症状\r\n• 接种后观察30分钟再离开\r\n• 接种后一周内不要洗澡，避免剧烈运动\r\n• 部分狗狗可能出现轻微嗜睡食欲减退，属正常反应\r\n\r\n三、常见疫苗种类\r\n• 核心疫苗：犬瘟热、犬细小病毒、狂犬病（必打）\r\n• 非核心疫苗：钩端螺旋体、犬窝咳等（根据情况选择）', '2026-04-20', '/file/e295b608a85c472c935a322aa0a4dc9e.png', 1257);
INSERT INTO `article_kp` VALUES (9, '🐱 猫咪日常护理小技巧，让你的猫主子更健康', '一、猫咪清洁护理\r\n1. 毛发梳理：短毛猫每周1-2次，长毛猫每日梳理\r\n2. 指甲修剪：每2-3周修剪一次，仅剪尖端透明部分\r\n3. 耳朵清洁：每月用专用清洁液清理一次\r\n4. 牙齿护理：每周刷牙2-3次，预防牙结石\r\n\r\n二、饮食注意事项\r\n• 选择优质猫粮，注意查看成分表\r\n• 保持饮水充足，可放置多个水碗\r\n• 避免喂食人类食物，特别是巧克力、洋葱等\r\n• 定时定量喂食，控制体重\r\n\r\n三、环境布置\r\n• 准备猫抓板，减少抓挠家具\r\n• 提供猫爬架，满足攀爬天性\r\n• 保持猫砂盆清洁，每日清理', '2026-04-18', '/file/55ee651f0c3947f7a9c194aff8e88a05.png', 982);
INSERT INTO `article_kp` VALUES (10, '🐰 兔子饲养指南：营造舒适的居住环境', '一、居住环境要求\r\n• 兔笼要足够大，至少是兔子体长的4倍\r\n• 底部铺上柔软垫料，保护兔兔脚底\r\n• 保持通风干燥，避免阳光直射\r\n• 室温控制在15-25度为宜\r\n\r\n二、科学喂养\r\n• 主食：提摩西草无限量供应\r\n• 辅食：专用兔粮每日适量\r\n• 零食：少量蔬菜水果（洗净沥干）\r\n• 饮水：新鲜白开水每日更换\r\n\r\n三、健康预警信号\r\n• 食欲下降或不吃东西\r\n• 粪便变小、变硬或腹泻\r\n• 精神萎靡、躲藏不动\r\n• 眼睛分泌物增多、流鼻涕\r\n发现异常请及时就医！', '2026-04-15', '/file/c1c01c1565bd4d67bd2c43d5dfd2802d.png', 757);
INSERT INTO `article_kp` VALUES (11, '🦜 鸟类宠物养护：鹦鹉的日常饲养要点', '一、鹦鹉品种选择\r\n常见宠物鹦鹉：虎皮鹦鹉、玄凤鹦鹉、牡丹鹦鹉、葵花鹦鹉、金刚鹦鹉等\r\n新手推荐从虎皮鹦鹉开始，饲养难度低，互动性好\r\n\r\n二、饮食管理\r\n• 主食：混合谷物种子\r\n• 营养补充：墨鱼骨、矿物质块\r\n• 新鲜蔬果：苹果、胡萝卜、青菜等\r\n• 禁忌食物：牛油果、巧克力、咖啡因\r\n\r\n三、日常互动\r\n• 每天至少1-2小时陪伴时间\r\n• 进行简单指令训练（上手、说话等）\r\n• 提供玩具丰富生活，避免抑郁\r\n• 保持笼舍清洁，定期消毒', '2026-04-12', '/file/262806c7620742e781e16f674f65e330.png', 543);
INSERT INTO `article_kp` VALUES (12, '🐹 仓鼠饲养入门：打造温馨小窝', '一、笼舍选择\r\n• 仓鼠笼至少60cm x 40cm\r\n• 跑轮直径22cm以上\r\n• 多层别墅提供更多活动空间\r\n• 垫料选择纸质垫料或木屑\r\n\r\n二、饮食推荐\r\n• 主食：仓鼠专用粮\r\n• 辅食：小米、燕麦、少量坚果\r\n• 零食：新鲜蔬菜水果（小块）\r\n• 禁忌：柑橘类、洋葱、大蒜\r\n\r\n三、互动技巧\r\n• 刚到家先让仓鼠适应环境\r\n• 用零食建立信任\r\n• 避免在仓鼠睡觉时打扰\r\n• 不要用力抓握，轻柔对待\r\n\r\n四、繁殖注意\r\n• 仓鼠为独居动物，一鼠一笼\r\n• 怀孕期间提供安静环境\r\n• 产后不要触碰幼崽', '2026-04-10', '/file/936aa6abb1fe42ecbe033e28bf8726de.png', 698);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复内容',
  `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复人',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回复时间',
  `pid` int(11) NULL DEFAULT NULL COMMENT '父id',
  `article_id` int(11) NULL DEFAULT NULL COMMENT '文章id',
  `type` int(11) NULL DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, '说得好！支持领养！', 'user2', '2026-04-15 11:00:00', NULL, 1, 2);
INSERT INTO `comment` VALUES (2, '我家也是领养的，真的很乖，感谢救助站', 'user3', '2026-04-15 12:30:00', NULL, 1, 2);
INSERT INTO `comment` VALUES (3, '领养前建议先了解家里的环境是否适合养宠物', 'user4', '2026-04-15 14:00:00', NULL, 1, 2);
INSERT INTO `comment` VALUES (4, '回复user2：救助站的工作人员真的很热心，教会了我很多养宠知识', 'user1', '2026-04-15 15:30:00', 2, 1, 2);
INSERT INTO `comment` VALUES (5, '我家那只刚来的时候超凶，现在黏人得不行~', 'user5', '2026-04-16 09:00:00', NULL, 1, 2);
INSERT INTO `comment` VALUES (6, '回复user5：哈哈哈，流浪过的猫咪会更珍惜有人对它好', 'user3', '2026-04-16 10:30:00', 5, 1, 2);
INSERT INTO `comment` VALUES (7, '太有用了！我家狗子下周第一次洗澡', 'user1', '2026-04-18 15:00:00', NULL, 2, 2);
INSERT INTO `comment` VALUES (8, '我家金毛第一次洗澡吓尿了😂', 'user3', '2026-04-18 16:30:00', NULL, 2, 2);
INSERT INTO `comment` VALUES (9, '回复user3：哈哈哈，金毛胆子大但是怕水是出了名的', 'user2', '2026-04-18 17:00:00', 2, 2, 2);
INSERT INTO `comment` VALUES (10, '建议选择下午时段，狗狗精力消耗后会更配合', 'user4', '2026-04-19 09:00:00', NULL, 2, 2);
INSERT INTO `comment` VALUES (11, '美容师的选择也很重要！要有耐心的那种', 'user5', '2026-04-19 11:00:00', NULL, 2, 2);
INSERT INTO `comment` VALUES (12, '兔兔真的好可爱！但是不能洗澡这个我以前真的不知道', 'user1', '2026-04-20 10:00:00', NULL, 3, 2);
INSERT INTO `comment` VALUES (13, '回复user1：兔子真的很爱干净，会自己舔毛清洁', 'user3', '2026-04-20 11:00:00', 1, 3, 2);
INSERT INTO `comment` VALUES (14, '我家兔兔活了9年，按照这个方法养的，非常健康！', 'user2', '2026-04-20 14:00:00', NULL, 3, 2);
INSERT INTO `comment` VALUES (15, '提摩西草哪里买比较便宜呀？', 'user4', '2026-04-21 09:00:00', NULL, 3, 2);
INSERT INTO `comment` VALUES (16, '回复user4：网上有很多宠物用品店都有，我一般买大包装的更划算', 'user5', '2026-04-21 10:30:00', 4, 3, 2);
INSERT INTO `comment` VALUES (17, '太危险了！遇到不牵绳的一定要报警', 'user1', '2026-04-22 19:00:00', NULL, 4, 2);
INSERT INTO `comment` VALUES (18, '我家也是，每次都很紧张', 'user3', '2026-04-22 20:00:00', NULL, 4, 2);
INSERT INTO `comment` VALUES (19, '驱狗喷雾真的有用！我包里一直备着', 'user5', '2026-04-23 08:00:00', NULL, 4, 2);
INSERT INTO `comment` VALUES (20, '回复user5：什么牌子的推荐一下呗', 'user4', '2026-04-23 09:30:00', 3, 4, 2);
INSERT INTO `comment` VALUES (21, '回复user4：我用的NBS防狗喷雾，很温和但有效', 'user5', '2026-04-23 10:00:00', 4, 4, 2);
INSERT INTO `comment` VALUES (22, '希望出台更严格的遛狗不牵绳处罚规定！', 'user2', '2026-04-23 14:00:00', NULL, 4, 2);
INSERT INTO `comment` VALUES (23, '我家猫也是！后来发现是猫粮放太久不新鲜了', 'user2', '2026-04-24 12:00:00', NULL, 5, 2);
INSERT INTO `comment` VALUES (24, '加热食物这个方法真的有用！', 'user4', '2026-04-24 13:30:00', NULL, 5, 2);
INSERT INTO `comment` VALUES (25, '是不是该换粮了？有些猫粮吃久了会腻', 'user3', '2026-04-24 15:00:00', NULL, 5, 2);
INSERT INTO `comment` VALUES (26, '回复user3：可以考虑7日换粮法，逐渐过渡', 'user5', '2026-04-24 16:00:00', 3, 5, 2);
INSERT INTO `comment` VALUES (27, '我家的挑食就给它加点罐头，秒变干饭猫', 'user1', '2026-04-25 09:00:00', NULL, 5, 2);
INSERT INTO `comment` VALUES (28, '买过，赔付速度挺快的，就是流程稍微复杂', 'user2', '2026-04-25 17:00:00', NULL, 6, 2);
INSERT INTO `comment` VALUES (29, '纯种猫真的要注意！我家布偶上次生病花了5000多', 'user3', '2026-04-25 18:30:00', NULL, 6, 2);
INSERT INTO `comment` VALUES (30, '回复user3：所以保险还是有必要的，防患于未然', 'user1', '2026-04-25 19:00:00', 2, 6, 2);
INSERT INTO `comment` VALUES (31, '有没有推荐的保险品牌呀？', 'user4', '2026-04-26 09:00:00', NULL, 6, 2);
INSERT INTO `comment` VALUES (32, '回复user4：我用的是支付宝的宠物医疗险还不错', 'user5', '2026-04-26 10:00:00', 4, 6, 2);
INSERT INTO `comment` VALUES (33, '我家猫也是！叫名字完全不理，但一开罐头就秒到', 'user1', '2026-04-26 09:00:00', NULL, 7, 2);
INSERT INTO `comment` VALUES (34, '哈哈哈，这研究太真实了！我家猫也是选择性耳聋', 'user3', '2026-04-26 09:30:00', NULL, 7, 2);
INSERT INTO `comment` VALUES (35, '回复user1：同款猫！开罐头的声音比叫名字管用一百倍', 'user4', '2026-04-26 10:00:00', 1, 7, 2);
INSERT INTO `comment` VALUES (36, '我家橘猫不但记不住名字，连自己的饭盆都不认识', 'user5', '2026-04-26 11:00:00', NULL, 7, 2);
INSERT INTO `comment` VALUES (37, '其实猫是可以记住的，只是看它心情要不要理你', 'user2', '2026-04-26 12:00:00', NULL, 7, 2);
INSERT INTO `comment` VALUES (38, '二哈果然名不虚传！哈哈哈哈', 'user1', '2026-04-27 15:00:00', NULL, 8, 2);
INSERT INTO `comment` VALUES (39, '我家金毛把一整卷纸巾全部撕碎，客厅像暴风雪过境', 'user3', '2026-04-27 15:30:00', NULL, 8, 2);
INSERT INTO `comment` VALUES (40, '回复user3：金毛：我不是故意的...（但我下次还敢）', 'user5', '2026-04-27 16:00:00', 2, 8, 2);
INSERT INTO `comment` VALUES (41, '我家狗子最厉害的一次：把门啃了一个洞，就为了看我出门没', 'user2', '2026-04-27 17:00:00', NULL, 8, 2);
INSERT INTO `comment` VALUES (42, '每次生气看到这张无辜脸就消气了，气死了', 'user4', '2026-04-27 18:00:00', NULL, 8, 2);
INSERT INTO `comment` VALUES (43, '写得真详细！我上次寄养踩坑了，环境很差', 'user2', '2026-04-28 10:00:00', NULL, 9, 2);
INSERT INTO `comment` VALUES (44, '请问有没有推荐的寄养平台？', 'user5', '2026-04-28 10:30:00', NULL, 9, 2);
INSERT INTO `comment` VALUES (45, '回复user5：我用的宠友app还不错，有评价系统', 'user1', '2026-04-28 11:00:00', 2, 9, 2);
INSERT INTO `comment` VALUES (46, '其实让朋友帮忙照顾也挺好的，就是欠人情', 'user3', '2026-04-28 12:00:00', NULL, 9, 2);
INSERT INTO `comment` VALUES (47, '鸡肉干这个简单！我家狗子超爱吃', 'user1', '2026-04-29 11:30:00', NULL, 10, 2);
INSERT INTO `comment` VALUES (48, '请问可以用微波炉做吗？家里没有烤箱', 'user4', '2026-04-29 12:00:00', NULL, 10, 2);
INSERT INTO `comment` VALUES (49, '回复user4：可以的，但是要注意火候，每30秒翻一次', 'user3', '2026-04-29 12:30:00', 2, 10, 2);
INSERT INTO `comment` VALUES (50, '我做过鸡肝粉，狗狗也超喜欢，就是味道有点上头', 'user2', '2026-04-29 13:00:00', NULL, 10, 2);
INSERT INTO `comment` VALUES (51, '太实用了！周末试试给猫咪做', 'user5', '2026-04-29 14:00:00', NULL, 10, 2);
INSERT INTO `comment` VALUES (52, '猫咪挑食真的头疼！我试过换粮、加罐头、加鸡胸肉，换了无数方法才解决', 'user2', '2026-04-27 10:00:00', NULL, 1, 2);
INSERT INTO `comment` VALUES (53, '回复user2：加鸡胸肉真的有用！我家猫挑食三个月，换了鸡胸肉秒变干饭猫', 'user5', '2026-04-27 11:00:00', 1, 1, 2);
INSERT INTO `comment` VALUES (54, '我家那只挑食是因为零食喂多了，后来断了零食才恢复正常的', 'user3', '2026-04-27 12:00:00', NULL, 5, 2);
INSERT INTO `comment` VALUES (55, '加热食物这个方法太棒了！我家猫闻到热猫粮的味道就冲过来了', 'user1', '2026-04-28 09:00:00', NULL, 5, 2);
INSERT INTO `comment` VALUES (56, '真好吃', '爱猫人士小王', '2026-05-22 21:42:32', NULL, 10, 2);
INSERT INTO `comment` VALUES (57, '真好', '爱猫人士小王', '2026-05-22 21:51:44', NULL, 8, 2);

-- ----------------------------
-- Table structure for feed
-- ----------------------------
DROP TABLE IF EXISTS `feed`;
CREATE TABLE `feed`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '喂食点ID',
  `img` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '环境照片1',
  `img2` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '环境照片2',
  `img3` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '环境照片3',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `information` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '相关描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '流浪动物喂食点表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of feed
-- ----------------------------
INSERT INTO `feed` VALUES (1, '/file/7cf9bedc69354a8dae60bdab53f919da.png', '/file/54b9b97e82154ea8a8ab0b10e1af40df.png', '/file/34adef4accb244a1a838f2910eedde1b.png', '广州市天河区珠江公园东门草坪区', 'TNR救助点，每日固定时间投喂猫粮，设置诱捕笼进行绝育救助');
INSERT INTO `feed` VALUES (2, '/file/a496994355a340ed8d0d50747a064d43.png', '/file/85c95623720145c4898cce1ec3a62a9d.png', '/file/e893810859b84931a4d560cdd9d187ec.png', '广州市越秀区麓湖公园聚芳园旁', '景区流浪猫和谐共处示范点，游客喂食点，定期清洁消毒');
INSERT INTO `feed` VALUES (3, '/file/a01da851d0224d24921ff87519b0d9c5.png', '/file/69b2a2964ace45c1a79268379bad1d01.png', '/file/89b1cdeba6464d13b857ebe339053899.png', '广州市白云区云台花园入口左侧', '鸟类与小动物混合喂食点，设有饮水池和避雨棚');
INSERT INTO `feed` VALUES (4, '/file/7e2752505ec0499fbb6188e9a899283b.png', '/file/e64134bf4fea4038ae2a868dda6c95bd.png', '/file/0f79dcbced974f68a6272e9345a36d85.png', '广州市番禺区大学城广东科学中心后面草坪', '校园流浪动物关爱点，大学生志愿者管理，科学投喂');
INSERT INTO `feed` VALUES (5, '/file/90afd5752f4d427183c1dde078dc1c12.png', '/file/f8a48c78012c4557a13f696cb45559c3.png', '/file/3d0923aa7f114b19bb842c3db1c04e04.png', '广州市海珠区海珠湖公园东侧栈道旁', '湿地公园流浪动物关爱点，生态友好型投喂站');
INSERT INTO `feed` VALUES (6, '/file/d27cf4fd2c2c49b884c098eeff74beae.png', '/file/6c2f56e09fab4cceae21eefb36686a54.png', '/file/b4dc164ec0694eccb7bb7b7f82affc7f.png', '广州市越秀区二沙岛体育公园北侧', '高档社区流浪动物管理示范点，居民共建共管');

-- ----------------------------
-- Table structure for health_advice
-- ----------------------------
DROP TABLE IF EXISTS `health_advice`;
CREATE TABLE `health_advice`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '建议ID',
  `pet_id` int(11) NOT NULL COMMENT '宠物ID',
  `pet_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宠物名称',
  `breed` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宠物品种',
  `age` int(11) NULL DEFAULT NULL COMMENT '宠物年龄（月）',
  `advice_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '建议类型',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '建议标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '建议内容',
  `priority` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '优先级',
  `is_read` int(11) NULL DEFAULT 0 COMMENT '是否已读',
  `is_handled` int(11) NULL DEFAULT 0 COMMENT '是否已处理',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pet_id`(`pet_id`) USING BTREE,
  INDEX `idx_advice_type`(`advice_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 303 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '健康建议表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_advice
-- ----------------------------
INSERT INTO `health_advice` VALUES (1, 3, 'hh', 'Jing', NULL, 'DIET', 'h', 'h', 'LOW', 1, 0, '2026-04-22 16:14:34');
INSERT INTO `health_advice` VALUES (88, 108, '黑子', '狗', 0, 'VACCINE', '幼宠疫苗接种', '建议带宠物进行疫苗接种，幼年期需要完成基础免疫程序。', 'HIGH', 0, 0, '2026-05-02 00:43:28');
INSERT INTO `health_advice` VALUES (89, 108, '黑子', '狗', 0, 'DIET', '幼宠驱虫护理', '幼宠建议每月进行一次体内驱虫，注意观察排便情况。', 'HIGH', 0, 0, '2026-05-02 00:43:28');
INSERT INTO `health_advice` VALUES (90, 108, '黑子', '狗', 0, 'CHECKUP', '幼宠首次体检', '建议进行全面的幼宠体检，包括传染病筛查和寄生虫检查。', 'HIGH', 0, 0, '2026-05-02 00:43:28');
INSERT INTO `health_advice` VALUES (91, 108, '黑子', '狗', 0, 'DIET', '幼宠营养配方', '建议使用专门的幼宠粮，提供充足的蛋白质和钙质支持生长发育。', 'HIGH', 0, 0, '2026-05-02 00:43:28');
INSERT INTO `health_advice` VALUES (92, 109, '黑子', '狗', 0, 'VACCINE', '幼宠疫苗接种', '建议带宠物进行疫苗接种，幼年期需要完成基础免疫程序。', 'HIGH', 0, 0, '2026-05-02 00:43:32');
INSERT INTO `health_advice` VALUES (93, 109, '黑子', '狗', 0, 'DIET', '幼宠驱虫护理', '幼宠建议每月进行一次体内驱虫，注意观察排便情况。', 'HIGH', 0, 0, '2026-05-02 00:43:32');
INSERT INTO `health_advice` VALUES (94, 109, '黑子', '狗', 0, 'CHECKUP', '幼宠首次体检', '建议进行全面的幼宠体检，包括传染病筛查和寄生虫检查。', 'HIGH', 0, 0, '2026-05-02 00:43:32');
INSERT INTO `health_advice` VALUES (95, 109, '黑子', '狗', 0, 'DIET', '幼宠营养配方', '建议使用专门的幼宠粮，提供充足的蛋白质和钙质支持生长发育。', 'HIGH', 0, 0, '2026-05-02 00:43:32');
INSERT INTO `health_advice` VALUES (96, 110, '黑子', '狗', 0, 'VACCINE', '幼宠疫苗接种', '建议带宠物进行疫苗接种，幼年期需要完成基础免疫程序。', 'HIGH', 0, 0, '2026-05-02 00:43:36');
INSERT INTO `health_advice` VALUES (97, 110, '黑子', '狗', 0, 'DIET', '幼宠驱虫护理', '幼宠建议每月进行一次体内驱虫，注意观察排便情况。', 'HIGH', 0, 0, '2026-05-02 00:43:36');
INSERT INTO `health_advice` VALUES (98, 110, '黑子', '狗', 0, 'CHECKUP', '幼宠首次体检', '建议进行全面的幼宠体检，包括传染病筛查和寄生虫检查。', 'HIGH', 0, 0, '2026-05-02 00:43:36');
INSERT INTO `health_advice` VALUES (99, 110, '黑子', '狗', 0, 'DIET', '幼宠营养配方', '建议使用专门的幼宠粮，提供充足的蛋白质和钙质支持生长发育。', 'HIGH', 0, 0, '2026-05-02 00:43:36');
INSERT INTO `health_advice` VALUES (217, 25, '小白', '英国短毛猫', 24, 'VACCINE', '年度疫苗加强', '宠物已到接种加强针的时间，请联系兽医预约疫苗接种。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (218, 25, '小白', '英国短毛猫', 24, 'DIET', '定期驱虫提醒', '建议每3个月进行一次体内驱虫，每1-2个月进行一次体外驱虫。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (219, 25, '小白', '英国短毛猫', 24, 'CHECKUP', '年度体检提醒', '建议每年进行一次全面体检，早发现早治疗。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (220, 25, '小白', '英国短毛猫', 24, 'EXERCISE', '猫咪运动建议', '建议每天陪猫咪玩耍15-30分钟，使用逗猫棒或激光笔保持其活力。', 'LOW', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (221, 25, '小白', '英国短毛猫', 24, '疫苗', '猫三联疫苗（第三针）复诊提醒', '根据上次记录，建议在 2026-04-21 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (222, 25, '小白', '英国短毛猫', 24, '驱虫', '体内驱虫（拜耳）复诊提醒', '根据上次记录，建议在 2026-01-26 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (223, 25, '小白', '英国短毛猫', 24, '体检', '年度健康体检复诊提醒', '根据上次记录，建议在 2025-06-30 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (224, 25, '小白', '英国短毛猫', 24, '疫苗', '狂犬疫苗复诊提醒', '根据上次记录，建议在 2025-10-23 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (225, 25, '小白', '英国短毛猫', 24, '驱虫', '月度体内驱虫复诊提醒', '根据上次记录，建议在 2026-02-01 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (226, 25, '小白', '英国短毛猫', 24, '驱虫', '月度体内驱虫复诊提醒', '根据上次记录，建议在 2026-05-01 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (227, 25, '小白', '英国短毛猫', 24, '驱虫', '体内驱虫复诊提醒', '根据上次记录，建议在 2026-02-01 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (228, 25, '小白', '英国短毛猫', 24, '体检', '半年度体检复诊提醒', '根据上次记录，建议在 2026-06-15 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (229, 25, '小白', '英国短毛猫', 24, '驱虫', '体外驱虫复诊提醒', '根据上次记录，建议在 2026-05-01 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (230, 26, '花花', '美国短毛猫', 12, 'VACCINE', '年度疫苗加强', '宠物已到接种加强针的时间，请联系兽医预约疫苗接种。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (231, 26, '花花', '美国短毛猫', 12, 'DIET', '定期驱虫提醒', '建议每3个月进行一次体内驱虫，每1-2个月进行一次体外驱虫。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (232, 26, '花花', '美国短毛猫', 12, 'CHECKUP', '年度体检提醒', '建议每年进行一次全面体检，早发现早治疗。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (233, 26, '花花', '美国短毛猫', 12, 'EXERCISE', '猫咪运动建议', '建议每天陪猫咪玩耍15-30分钟，使用逗猫棒或激光笔保持其活力。', 'LOW', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (234, 26, '花花', '美国短毛猫', 12, 'OTHER', '绝育建议', '建议在合适的年龄进行绝育手术，可以预防一些生殖系统疾病。', 'LOW', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (235, 26, '花花', '美国短毛猫', 12, '疫苗', '猫三联疫苗（第二针）复诊提醒', '根据上次记录，建议在 2026-04-11 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (236, 26, '花花', '美国短毛猫', 12, '驱虫', '体内+体外驱虫复诊提醒', '根据上次记录，建议在 2026-03-12 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (237, 26, '花花', '美国短毛猫', 12, '疫苗', '猫三联疫苗（第三针）复诊提醒', '根据上次记录，建议在 2026-12-01 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (238, 26, '花花', '美国短毛猫', 12, '驱虫', '体内+体外驱虫复诊提醒', '根据上次记录，建议在 2026-05-15 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (239, 27, '黑豆', '中华田园猫', 8, 'VACCINE', '幼宠疫苗接种', '建议带宠物进行疫苗接种，幼年期需要完成基础免疫程序。', 'HIGH', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (240, 27, '黑豆', '中华田园猫', 8, 'DIET', '幼宠驱虫护理', '幼宠建议每月进行一次体内驱虫，注意观察排便情况。', 'HIGH', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (241, 27, '黑豆', '中华田园猫', 8, 'CHECKUP', '幼宠首次体检', '建议进行全面的幼宠体检，包括传染病筛查和寄生虫检查。', 'HIGH', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (242, 27, '黑豆', '中华田园猫', 8, 'EXERCISE', '猫咪运动建议', '建议每天陪猫咪玩耍15-30分钟，使用逗猫棒或激光笔保持其活力。', 'LOW', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (243, 27, '黑豆', '中华田园猫', 8, '疫苗', '猫三联疫苗（第一针）复诊提醒', '根据上次记录，建议在 2026-04-11 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (244, 27, '黑豆', '中华田园猫', 8, '驱虫', '体外驱虫复诊提醒', '根据上次记录，建议在 2026-03-12 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (245, 27, '黑豆', '中华田园猫', 8, '疫苗', '猫三联疫苗（第一针）复诊提醒', '根据上次记录，建议在 2026-01-01 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (246, 27, '黑豆', '中华田园猫', 8, '疫苗', '猫三联疫苗（第二针）复诊提醒', '根据上次记录，建议在 2026-02-15 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (247, 27, '黑豆', '中华田园猫', 8, '驱虫', '体内+体外驱虫复诊提醒', '根据上次记录，建议在 2026-06-10 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (248, 28, '萌萌', '泰迪犬', 24, 'VACCINE', '年度疫苗加强', '宠物已到接种加强针的时间，请联系兽医预约疫苗接种。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (249, 28, '萌萌', '泰迪犬', 24, 'DIET', '定期驱虫提醒', '建议每3个月进行一次体内驱虫，每1-2个月进行一次体外驱虫。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (250, 28, '萌萌', '泰迪犬', 24, 'CHECKUP', '年度体检提醒', '建议每年进行一次全面体检，早发现早治疗。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (251, 28, '萌萌', '泰迪犬', 24, '疫苗', '犬六联疫苗（加强针）复诊提醒', '根据上次记录，建议在 2025-11-12 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (252, 28, '萌萌', '泰迪犬', 24, '驱虫', '体内驱虫复诊提醒', '根据上次记录，建议在 2026-02-25 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (253, 28, '萌萌', '泰迪犬', 24, '体检', '年度体检复诊提醒', '根据上次记录，建议在 2025-07-25 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (254, 28, '萌萌', '泰迪犬', 24, '疫苗', '狂犬疫苗复诊提醒', '根据上次记录，建议在 2025-11-12 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (255, 28, '萌萌', '泰迪犬', 24, '驱虫', '体内+体外驱虫复诊提醒', '根据上次记录，建议在 2026-02-05 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (256, 28, '萌萌', '泰迪犬', 24, '体检', '年度体检复诊提醒', '根据上次记录，建议在 2026-12-20 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (257, 28, '萌萌', '泰迪犬', 24, '驱虫', '体外驱虫复诊提醒', '根据上次记录，建议在 2026-05-20 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (258, 29, '小灰', '比熊犬', 12, 'VACCINE', '年度疫苗加强', '宠物已到接种加强针的时间，请联系兽医预约疫苗接种。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (259, 29, '小灰', '比熊犬', 12, 'DIET', '定期驱虫提醒', '建议每3个月进行一次体内驱虫，每1-2个月进行一次体外驱虫。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (260, 29, '小灰', '比熊犬', 12, 'CHECKUP', '年度体检提醒', '建议每年进行一次全面体检，早发现早治疗。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (261, 29, '小灰', '比熊犬', 12, 'OTHER', '绝育建议', '建议在合适的年龄进行绝育手术，可以预防一些生殖系统疾病。', 'LOW', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (262, 29, '小灰', '比熊犬', 12, '疫苗', '犬六联疫苗（第三针）复诊提醒', '根据上次记录，建议在 2026-04-11 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (263, 29, '小灰', '比熊犬', 12, '驱虫', '体内+体外驱虫复诊提醒', '根据上次记录，建议在 2026-03-27 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (264, 29, '小灰', '比熊犬', 12, '疫苗', '狂犬疫苗复诊提醒', '根据上次记录，建议在 2025-08-04 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (265, 29, '小灰', '比熊犬', 12, '驱虫', '体内驱虫复诊提醒', '根据上次记录，建议在 2026-02-10 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (266, 29, '小灰', '比熊犬', 12, '体检', '年度体检复诊提醒', '根据上次记录，建议在 2026-12-18 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (267, 29, '小灰', '比熊犬', 12, '驱虫', '体内+体外驱虫复诊提醒', '根据上次记录，建议在 2026-05-28 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (268, 30, '团子', '布偶猫', 48, 'DIET', '定期驱虫提醒', '建议每3个月进行一次体内驱虫，每1-2个月进行一次体外驱虫。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (269, 30, '团子', '布偶猫', 48, 'CHECKUP', '年度体检提醒', '建议每年进行一次全面体检，早发现早治疗。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (270, 30, '团子', '布偶猫', 48, 'EXERCISE', '猫咪运动建议', '建议每天陪猫咪玩耍15-30分钟，使用逗猫棒或激光笔保持其活力。', 'LOW', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (271, 30, '团子', '布偶猫', 48, 'DENTAL', '宠物牙齿护理', '宠物进入中年期，建议开始定期刷牙或使用漱口水，预防牙结石和口腔疾病。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (272, 30, '团子', '布偶猫', 48, '疫苗', '猫三联疫苗（加强针）复诊提醒', '根据上次记录，建议在 2025-10-23 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (273, 30, '团子', '布偶猫', 48, '驱虫', '体内驱虫复诊提醒', '根据上次记录，建议在 2026-01-26 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (274, 30, '团子', '布偶猫', 48, '体检', '年度全面体检复诊提醒', '根据上次记录，建议在 2025-08-24 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (275, 30, '团子', '布偶猫', 48, '疫苗', '狂犬疫苗复诊提醒', '根据上次记录，建议在 2025-10-23 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (276, 31, '旺财', '柴犬', 24, 'VACCINE', '年度疫苗加强', '宠物已到接种加强针的时间，请联系兽医预约疫苗接种。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (277, 31, '旺财', '柴犬', 24, 'DIET', '定期驱虫提醒', '建议每3个月进行一次体内驱虫，每1-2个月进行一次体外驱虫。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (278, 31, '旺财', '柴犬', 24, 'CHECKUP', '年度体检提醒', '建议每年进行一次全面体检，早发现早治疗。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (279, 31, '旺财', '柴犬', 24, '疫苗', '犬八联疫苗（加强针）复诊提醒', '根据上次记录，建议在 2025-11-12 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (280, 31, '旺财', '柴犬', 24, '驱虫', '体内+体外驱虫复诊提醒', '根据上次记录，建议在 2026-03-12 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (281, 31, '旺财', '柴犬', 24, '体检', '年度体检复诊提醒', '根据上次记录，建议在 2025-08-04 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (282, 31, '旺财', '柴犬', 24, '疫苗', '狂犬疫苗复诊提醒', '根据上次记录，建议在 2025-11-12 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (283, 31, '旺财', '柴犬', 24, '治疗', '皮肤病检查复诊提醒', '根据上次记录，建议在 2026-04-06 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (284, 31, '旺财', '柴犬', 24, '驱虫', '体内驱虫复诊提醒', '根据上次记录，建议在 2026-02-10 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (285, 31, '旺财', '柴犬', 24, '驱虫', '体内驱虫复诊提醒', '根据上次记录，建议在 2026-04-10 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (286, 32, '球球', '萨摩耶', 12, 'VACCINE', '年度疫苗加强', '宠物已到接种加强针的时间，请联系兽医预约疫苗接种。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (287, 32, '球球', '萨摩耶', 12, 'DIET', '定期驱虫提醒', '建议每3个月进行一次体内驱虫，每1-2个月进行一次体外驱虫。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (288, 32, '球球', '萨摩耶', 12, 'CHECKUP', '年度体检提醒', '建议每年进行一次全面体检，早发现早治疗。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (289, 32, '球球', '萨摩耶', 12, 'GROOMING', '长毛宠物美容建议', '建议每周至少梳理毛发2-3次，定期洗澡美容，预防毛发打结。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (290, 32, '球球', '萨摩耶', 12, 'OTHER', '绝育建议', '建议在合适的年龄进行绝育手术，可以预防一些生殖系统疾病。', 'LOW', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (291, 32, '球球', '萨摩耶', 12, '疫苗', '犬八联疫苗（第二针）复诊提醒', '根据上次记录，建议在 2026-04-11 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (292, 32, '球球', '萨摩耶', 12, '驱虫', '体内驱虫复诊提醒', '根据上次记录，建议在 2026-02-25 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (293, 32, '球球', '萨摩耶', 12, '疫苗', '狂犬疫苗复诊提醒', '根据上次记录，建议在 2025-07-25 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (294, 32, '球球', '萨摩耶', 12, '体检', '幼犬发育评估复诊提醒', '根据上次记录，建议在 2025-11-27 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (295, 32, '球球', '萨摩耶', 12, '驱虫', '体内驱虫复诊提醒', '根据上次记录，建议在 2026-02-20 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (296, 32, '球球', '萨摩耶', 12, '驱虫', '体内驱虫复诊提醒', '根据上次记录，建议在 2026-05-15 进行复诊或复查。', 'MEDIUM', 0, 0, '2026-05-22 21:54:22');
INSERT INTO `health_advice` VALUES (297, 26, '花花', NULL, NULL, 'SERVICE_REMINDER', '服务预约到期提醒', '【订单号：202605222142053471】\n尊敬的【爱猫人士小王】：\n您好！您预约的【花花】的【绝育手术套餐】，将于【2026年05月23日 00:00】开始。\n\n服务门店：爱宠宠物医院\n联系电话：021-88880004\n\n请您按时前往门店，为保障服务体验，建议提前10分钟到店。', 'HIGH', 0, 0, NULL);
INSERT INTO `health_advice` VALUES (298, 25, '小白', NULL, NULL, 'SERVICE_REMINDER', '服务预约到期提醒', '【订单号：202605222151238974】\n尊敬的【爱猫人士小王】：\n您好！您预约的【小白】的【猫咪美容SPA】，将于【2026年05月23日 00:00】开始。\n\n服务门店：萌爪宠物SPA\n联系电话：021-88880001\n\n请您按时前往门店，为保障服务体验，建议提前10分钟到店。', 'HIGH', 0, 0, NULL);
INSERT INTO `health_advice` VALUES (299, 32, '球球', NULL, NULL, 'SERVICE_REMINDER', '服务预约到期提醒', '【订单号：SO2026040025】\n尊敬的【铲屎官小李】：\n您好！您预约的【球球】的【泰迪造型美容】，将于【2026年04月29日 23:41】开始。\n\n服务门店：萌爪宠物SPA\n联系电话：021-88880001\n\n请您按时前往门店，为保障服务体验，建议提前10分钟到店。', 'HIGH', 0, 0, NULL);
INSERT INTO `health_advice` VALUES (300, 26, '花花', NULL, NULL, 'SERVICE_REMINDER', '服务预约到期提醒', '【订单号：202604270231001918】\n尊敬的【爱猫人士小王】：\n您好！您预约的【花花】的【眼科专项检查】，将于【2026年04月28日 00:00】开始。\n\n服务门店：爱宠宠物医院\n联系电话：021-88880004\n\n请您按时前往门店，为保障服务体验，建议提前10分钟到店。', 'HIGH', 0, 0, NULL);
INSERT INTO `health_advice` VALUES (301, 26, '花花', NULL, NULL, 'SERVICE_REMINDER', '服务预约到期提醒', '【订单号：202604291751322064】\n尊敬的【爱猫人士小王】：\n您好！您预约的【花花】的【绝育手术套餐】，将于【2026年04月30日 23:00】开始。\n\n服务门店：爱宠宠物医院\n联系电话：021-88880004\n\n请您按时前往门店，为保障服务体验，建议提前10分钟到店。', 'HIGH', 0, 0, NULL);
INSERT INTO `health_advice` VALUES (302, 26, '花花', NULL, NULL, 'SERVICE_REMINDER', '服务预约到期提醒', '【订单号：202605222137012176】\n尊敬的【爱猫人士小王】：\n您好！您预约的【花花】的【会员专享洗澡卡】，将于【2026年05月23日 08:00】开始。\n\n服务门店：星级宠物会所\n联系电话：021-88880003\n\n请您按时前往门店，为保障服务体验，建议提前10分钟到店。', 'HIGH', 0, 0, NULL);

-- ----------------------------
-- Table structure for health_record
-- ----------------------------
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `pet_id` int(11) NOT NULL COMMENT '宠物ID，关联pet_profile.id',
  `record_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '记录类型：疫苗/驱虫/体检/治疗',
  `item_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
  `record_date` date NOT NULL COMMENT '记录时间',
  `hospital` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '医院/机构',
  `doctor` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '医生',
  `next_date` date NULL DEFAULT NULL COMMENT '下次提醒时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `attachment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件URL，多个用逗号分隔',
  `weight` decimal(6, 2) NULL DEFAULT NULL COMMENT '当时体重（kg）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_health_pet_id`(`pet_id`) USING BTREE,
  INDEX `idx_health_type`(`record_type`) USING BTREE,
  INDEX `idx_health_next_date`(`next_date`) USING BTREE,
  CONSTRAINT `fk_health_pet` FOREIGN KEY (`pet_id`) REFERENCES `pet_profile` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物健康记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_record
-- ----------------------------
INSERT INTO `health_record` VALUES (1, 25, '疫苗', '猫三联疫苗（第三针）', '2025-04-26', '爱贝宠物医院', '张医生', '2026-04-21', '已完成全部免疫程序', NULL, 4.50, '2025-04-26 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (2, 25, '驱虫', '体内驱虫（拜耳）', '2026-01-26', '爱贝宠物医院', '李医生', '2026-01-26', '体内寄生虫检查正常', NULL, 4.55, '2026-01-26 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (3, 25, '体检', '年度健康体检', '2026-02-25', '爱贝宠物医院', '张医生', '2025-06-30', '各项指标正常，身体健康', NULL, 4.50, '2026-02-25 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (4, 25, '疫苗', '狂犬疫苗', '2025-10-28', '爱贝宠物医院', '张医生', '2025-10-23', '抗体检测合格', NULL, 4.50, '2025-10-28 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (5, 26, '疫苗', '猫三联疫苗（第二针）', '2025-10-28', '爱贝宠物医院', '王医生', '2026-04-11', '即将接种第三针', NULL, 3.60, '2025-10-28 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (6, 26, '驱虫', '体内+体外驱虫', '2026-03-12', '爱贝宠物医院', '李医生', '2026-03-12', '使用大宠爱滴剂，无不良反应', NULL, 3.75, '2026-03-12 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (7, 26, '体检', '幼猫基础体检', '2025-12-27', '爱贝宠物医院', '张医生', NULL, '身体发育良好，无遗传疾病', NULL, 3.80, '2025-12-27 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (8, 27, '疫苗', '猫三联疫苗（第一针）', '2026-03-27', '爱贝宠物医院', '王医生', '2026-04-11', '需要继续完成免疫程序', NULL, 3.20, '2026-03-27 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (9, 27, '驱虫', '体外驱虫', '2026-04-11', '爱贝宠物医院', '李医生', '2026-03-12', '预防跳蚤和耳螨', NULL, 3.50, '2026-04-11 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (10, 28, '疫苗', '犬六联疫苗（加强针）', '2025-10-08', '汪星人宠物医院', '刘医生', '2025-11-12', '抗体水平良好', NULL, 5.10, '2025-10-08 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (11, 28, '驱虫', '体内驱虫', '2026-02-25', '汪星人宠物医院', '刘医生', '2026-02-25', '粪便检查无寄生虫', NULL, 5.20, '2026-02-25 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (12, 28, '体检', '年度体检', '2026-01-26', '汪星人宠物医院', '刘医生', '2025-07-25', '心脏功能正常，牙齿轻微牙结石', NULL, 5.20, '2026-01-26 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (13, 28, '治疗', '牙齿清洁', '2026-03-27', '汪星人宠物医院', '刘医生', NULL, '已进行超声波洁牙', NULL, 5.25, '2026-03-27 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (14, 28, '疫苗', '狂犬疫苗', '2025-10-08', '汪星人宠物医院', '刘医生', '2025-11-12', '已产生抗体', NULL, 5.10, '2025-10-08 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (15, 29, '疫苗', '犬六联疫苗（第三针）', '2025-11-27', '汪星人宠物医院', '刘医生', '2026-04-11', '免疫程序即将完成', NULL, 5.80, '2025-11-27 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (16, 29, '驱虫', '体内+体外驱虫', '2026-03-27', '汪星人宠物医院', '刘医生', '2026-03-27', '使用尼可信口服驱虫药', NULL, 6.00, '2026-03-27 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (17, 29, '疫苗', '狂犬疫苗', '2026-01-16', '汪星人宠物医院', '刘医生', '2025-08-04', '已产生抗体', NULL, 5.90, '2026-01-16 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (18, 30, '疫苗', '猫三联疫苗（加强针）', '2025-10-28', '爱贝宠物医院', '张医生', '2025-10-23', '抗体水平正常', NULL, 5.40, '2025-10-28 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (19, 30, '驱虫', '体内驱虫', '2026-01-26', '爱贝宠物医院', '李医生', '2026-01-26', '预防性驱虫', NULL, 5.50, '2026-01-26 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (20, 30, '体检', '年度全面体检', '2025-12-27', '爱贝宠物医院', '张医生', '2025-08-24', '心脏彩超正常，肾功能良好', NULL, 5.50, '2025-12-27 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (21, 30, '疫苗', '狂犬疫苗', '2025-10-28', '爱贝宠物医院', '张医生', '2025-10-23', '抗体检测合格', NULL, 5.40, '2025-10-28 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (22, 30, '治疗', '眼睛检查', '2026-02-25', '爱贝宠物医院', '王医生', NULL, '布偶猫常见眼部问题排查，未见异常', NULL, 5.50, '2026-02-25 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (23, 31, '疫苗', '犬八联疫苗（加强针）', '2025-10-08', '星级宠物会所', '陈医生', '2025-11-12', '免疫效果良好', NULL, 10.30, '2025-10-08 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (24, 31, '驱虫', '体内+体外驱虫', '2026-03-12', '星级宠物会所', '陈医生', '2026-03-12', '使用超可信驱虫药', NULL, 10.50, '2026-03-12 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (25, 31, '体检', '年度体检', '2026-01-16', '星级宠物会所', '陈医生', '2025-08-04', '髋关节检查正常，关节活动良好', NULL, 10.50, '2026-01-16 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (26, 31, '疫苗', '狂犬疫苗', '2025-10-08', '星级宠物会所', '陈医生', '2025-11-12', '抗体水平合格', NULL, 10.30, '2025-10-08 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (27, 31, '治疗', '皮肤病检查', '2026-04-06', '星级宠物会所', '陈医生', '2026-04-06', '季节性湿疹，药物治疗中', NULL, 10.50, '2026-04-06 23:50:23', '2026-04-26 23:50:23');
INSERT INTO `health_record` VALUES (28, 32, '疫苗', '犬八联疫苗（第二针）', '2025-12-27', '爱宠宠物医院', '赵医生', '2026-04-11', '还需完成第三针', NULL, 17.50, '2025-12-27 23:52:16', '2026-04-26 23:52:16');
INSERT INTO `health_record` VALUES (29, 32, '驱虫', '体内驱虫', '2026-02-25', '爱宠宠物医院', '赵医生', '2026-02-25', '预防心丝虫', NULL, 18.00, '2026-02-25 23:52:16', '2026-04-26 23:52:16');
INSERT INTO `health_record` VALUES (30, 32, '疫苗', '狂犬疫苗', '2026-01-26', '爱宠宠物医院', '赵医生', '2025-07-25', '已产生抗体', NULL, 17.80, '2026-01-26 23:52:16', '2026-04-26 23:52:16');
INSERT INTO `health_record` VALUES (31, 32, '体检', '幼犬发育评估', '2026-03-27', '爱宠宠物医院', '赵医生', '2025-11-27', '骨骼发育正常，体型标准', NULL, 18.00, '2026-03-27 23:52:16', '2026-04-26 23:52:16');
INSERT INTO `health_record` VALUES (32, 25, '驱虫', '月度体内驱虫', '2025-11-01', '爱贝宠物医院', '张医生', '2026-02-01', NULL, NULL, 4.30, '2025-11-01 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (33, 25, '驱虫', '月度体内驱虫', '2026-02-01', '爱贝宠物医院', '李医生', '2026-05-01', NULL, NULL, 4.55, '2026-02-01 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (34, 25, '驱虫', '月度体内驱虫', '2026-04-26', '爱贝宠物医院', '张医生', NULL, NULL, NULL, 4.65, '2026-04-26 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (35, 31, '驱虫', '体内驱虫', '2025-11-10', '星级宠物会所', '陈医生', '2026-02-10', NULL, NULL, 9.80, '2025-11-10 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (36, 31, '驱虫', '体内驱虫', '2026-01-10', '星级宠物会所', '陈医生', '2026-04-10', NULL, NULL, 10.10, '2026-01-10 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (37, 31, '驱虫', '体内驱虫', '2026-04-15', '星级宠物会所', '陈医生', NULL, NULL, NULL, 10.60, '2026-04-15 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (38, 32, '驱虫', '体内驱虫', '2025-11-20', '爱宠宠物医院', '赵医生', '2026-02-20', NULL, NULL, 16.20, '2025-11-20 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (39, 32, '驱虫', '体内驱虫', '2026-02-15', '爱宠宠物医院', '赵医生', '2026-05-15', NULL, NULL, 17.20, '2026-02-15 23:52:16', '2026-05-03 23:52:16');
INSERT INTO `health_record` VALUES (40, 25, '驱虫', '体内驱虫', '2025-11-01', '爱贝宠物医院', '张医生', '2026-02-01', '体内寄生虫检查正常', NULL, 4.20, '2025-11-01 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (41, 25, '体检', '半年度体检', '2025-12-15', '爱贝宠物医院', '张医生', '2026-06-15', '各项指标正常，身体健康', NULL, 4.35, '2025-12-15 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (42, 25, '驱虫', '体外驱虫', '2026-02-01', '爱贝宠物医院', '李医生', '2026-05-01', '预防跳蚤，耳道清洁', NULL, 4.40, '2026-02-01 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (43, 25, '驱虫', '体内驱虫', '2026-04-20', '爱贝宠物医院', '张医生', NULL, '体重稳定，精神状态良好', NULL, 4.50, '2026-04-20 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (44, 26, '体检', '幼猫基础体检', '2025-11-01', '爱贝宠物医院', '王医生', NULL, '身体发育良好', NULL, 2.80, '2025-11-01 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (45, 26, '疫苗', '猫三联疫苗（第三针）', '2025-12-01', '爱贝宠物医院', '张医生', '2026-12-01', '已完成免疫程序', NULL, 3.20, '2025-12-01 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (46, 26, '驱虫', '体内+体外驱虫', '2026-02-15', '爱贝宠物医院', '李医生', '2026-05-15', '使用大宠爱滴剂，无不良反应', NULL, 3.50, '2026-02-15 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (47, 26, '体检', '半年度体检', '2026-04-10', '爱贝宠物医院', '张医生', NULL, '体重增长正常，牙齿发育良好', NULL, 3.80, '2026-04-10 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (48, 27, '体检', '幼猫首次体检', '2025-11-01', '爱贝宠物医院', '王医生', NULL, '身体发育正常，无遗传疾病', NULL, 2.10, '2025-11-01 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (49, 27, '疫苗', '猫三联疫苗（第一针）', '2025-12-01', '爱贝宠物医院', '张医生', '2026-01-01', '建立基础免疫', NULL, 2.60, '2025-12-01 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (50, 27, '疫苗', '猫三联疫苗（第二针）', '2026-01-15', '爱贝宠物医院', '王医生', '2026-02-15', '免疫程序进行中', NULL, 3.00, '2026-01-15 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (51, 27, '驱虫', '体内+体外驱虫', '2026-03-10', '爱贝宠物医院', '李医生', '2026-06-10', '使用尼可信口服，无不良反应', NULL, 3.30, '2026-03-10 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (52, 27, '体检', '生长发育评估', '2026-04-25', '爱贝宠物医院', '张医生', NULL, '骨骼发育正常，体型标准', NULL, 3.50, '2026-04-25 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (53, 28, '驱虫', '体内+体外驱虫', '2025-11-05', '汪星人宠物医院', '刘医生', '2026-02-05', '预防心丝虫', NULL, 5.00, '2025-11-05 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (54, 28, '体检', '年度体检', '2025-12-20', '汪星人宠物医院', '刘医生', '2026-12-20', '心脏功能正常，牙齿轻微牙结石', NULL, 5.10, '2025-12-20 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (55, 28, '驱虫', '体外驱虫', '2026-02-20', '汪星人宠物医院', '李医生', '2026-05-20', '预防蜱虫跳蚤', NULL, 5.15, '2026-02-20 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (56, 28, '治疗', '牙齿清洁', '2026-04-15', '汪星人宠物医院', '刘医生', NULL, '超声波洁牙，牙齿健康良好', NULL, 5.20, '2026-04-15 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (57, 29, '驱虫', '体内驱虫', '2025-11-10', '汪星人宠物医院', '刘医生', '2026-02-10', '粪便检查无寄生虫', NULL, 5.60, '2025-11-10 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (58, 29, '体检', '年度体检', '2025-12-18', '汪星人宠物医院', '刘医生', '2026-12-18', '毛发浓密，关节正常', NULL, 5.80, '2025-12-18 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (59, 29, '驱虫', '体内+体外驱虫', '2026-02-28', '汪星人宠物医院', '陈医生', '2026-05-28', '使用超可信驱虫药', NULL, 5.90, '2026-02-28 23:50:23', '2026-05-03 23:50:23');
INSERT INTO `health_record` VALUES (60, 29, '治疗', '皮肤病检查', '2026-04-20', '汪星人宠物医院', '刘医生', NULL, '轻微湿疹，药浴治疗中', NULL, 6.00, '2026-04-20 23:50:23', '2026-05-03 23:50:23');

-- ----------------------------
-- Table structure for pet_profile
-- ----------------------------
DROP TABLE IF EXISTS `pet_profile`;
CREATE TABLE `pet_profile`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `owner_id` int(11) NOT NULL COMMENT '主人ID，关联sys_user.id',
  `pet_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '宠物名称',
  `breed` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '品种',
  `pet_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宠物大类：cat/dog/other',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄（月）',
  `gender` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别',
  `weight` decimal(6, 2) NULL DEFAULT NULL COMMENT '体重（kg）',
  `neutered` tinyint(4) NOT NULL DEFAULT 0 COMMENT '绝育状态：0未绝育 1已绝育',
  `adopt_date` date NULL DEFAULT NULL COMMENT '领养日期',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像URL',
  `emergency_contact` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `intro` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宠物简介/动态',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '话题标签，多个用逗号分隔',
  `is_public` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否公开：0不公开 1公开',
  `qr_code_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '二维码内容（宠物ID+主人手机号）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pet_owner_id`(`owner_id`) USING BTREE,
  INDEX `idx_pet_public`(`is_public`) USING BTREE,
  INDEX `idx_pet_name`(`pet_name`) USING BTREE,
  CONSTRAINT `fk_pet_profile_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物档案表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet_profile
-- ----------------------------
INSERT INTO `pet_profile` VALUES (25, 7, '小白', '英国短毛猫', 'cat', 24, 'MALE', 4.50, 1, '2025-03-22', '/file/a0dbb724d34c4a6bab8115f26b70c81b.png', '13800001111', NULL, '小白是个高冷小公主，每天最期待的就是罐头时间～', '可爱萌宠,铲屎官日常', 1, 'PET001|13800001111', '2026-04-26 23:34:11', '2026-05-03 22:08:11');
INSERT INTO `pet_profile` VALUES (26, 7, '花花', '美国短毛猫', 'cat', 12, '母', 3.80, 0, '2025-06-30', '/file/5fd8fe72106f4a50b0788232aab07c15.png', '13800001111', NULL, '花花是个活泼的小话痨，喜欢在窗台晒太阳看鸟～', '萌宠日记', 1, 'PET002|13800001111', '2026-04-26 23:34:11', '2026-05-03 22:08:11');
INSERT INTO `pet_profile` VALUES (27, 7, '黑豆', '中华田园猫', 'cat', 8, 'MALE', 3.50, 1, '2026-01-16', '/file/1998ef4452dd4f6a94f7fa5d10fb7250.png', '13800001111', NULL, '黑豆是个小调皮，爱偷吃桌上的小鱼干！', '宠物故事,吃货一枚', 1, 'PET003|13800001111', '2026-04-26 23:34:11', '2026-05-03 22:08:11');
INSERT INTO `pet_profile` VALUES (28, 8, '萌萌', '泰迪犬', 'dog', 24, 'FEMALE', 5.20, 1, '2025-05-11', '/file/821df7b79c8946e8958ab151cb09fa1d.png', '13800002222', NULL, '萌萌是个粘人的小可爱，每天都要抱抱才肯睡～', '可爱萌宠,家有毛孩子', 1, 'PET004|13800002222', '2026-04-26 23:34:11', '2026-05-03 22:08:11');
INSERT INTO `pet_profile` VALUES (29, 8, '小灰', '比熊犬', 'dog', 12, 'MALE', 6.00, 0, '2025-08-19', '/file/cfff9849329b4a61aaf40dd811195b3d.png', '13800002222', NULL, '小灰是个拆家小能手，但无辜的眼神让人舍不得生气！', '养宠心得', 1, 'PET005|13800002222', '2026-04-26 23:34:11', '2026-05-03 22:08:11');
INSERT INTO `pet_profile` VALUES (30, 9, '团子', '布偶猫', 'cat', 48, 'MALE', 5.50, 1, '2024-09-03', '/file/001ee2e7a01542e1b9583ed679db7a13.png', '13800003333', NULL, '团子是一只温柔的布偶猫，喜欢蹭人求摸摸～', '萌宠日记,宠物穿搭', 1, 'PET006|13800003333', '2026-04-26 23:34:11', '2026-05-03 22:08:11');
INSERT INTO `pet_profile` VALUES (31, 10, '旺财', '柴犬', 'dog', 24, 'MALE', 10.50, 1, '2025-03-22', '/file/8b25134ae1cf4770b74df872006c3e20.png', '13800004444', NULL, '旺财是个忠诚的小保镖，看到陌生人会立刻通知主人！', '宠物故事,养宠心得', 1, 'PET007|13800004444', '2026-04-26 23:34:11', '2026-05-03 22:08:11');
INSERT INTO `pet_profile` VALUES (32, 11, '球球', '萨摩耶', 'dog', 12, 'FEMALE', 18.00, 0, '2025-10-08', '/file/f8f7b7907c7c4617a93f9170964a602f.png', '13800005555', NULL, '球球是个拆家小能手，每天都要出门遛弯两小时～', '铲屎官日常,家有毛孩子', 1, 'PET008|13800005555', '2026-04-26 23:34:11', '2026-05-03 22:08:11');

-- ----------------------------
-- Table structure for pet_profile_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `pet_profile_audit_log`;
CREATE TABLE `pet_profile_audit_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_profile_id` int(11) NOT NULL COMMENT '宠物档案ID',
  `operator_id` int(11) NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人昵称',
  `action_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型',
  `before_public` tinyint(4) NULL DEFAULT NULL COMMENT '审核前公开状态',
  `after_public` tinyint(4) NULL DEFAULT NULL COMMENT '审核后公开状态',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pet_audit_profile_id`(`pet_profile_id`) USING BTREE,
  INDEX `idx_pet_audit_operator_id`(`operator_id`) USING BTREE,
  CONSTRAINT `fk_pet_audit_operator` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_pet_audit_profile` FOREIGN KEY (`pet_profile_id`) REFERENCES `pet_profile` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物档案审核日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet_profile_audit_log
-- ----------------------------

-- ----------------------------
-- Table structure for pet_profile_collect
-- ----------------------------
DROP TABLE IF EXISTS `pet_profile_collect`;
CREATE TABLE `pet_profile_collect`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_profile_id` int(11) NOT NULL COMMENT '宠物档案ID',
  `user_id` int(11) NOT NULL COMMENT '收藏用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_collect`(`pet_profile_id`, `user_id`) USING BTREE,
  INDEX `idx_collect_pet`(`pet_profile_id`) USING BTREE,
  INDEX `idx_collect_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物动态收藏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet_profile_collect
-- ----------------------------
INSERT INTO `pet_profile_collect` VALUES (2, 32, 7, '2026-05-22 21:29:37');
INSERT INTO `pet_profile_collect` VALUES (3, 31, 7, '2026-05-22 21:34:32');
INSERT INTO `pet_profile_collect` VALUES (4, 30, 7, '2026-05-22 21:40:36');
INSERT INTO `pet_profile_collect` VALUES (5, 28, 7, '2026-05-22 21:49:56');

-- ----------------------------
-- Table structure for pet_profile_comment
-- ----------------------------
DROP TABLE IF EXISTS `pet_profile_comment`;
CREATE TABLE `pet_profile_comment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `pet_profile_id` int(11) NOT NULL COMMENT '宠物档案ID',
  `user_id` int(11) NOT NULL COMMENT '评论用户ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_comment_pet`(`pet_profile_id`) USING BTREE,
  INDEX `idx_comment_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物动态评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet_profile_comment
-- ----------------------------
INSERT INTO `pet_profile_comment` VALUES (1, 25, 8, '小白好可爱呀，眼睛好蓝！', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (2, 25, 9, '我家猫也超爱罐头，罐头的力量无穷大～', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (3, 26, 10, '花花的花纹好特别，像个小豹子', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (4, 27, 11, '中华田园猫最棒了，身体好又聪明！', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (5, 28, 7, '萌萌好漂亮！这个造型在哪里做的呀', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (6, 28, 9, '泰迪真的很粘人，想rua一下', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (7, 29, 10, '比熊真的是小天使，笑容治愈', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (8, 30, 8, '团子这眼神绝了，太温柔了吧', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (9, 31, 11, '柴犬果然是最帅的！求多发照片', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (10, 32, 7, '球球笑起来太治愈了，萨摩耶天使狗', '2026-05-03 22:08:32');
INSERT INTO `pet_profile_comment` VALUES (11, 32, 7, '真好看', '2026-05-22 21:12:48');
INSERT INTO `pet_profile_comment` VALUES (12, 32, 7, '真好看', '2026-05-22 21:29:42');
INSERT INTO `pet_profile_comment` VALUES (13, 31, 7, '这好看', '2026-05-22 21:34:42');

-- ----------------------------
-- Table structure for pet_profile_like
-- ----------------------------
DROP TABLE IF EXISTS `pet_profile_like`;
CREATE TABLE `pet_profile_like`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_profile_id` int(11) NOT NULL COMMENT '宠物档案ID',
  `user_id` int(11) NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_like`(`pet_profile_id`, `user_id`) USING BTREE,
  INDEX `idx_like_pet`(`pet_profile_id`) USING BTREE,
  INDEX `idx_like_user`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物动态点赞表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet_profile_like
-- ----------------------------
INSERT INTO `pet_profile_like` VALUES (1, 25, 8, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (2, 25, 9, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (3, 25, 10, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (4, 26, 7, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (5, 26, 10, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (6, 26, 11, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (7, 27, 7, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (8, 27, 8, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (9, 28, 7, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (10, 28, 9, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (11, 28, 11, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (12, 28, 10, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (13, 29, 8, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (14, 29, 9, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (15, 30, 7, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (16, 30, 8, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (17, 30, 9, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (18, 30, 10, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (19, 30, 11, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (20, 31, 7, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (21, 31, 9, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (22, 31, 11, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (23, 32, 7, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (24, 32, 8, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (25, 32, 9, '2026-05-03 22:08:23');
INSERT INTO `pet_profile_like` VALUES (26, 32, 10, '2026-05-03 22:08:23');

-- ----------------------------
-- Table structure for pet_topic
-- ----------------------------
DROP TABLE IF EXISTS `pet_topic`;
CREATE TABLE `pet_topic`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '话题ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '话题名称',
  `use_count` int(11) NOT NULL DEFAULT 0 COMMENT '使用次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_topic_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物话题标签表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pet_topic
-- ----------------------------
INSERT INTO `pet_topic` VALUES (1, '可爱萌宠', 0, '2026-05-03 22:07:57');
INSERT INTO `pet_topic` VALUES (2, '铲屎官日常', 0, '2026-05-03 22:07:57');
INSERT INTO `pet_topic` VALUES (3, '萌宠日记', 0, '2026-05-03 22:07:57');
INSERT INTO `pet_topic` VALUES (4, '宠物穿搭', 0, '2026-05-03 22:07:57');
INSERT INTO `pet_topic` VALUES (5, '吃货一枚', 0, '2026-05-03 22:07:57');
INSERT INTO `pet_topic` VALUES (6, '家有毛孩子', 0, '2026-05-03 22:07:57');
INSERT INTO `pet_topic` VALUES (7, '养宠心得', 0, '2026-05-03 22:07:57');
INSERT INTO `pet_topic` VALUES (8, '宠物故事', 0, '2026-05-03 22:07:57');

-- ----------------------------
-- Table structure for rescue
-- ----------------------------
DROP TABLE IF EXISTS `rescue`;
CREATE TABLE `rescue`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '救助记录ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '名称',
  `addres` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `img` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '照片',
  `person` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '联系人',
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `information` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '相关描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci COMMENT = '流浪动物救助表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of rescue
-- ----------------------------
INSERT INTO `rescue` VALUES (1, '阳光宠物救助中心', '广州市天河区珠江新城花城大道88号', '/file/0386be8a1a8e4e45a509eeea167cdb35.png', '李明', '020-88881234', '专业流浪动物救助机构，提供临时收容、医疗救治、领养服务');
INSERT INTO `rescue` VALUES (2, '爱心流浪动物之家', '广州市白云区同和街云景花园12栋', '/file/e4ddaa6fbbbe4a8a8b9384678c3f7b1e.png', '王芳', '020-88882345', '致力于流浪猫狗救助，提供绝育、疫苗、领养一体化服务');
INSERT INTO `rescue` VALUES (3, '城市毛孩子救助站', '广州市番禺区大学城外环西路28号', '/file/3fc9adaea3d44949ae51b87106a70c3a.png', '张伟', '020-88883456', '高校周边专业救助站，志愿者活跃，定期举办领养活动');
INSERT INTO `rescue` VALUES (4, '萌爪社区救助点', '广州市越秀区北京路步行街128号', '/file/69624a81ac594329bbbe92bcf4840b2a.png', '陈静', '020-88884567', '市区中心救助点，方便市民发现流浪动物后及时联系');
INSERT INTO `rescue` VALUES (5, '华南宠物医疗救助中心', '广州市海珠区新港东路135号', '/file/6a637ac16f624363bbe1e2b726163ce8.png', '刘强', '020-88885678', '具备医疗资质的专业救助中心，可提供重伤流浪动物的紧急救治');
INSERT INTO `rescue` VALUES (6, '温馨港湾动物救助站', '广州市黄埔区科学城开创大道66号', '/file/2d5dba928bf241fa99e5f2a18b96eb64.png', '周琳', '020-88886789', '科园区企业爱心共建救助站，为园区流浪动物提供庇护');
INSERT INTO `rescue` VALUES (7, '流浪动物临时安置点', '广州市荔湾区中山七路68号', '/file/4df7e115b3fb4aaa842aaf4fc8c9e35c.png', '赵军', '020-88887890', '社区合作救助点，为周边流浪动物提供紧急安置服务');
INSERT INTO `rescue` VALUES (8, '爱心宠物收容所', '广州市花都区狮岭镇皮革城旁', '/file/81b1b521c1274ffc94453fd6a0fe1103.png', '孙燕', '020-88888901', '乡镇级救助中心，接收周边走失及流浪宠物');

-- ----------------------------
-- Table structure for service_item
-- ----------------------------
DROP TABLE IF EXISTS `service_item`;
CREATE TABLE `service_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '服务项目ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务名称',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'BATH/BEAUTY/FOSTER/MEDICAL',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务描述',
  `price` decimal(10, 2) NOT NULL COMMENT '服务价格',
  `duration` int(11) NOT NULL COMMENT '服务时长（分钟）',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务图片',
  `status` int(11) NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
  `store_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '门店名称',
  `store_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '门店联系电话',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_item
-- ----------------------------
INSERT INTO `service_item` VALUES (1, '洗澡', 'BATH', '专业宠物沐浴服务，包含清洗、吹干、耳朵清理和指甲修剪全套护理', 120.00, 60, NULL, 1, NULL, NULL, '2026-04-22 13:30:30', '2026-05-01 23:28:16');
INSERT INTO `service_item` VALUES (2, '驱虫', 'MEDICAL', '驱虫', 100.00, 66, NULL, 1, NULL, NULL, '2026-04-22 17:10:45', '2026-04-22 17:10:45');
INSERT INTO `service_item` VALUES (3, '剃毛', 'BEAUTY', '专业宠物剃毛造型服务，根据主人需求打造可爱造型，清爽过夏', 200.00, 30, NULL, 1, '南华大学笃行园', '15574751679', '2026-04-22 21:18:41', '2026-05-01 23:28:16');
INSERT INTO `service_item` VALUES (4, '宠物基础洗澡', 'BATH', '专业宠物沐浴，清洗吹干，包括耳朵清理和指甲修剪', 80.00, 60, NULL, 1, '萌爪宠物SPA', '021-88880001', '2026-01-26 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (5, '猫咪专属洗澡', 'BATH', '专为猫咪设计的温和洗浴，减压去油，舒适吹干', 100.00, 60, NULL, 1, '萌爪宠物SPA', '021-88880001', '2026-02-25 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (6, '大型犬深度洗护', 'BATH', '适合金毛、哈士奇等大型犬，彻底清洁除虫', 150.00, 90, NULL, 1, '汪星人宠物店', '021-88880002', '2026-03-12 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (7, '小型犬精致洗浴', 'BATH', '适合泰迪、比熊等小型犬，精油护理，毛发柔顺', 90.00, 60, NULL, 1, '汪星人宠物店', '021-88880002', '2026-03-27 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (8, '宠物SPA泡泡浴', 'BATH', '奢华泡泡浴体验，深层清洁加香薰放松', 180.00, 90, NULL, 1, '星级宠物会所', '021-88880003', '2026-04-11 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (9, '幼犬温和洗澡', 'BATH', '专为3个月以上幼犬设计的温和洗浴套餐', 70.00, 45, NULL, 1, '萌爪宠物SPA', '021-88880001', '2026-04-16 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (10, '除臭深层清洁浴', 'BATH', '针对体味较重的宠物，深层清洁除臭', 120.00, 75, NULL, 1, '汪星人宠物店', '021-88880002', '2026-04-21 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (11, '会员专享洗澡卡', 'BATH', '10次洗澡服务，平均每次仅需60元', 600.00, 60, NULL, 1, '星级宠物会所', '021-88880003', '2026-04-24 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (12, '宠物精致美容', 'BEAUTY', '全套美容服务，包括洗澡、造型、指甲修剪等', 150.00, 120, NULL, 1, '星级宠物会所', '021-88880003', '2026-01-26 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (13, '泰迪造型美容', 'BEAUTY', '专业泰迪熊造型，精剪染色，打造萌宠形象', 200.00, 150, NULL, 1, '萌爪宠物SPA', '021-88880001', '2026-02-25 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (14, '比熊专属美容', 'BEAUTY', '比熊犬专业造型，白毛护理，圆润可爱', 180.00, 120, NULL, 1, '汪星人宠物店', '021-88880002', '2026-03-12 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (15, '宠物毛发护理', 'BEAUTY', '深层毛发护理，开结蓬松，丝滑亮泽', 120.00, 90, NULL, 1, '星级宠物会所', '021-88880003', '2026-03-27 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (16, '创意染色造型', 'BEAUTY', '安全无刺激宠物专用染料，多种颜色可选', 280.00, 180, NULL, 1, '萌爪宠物SPA', '021-88880001', '2026-04-06 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (17, '宠物美甲服务', 'BEAUTY', '专业指甲修剪+打磨抛光+彩色甲油', 80.00, 45, NULL, 1, '汪星人宠物店', '021-88880002', '2026-04-11 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (18, '赛级美容套餐', 'BEAUTY', '专业赛级美容，含洗澡、造型、护理全套', 350.00, 180, NULL, 1, '星级宠物会所', '021-88880003', '2026-04-18 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (19, '猫咪美容SPA', 'BEAUTY', '猫咪专属美容套餐，减压按摩毛发护理', 160.00, 100, NULL, 1, '萌爪宠物SPA', '021-88880001', '2026-04-23 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (20, '宠物舒适寄养', 'FOSTER', '单犬单间，24小时监控，每日遛弯3次', 120.00, 1440, NULL, 1, '星级宠物会所', '021-88880003', '2026-01-26 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (21, '猫咪独立公寓', 'FOSTER', '猫咪专属寄养空间，爬架玩具齐全', 80.00, 1440, NULL, 1, '萌爪宠物SPA', '021-88880001', '2026-02-25 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (22, '豪华寄养套房', 'FOSTER', '超大独立空间，空调恒温，实时视频', 200.00, 1440, NULL, 1, '汪星人宠物店', '021-88880002', '2026-03-12 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (23, '家庭式寄养', 'FOSTER', '温馨家庭环境，让宠物感受家的温暖', 100.00, 1440, NULL, 1, '星级宠物会所', '021-88880003', '2026-03-27 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (24, '假期宠物托管', 'FOSTER', '节假日专用套餐，含早晚各一次遛狗服务', 150.00, 1440, NULL, 1, '汪星人宠物店', '021-88880002', '2026-04-06 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (25, '老年宠物护理寄养', 'FOSTER', '专为老年宠物设计，特殊护理，按时喂药', 180.00, 1440, NULL, 1, '萌爪宠物SPA', '021-88880001', '2026-04-16 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (26, 'VIP独立花园寄养', 'FOSTER', '带私家花园的独立别墅，无限量遛狗', 280.00, 1440, NULL, 1, '星级宠物会所', '021-88880003', '2026-04-21 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (27, '短时托管服务', 'FOSTER', '4小时内的临时托管，适合主人临时外出', 50.00, 240, NULL, 1, '汪星人宠物店', '021-88880002', '2026-04-24 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (28, '宠物全面体检', 'MEDICAL', '专业兽医问诊，血常规检测，寄生虫检查', 200.00, 60, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-01-26 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (29, '疫苗接种服务', 'MEDICAL', '狂犬疫苗+传染病疫苗，专业注射', 100.00, 30, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-02-25 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (30, '体内驱虫套餐', 'MEDICAL', '全面体内驱虫，保护宠物肠道健康', 80.00, 30, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-03-12 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (31, '体外驱虫服务', 'MEDICAL', '驱除跳蚤、蜱虫等体外寄生虫', 60.00, 30, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-03-27 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (32, '宠物洁牙护理', 'MEDICAL', '超声波洁牙，抛光护理，预防牙病', 300.00, 60, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-04-06 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (33, '眼科专项检查', 'MEDICAL', '专业宠物眼科检查，泪腺、耳道检测', 150.00, 45, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-04-11 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (34, '皮肤科诊疗', 'MEDICAL', '宠物皮肤病诊断，对症治疗', 120.00, 45, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-04-16 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (35, '老年宠物体检', 'MEDICAL', '专为7岁以上宠物设计的全面体检套餐', 350.00, 90, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-04-21 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (36, '疫苗加强针', 'MEDICAL', '宠物疫苗加强针注射，巩固免疫', 90.00, 30, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-04-24 22:53:11', '2026-04-26 22:53:11');
INSERT INTO `service_item` VALUES (37, '绝育手术套餐', 'MEDICAL', '专业绝育手术，含麻醉、术后消炎', 500.00, 120, NULL, 1, '爱宠宠物医院', '021-88880004', '2026-04-25 22:53:11', '2026-04-26 22:53:11');

-- ----------------------------
-- Table structure for service_order
-- ----------------------------
DROP TABLE IF EXISTS `service_order`;
CREATE TABLE `service_order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `service_id` int(11) NOT NULL COMMENT '服务项目ID',
  `pet_id` int(11) NOT NULL COMMENT '宠物ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `appointment_time` datetime NOT NULL COMMENT '预约时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/CONFIRMED/COMPLETED/CANCELLED',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注/取消原因',
  `confirm_time` datetime NULL DEFAULT NULL COMMENT '确认时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime NULL DEFAULT NULL COMMENT '取消时间',
  `store_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预约地点/门店名称',
  `store_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE,
  INDEX `idx_service_time`(`service_id`, `appointment_time`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_pet_id`(`pet_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_service_id`(`service_id`) USING BTREE,
  INDEX `idx_appointment_time`(`appointment_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 236 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '预约订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_order
-- ----------------------------
INSERT INTO `service_order` VALUES (100, 'SO2026040001', 5, 25, 7, '2026-03-27 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-03-27 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (101, 'SO2026040002', 1, 26, 7, '2026-04-01 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-01 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (102, 'SO2026040003', 8, 27, 7, '2026-04-06 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-06 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (103, 'SO2026040004', 5, 25, 7, '2026-04-11 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-11 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (104, 'SO2026040005', 1, 26, 7, '2026-04-16 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-16 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (105, 'SO2026040006', 13, 28, 8, '2026-03-29 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-03-29 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (106, 'SO2026040007', 14, 29, 8, '2026-04-03 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-03 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (107, 'SO2026040008', 13, 28, 8, '2026-04-08 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-08 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (108, 'SO2026040009', 14, 29, 8, '2026-04-13 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-13 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (109, 'SO2026040010', 13, 28, 8, '2026-04-18 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-18 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (110, 'SO2026040011', 18, 28, 8, '2026-04-23 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-23 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (111, 'SO2026040012', 28, 30, 9, '2026-03-30 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-03-30 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (112, 'SO2026040013', 29, 30, 9, '2026-04-04 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-04 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (113, 'SO2026040014', 30, 30, 9, '2026-04-09 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-09 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (114, 'SO2026040015', 28, 30, 9, '2026-04-19 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-19 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (115, 'SO2026040016', 30, 30, 9, '2026-04-24 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-24 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (116, 'SO2026040017', 20, 31, 10, '2026-03-31 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-03-31 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (117, 'SO2026040018', 22, 31, 10, '2026-04-05 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-05 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (118, 'SO2026040019', 20, 31, 10, '2026-04-10 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-10 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (119, 'SO2026040020', 22, 31, 10, '2026-04-20 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-20 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (120, 'SO2026040021', 6, 32, 11, '2026-04-02 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-02 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (121, 'SO2026040022', 28, 32, 11, '2026-04-07 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-07 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (122, 'SO2026040023', 12, 32, 11, '2026-04-12 23:41:24', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-12 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (123, 'SO2026040024', 29, 32, 11, '2026-04-27 23:41:24', 'COMPLETED', NULL, NULL, '2026-04-29 16:49:58', NULL, '爱宠宠物医院', NULL, '2026-04-26 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (124, 'SO2026040025', 13, 32, 11, '2026-04-29 23:41:24', 'PENDING', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-26 23:41:24', '2026-04-26 23:41:24');
INSERT INTO `service_order` VALUES (125, '202604270114156587', 37, 27, 7, '2026-04-28 00:00:00', 'CANCELLED', '', NULL, NULL, '2026-04-27 02:28:19', '爱宠宠物医院', '021-88880004', '2026-04-27 01:14:15', '2026-04-27 01:14:15');
INSERT INTO `service_order` VALUES (126, '202604270226012480', 31, 27, 7, '2026-04-27 15:25:44', 'CANCELLED', '', NULL, NULL, '2026-04-27 02:28:18', '爱宠宠物医院', '021-88880004', '2026-04-27 02:26:01', '2026-04-27 02:26:01');
INSERT INTO `service_order` VALUES (127, '202604270228355403', 36, 26, 7, '2026-04-27 14:28:27', 'CANCELLED', '', NULL, NULL, '2026-04-27 02:30:04', '爱宠宠物医院', '021-88880004', '2026-04-27 02:28:35', '2026-04-27 02:28:35');
INSERT INTO `service_order` VALUES (128, '202604270229179088', 37, 27, 7, '2026-04-28 00:00:00', 'CANCELLED', '', NULL, NULL, '2026-04-27 02:30:50', '爱宠宠物医院', '021-88880004', '2026-04-27 02:29:17', '2026-04-27 02:29:17');
INSERT INTO `service_order` VALUES (129, '202604270231001918', 33, 26, 7, '2026-04-28 00:00:00', 'PENDING', '', NULL, NULL, NULL, '爱宠宠物医院', '021-88880004', '2026-04-27 02:31:00', '2026-04-27 02:31:00');
INSERT INTO `service_order` VALUES (130, '202604291749079237', 37, 27, 7, '2026-04-30 00:00:00', 'CANCELLED', '', NULL, NULL, '2026-04-29 17:49:59', '爱宠宠物医院', '021-88880004', '2026-04-29 17:49:07', '2026-04-29 17:49:07');
INSERT INTO `service_order` VALUES (131, '202604291751322064', 37, 26, 7, '2026-04-30 23:00:00', 'PENDING', '', NULL, NULL, NULL, '爱宠宠物医院', '021-88880004', '2026-04-29 17:51:32', '2026-04-29 17:51:32');
INSERT INTO `service_order` VALUES (200, 'TEST140001', 1, 26, 7, '2026-04-20 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-20 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (201, 'TEST140002', 5, 25, 7, '2026-04-20 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-20 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (202, 'TEST140003', 8, 27, 8, '2026-04-20 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-20 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (203, 'TEST140004', 13, 28, 8, '2026-04-20 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-20 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (204, 'TEST140005', 14, 29, 9, '2026-04-20 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-20 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (205, 'TEST140006', 18, 28, 7, '2026-04-21 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-21 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (206, 'TEST140007', 28, 30, 8, '2026-04-21 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-21 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (207, 'TEST140008', 29, 30, 9, '2026-04-21 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-21 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (208, 'TEST140009', 30, 30, 10, '2026-04-21 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-21 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (209, 'TEST140010', 20, 31, 11, '2026-04-21 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-21 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (210, 'TEST140011', 22, 31, 7, '2026-04-22 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-22 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (211, 'TEST140012', 6, 32, 8, '2026-04-22 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-22 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (212, 'TEST140013', 28, 32, 9, '2026-04-22 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-22 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (213, 'TEST140014', 12, 32, 10, '2026-04-22 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-22 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (214, 'TEST140015', 29, 32, 11, '2026-04-22 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-22 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (215, 'TEST140016', 1, 26, 7, '2026-04-23 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-23 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (216, 'TEST140017', 5, 25, 8, '2026-04-23 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-23 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (217, 'TEST140018', 8, 27, 9, '2026-04-23 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-23 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (218, 'TEST140019', 13, 28, 10, '2026-04-23 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-23 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (219, 'TEST140020', 14, 29, 7, '2026-04-24 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-24 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (220, 'TEST140021', 18, 28, 8, '2026-04-24 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-24 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (221, 'TEST140022', 28, 30, 9, '2026-04-24 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-24 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (222, 'TEST140023', 29, 30, 10, '2026-04-24 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-24 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (223, 'TEST140024', 30, 30, 7, '2026-04-25 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-25 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (224, 'TEST140025', 20, 31, 8, '2026-04-25 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-25 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (225, 'TEST140026', 22, 31, 9, '2026-04-25 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-25 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (226, 'TEST140027', 6, 32, 10, '2026-04-25 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '汪星人宠物店', NULL, '2026-04-25 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (227, 'TEST140028', 28, 32, 7, '2026-04-26 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-26 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (228, 'TEST140029', 12, 32, 8, '2026-04-26 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '星级宠物会所', NULL, '2026-04-26 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (229, 'TEST140030', 29, 32, 9, '2026-04-26 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '爱宠宠物医院', NULL, '2026-04-26 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (230, 'TEST140031', 13, 28, 10, '2026-04-26 14:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-26 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (231, 'TEST090001', 1, 26, 7, '2026-04-25 09:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-25 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (232, 'TEST090002', 5, 25, 8, '2026-04-25 09:00:00', 'COMPLETED', NULL, NULL, NULL, NULL, '萌爪宠物SPA', NULL, '2026-04-25 00:00:00', '2026-04-26 00:00:00');
INSERT INTO `service_order` VALUES (233, '202605222137012176', 11, 26, 7, '2026-05-23 08:00:00', 'PENDING', '', NULL, NULL, NULL, '星级宠物会所', '021-88880003', '2026-05-22 21:37:01', '2026-05-22 21:37:01');
INSERT INTO `service_order` VALUES (234, '202605222142053471', 37, 26, 7, '2026-05-23 00:00:00', 'CONFIRMED', '', '2026-05-22 21:43:47', NULL, NULL, '爱宠宠物医院', '021-88880004', '2026-05-22 21:42:05', '2026-05-22 21:42:05');
INSERT INTO `service_order` VALUES (235, '202605222151238974', 19, 25, 7, '2026-05-23 00:00:00', 'CONFIRMED', '', '2026-05-22 21:53:14', NULL, NULL, '萌爪宠物SPA', '021-88880001', '2026-05-22 21:51:23', '2026-05-22 21:51:23');

-- ----------------------------
-- Table structure for service_review
-- ----------------------------
DROP TABLE IF EXISTS `service_review`;
CREATE TABLE `service_review`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` int(11) NULL DEFAULT NULL COMMENT '订单ID',
  `service_id` int(11) NULL DEFAULT NULL COMMENT '服务项目ID',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '用户ID',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `rating` int(11) NULL DEFAULT NULL COMMENT '评分(1-5星)',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `reply` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商家回复',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 83 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务评价' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_review
-- ----------------------------
INSERT INTO `service_review` VALUES (1, 25, 1, 7, 'user1', 5, '服务非常专业！洗得很干净，还帮忙清理了耳朵，推荐给大家！', '2026-04-10 10:30:00', '感谢您的认可！', '2026-04-10 11:00:00');
INSERT INTO `service_review` VALUES (2, 26, 1, 8, 'user2', 4, '洗得挺干净的，就是等待时间稍微有点长', '2026-04-12 14:00:00', '我们会改进服务效率，感谢反馈！', '2026-04-12 15:00:00');
INSERT INTO `service_review` VALUES (3, NULL, 1, 9, 'user3', 5, '第一次来这家店，体验很好，美容师很有耐心，下次还来！', '2026-04-15 16:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (4, 27, 1, 10, 'user4', 5, '我家金毛特别怕水，但是美容师 handling 得很好，完全没有应激', '2026-04-18 11:30:00', '谢谢您的信任！', '2026-04-18 12:00:00');
INSERT INTO `service_review` VALUES (5, 28, 2, 7, 'user1', 5, '美容效果超出预期！毛发剪得很漂亮，我家狗子瞬间变帅了', '2026-04-08 10:00:00', '谢谢夸奖！', '2026-04-08 11:00:00');
INSERT INTO `service_review` VALUES (6, 29, 2, 8, 'user2', 5, '非常满意！指甲修剪得很仔细，还帮剃了脚底毛', '2026-04-14 15:00:00', '感谢支持！', '2026-04-14 16:00:00');
INSERT INTO `service_review` VALUES (7, NULL, 2, 11, 'user5', 4, '服务不错，就是价格稍微贵了一点点', '2026-04-20 13:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (8, 30, 3, 7, 'user1', 5, '医生很专业，讲解很详细，猫咪打完疫苗没有任何不适', '2026-04-05 09:00:00', '祝猫咪健康成长！', '2026-04-05 10:00:00');
INSERT INTO `service_review` VALUES (9, 31, 3, 8, 'user2', 5, '环境干净卫生，疫苗来源正规，很放心', '2026-04-11 14:00:00', '感谢选择我们！', '2026-04-11 15:00:00');
INSERT INTO `service_review` VALUES (10, NULL, 3, 9, 'user3', 5, '医生很有爱心，对待小动物很温柔，强烈推荐！', '2026-04-16 11:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (11, 32, 3, 10, 'user4', 4, '整体不错，就是预约有点难约到时间', '2026-04-22 16:00:00', '我们会增加预约时段，感谢反馈！', '2026-04-22 17:00:00');
INSERT INTO `service_review` VALUES (12, 33, 4, 7, 'user1', 5, '体检项目很全面，医生给了很多养宠建议，非常实用', '2026-04-06 10:00:00', '很高兴能帮到您！', '2026-04-06 11:00:00');
INSERT INTO `service_review` VALUES (13, 34, 4, 8, 'user2', 5, '很专业！发现了狗狗的轻微牙齿问题，及时处理了', '2026-04-13 14:30:00', '定期体检很重要哦！', '2026-04-13 15:00:00');
INSERT INTO `service_review` VALUES (14, NULL, 4, 11, 'user5', 5, '体检报告写得很详细，感觉物超所值', '2026-04-19 09:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (15, 35, 5, 7, 'user1', 5, '驱虫效果很好，用的是进口驱虫药，安全放心', '2026-04-07 11:00:00', '感谢好评！', '2026-04-07 12:00:00');
INSERT INTO `service_review` VALUES (16, 36, 5, 9, 'user3', 5, '医生手法很轻柔，猫咪一点都不紧张，好评！', '2026-04-15 15:00:00', '谢谢！', '2026-04-15 16:00:00');
INSERT INTO `service_review` VALUES (17, NULL, 5, 10, 'user4', 4, '服务不错，但是驱虫后要注意观察猫咪状态', '2026-04-21 10:00:00', '是的，观察期间有任何问题随时联系我们！', '2026-04-21 11:00:00');
INSERT INTO `service_review` VALUES (18, 37, 6, 8, 'user2', 5, '大型犬洗护做得很好，很耐心，洗完香喷喷的', '2026-04-09 14:00:00', '谢谢您的认可！', '2026-04-09 15:00:00');
INSERT INTO `service_review` VALUES (19, NULL, 6, 10, 'user4', 5, '我家萨摩耶110斤，这里也能洗得很干净，太厉害了！', '2026-04-17 10:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (20, 38, 6, 11, 'user5', 4, '服务很专业，就是价格有点贵，希望能优惠一些', '2026-04-23 13:00:00', '感谢反馈，我们会有会员优惠活动哦！', '2026-04-23 14:00:00');
INSERT INTO `service_review` VALUES (21, 39, 7, 7, 'user1', 5, '寄养环境很好，有专人照顾，还每天发视频，让我们很安心', '2026-04-12 16:00:00', '感谢信任！', '2026-04-12 17:00:00');
INSERT INTO `service_review` VALUES (22, 40, 7, 9, 'user3', 5, '猫咪在寄养期间吃得好睡得好，回来还胖了一圈', '2026-04-20 11:00:00', '哈哈，吃得好我们就放心了！', '2026-04-20 12:00:00');
INSERT INTO `service_review` VALUES (23, NULL, 7, 11, 'user5', 4, '整体不错，但是猫咪好像有点认生，适应了几天才好', '2026-04-25 09:30:00', '每个宠物适应期不同，我们会更加耐心照顾！', '2026-04-25 10:00:00');
INSERT INTO `service_review` VALUES (24, 41, 8, 7, 'user1', 5, '泡泡浴太舒服了！猫咪做完SPA毛发蓬松发亮，太赞了', '2026-04-14 10:00:00', '感谢好评！', '2026-04-14 11:00:00');
INSERT INTO `service_review` VALUES (25, NULL, 8, 8, 'user2', 5, '超级推荐！按摩手法很专业，猫咪舒服得都睡着了', '2026-04-18 15:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (26, 42, 8, 9, 'user3', 5, '环境很好，有香薰，猫咪很放松，做完精神奕奕的', '2026-04-22 14:00:00', '谢谢您的支持！', '2026-04-22 15:00:00');
INSERT INTO `service_review` VALUES (27, 100, 5, 7, 'user1', 5, '环境很不错，服务人员很细心，我家猫咪洗完澡毛发很蓬松，下次还会再来！', '2026-04-10 11:00:00', '感谢您的认可，欢迎下次光临！', '2026-04-10 12:00:00');
INSERT INTO `service_review` VALUES (28, 101, 1, 7, 'user1', 5, '洗澡服务很专业，还帮猫咪清理了耳垢和剪了指甲，体验很好！', '2026-04-15 14:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (29, 102, 8, 7, 'user1', 4, '泡泡浴真的很舒服，猫咪做完SPA毛发蓬松有光泽，就是价格略贵', '2026-04-20 10:30:00', '感谢反馈，我们会推出更多优惠活动！', '2026-04-20 11:00:00');
INSERT INTO `service_review` VALUES (30, 103, 5, 7, 'user1', 5, '第二次来了，依旧很满意，店员对宠物很有爱心，推荐！', '2026-04-25 15:00:00', '谢谢支持！', '2026-04-25 16:00:00');
INSERT INTO `service_review` VALUES (31, 104, 1, 7, 'user1', 5, '洗得很干净，香香的，猫咪也不再抗拒洗澡了，感谢！', '2026-04-30 09:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (32, 105, 13, 8, 'user2', 5, '医生很有耐心，检查得很仔细，给了很多养宠建议，点赞！', '2026-04-02 10:00:00', '祝宠物健康成长！', '2026-04-02 11:00:00');
INSERT INTO `service_review` VALUES (33, 106, 14, 8, 'user2', 5, '美容效果很好，毛发修剪得很漂亮，造型师很专业！', '2026-04-08 15:30:00', '感谢好评！', '2026-04-08 16:00:00');
INSERT INTO `service_review` VALUES (34, 107, 13, 8, 'user2', 4, '服务不错，就是等待时间有点长，希望能提高预约效率', '2026-04-13 14:00:00', '感谢反馈，我们会持续优化流程！', '2026-04-13 15:00:00');
INSERT INTO `service_review` VALUES (35, 108, 14, 8, 'user2', 5, '非常满意！指甲修剪得很仔细，脚底毛也剃得很干净', '2026-04-18 16:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (36, 109, 13, 8, 'user2', 5, '环境干净卫生，设备很专业，医生的手法也很轻柔，好评！', '2026-04-23 11:00:00', '谢谢您的认可！', '2026-04-23 11:30:00');
INSERT INTO `service_review` VALUES (37, 110, 18, 8, 'user2', 5, '寄养服务超出预期，每天都会发宠物状态视频，很放心！', '2026-04-28 10:00:00', '感谢信任！', '2026-04-28 10:30:00');
INSERT INTO `service_review` VALUES (38, 111, 28, 9, 'user3', 5, '医生专业度很高，详细讲解了宠物的健康状况，感觉很靠谱', '2026-04-03 09:30:00', '祝宠物健康！', '2026-04-03 10:00:00');
INSERT INTO `service_review` VALUES (39, 112, 29, 9, 'user3', 5, '检查很全面，还拍了片子，医生解读很详细，赞！', '2026-04-08 14:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (40, 113, 30, 9, 'user3', 5, '设备很先进，环境也很温馨，医护人员的态度非常好', '2026-04-13 11:00:00', '感谢支持！', '2026-04-13 12:00:00');
INSERT INTO `service_review` VALUES (41, 114, 28, 9, 'user3', 4, '服务很好，就是费用比预期高了一点，希望能有些优惠活动', '2026-04-23 15:00:00', '我们即将推出会员卡优惠，敬请期待！', '2026-04-23 16:00:00');
INSERT INTO `service_review` VALUES (42, 115, 30, 9, 'user3', 5, '非常满意！做完全面检查后医生给了详细的养护方案，很实用', '2026-04-28 10:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (43, 116, 20, 10, 'user4', 5, '寄养环境太好了，有空调有玩具，猫咪住得很开心！', '2026-04-05 11:00:00', '谢谢您的认可！', '2026-04-05 12:00:00');
INSERT INTO `service_review` VALUES (44, 117, 22, 10, 'user4', 5, '高级美容套餐很值得，服务很细致，连爪子都护理到了', '2026-04-10 15:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (45, 118, 20, 10, 'user4', 5, '第二次寄养了，依旧很满意，工作人员对宠物很有耐心', '2026-04-15 10:30:00', '感谢再次选择我们！', '2026-04-15 11:00:00');
INSERT INTO `service_review` VALUES (46, 119, 22, 10, 'user4', 4, '效果不错，但是预约的时间有点难约，希望能增加时段', '2026-04-25 14:00:00', '我们会逐步增加预约时段，感谢反馈！', '2026-04-25 15:00:00');
INSERT INTO `service_review` VALUES (47, 120, 6, 11, 'user5', 5, '大型犬洗护做得很好，很耐心，洗完毛发光亮，很满意！', '2026-04-06 10:00:00', '感谢好评！', '2026-04-06 11:00:00');
INSERT INTO `service_review` VALUES (48, 121, 28, 11, 'user5', 5, '医生讲解非常详细，给了很实用的养宠建议，物超所值', '2026-04-11 14:30:00', NULL, NULL);
INSERT INTO `service_review` VALUES (49, 122, 12, 11, 'user5', 4, '服务很专业，就是当天人有点多，稍微等了一会儿', '2026-04-16 11:00:00', '感谢理解，我们正在优化预约流程！', '2026-04-16 12:00:00');
INSERT INTO `service_review` VALUES (50, 200, 1, 7, 'user1', 5, '洗澡服务一如既往地好，猫咪洗完很舒服，毛发顺滑有光泽', '2026-04-24 10:00:00', '感谢认可！', '2026-04-24 11:00:00');
INSERT INTO `service_review` VALUES (51, 201, 5, 7, 'user1', 5, '驱虫服务很专业，用的是进口药物，猫咪没有任何不适，很放心', '2026-04-24 14:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (52, 202, 8, 8, 'user2', 5, '泡泡浴太赞了，猫咪做完SPA精神奕奕，毛发光亮，效果超出预期', '2026-04-24 15:00:00', '谢谢好评！', '2026-04-24 16:00:00');
INSERT INTO `service_review` VALUES (53, 203, 13, 8, 'user2', 5, '医生很专业，检查得很仔细，还给了很多养宠建议，很实用', '2026-04-24 16:00:00', '祝宠物健康！', '2026-04-24 17:00:00');
INSERT INTO `service_review` VALUES (54, 204, 14, 9, 'user3', 5, '美容师技术很好，造型很满意，价格也合理，下次还来！', '2026-04-24 17:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (55, 205, 18, 7, 'user1', 5, '寄养服务很周到，每天都发视频，猫咪适应得很好，强烈推荐！', '2026-04-25 10:00:00', '感谢信任！', '2026-04-25 11:00:00');
INSERT INTO `service_review` VALUES (56, 206, 28, 8, 'user2', 5, '体检很全面，报告写得很详细，医生讲解耐心，很专业的服务', '2026-04-25 14:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (57, 207, 29, 9, 'user3', 5, '检查设备很先进，医生手法轻柔，猫咪一点都不紧张，好评！', '2026-04-25 15:00:00', '谢谢！', '2026-04-25 16:00:00');
INSERT INTO `service_review` VALUES (58, 208, 30, 10, 'user4', 5, '全面检查很值得，发现了潜在问题并及时处理，服务很专业', '2026-04-25 16:00:00', '定期体检很重要，感谢认可！', '2026-04-25 17:00:00');
INSERT INTO `service_review` VALUES (59, 209, 20, 11, 'user5', 5, '寄养环境很温馨，有空调和玩具，工作人员很有爱心，猫咪住得很开心', '2026-04-25 17:00:00', '感谢好评！', '2026-04-25 18:00:00');
INSERT INTO `service_review` VALUES (60, 210, 22, 7, 'user1', 5, '高级美容套餐物超所值，从毛发到指甲都护理得很仔细，很满意', '2026-04-26 10:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (61, 211, 6, 8, 'user2', 5, '大型犬洗护服务很专业，工作人员很有耐心，洗得干干净净，好评！', '2026-04-26 14:00:00', '感谢支持！', '2026-04-26 15:00:00');
INSERT INTO `service_review` VALUES (62, 212, 28, 9, 'user3', 5, '医生专业度很高，检查后发现了一些小问题及时处理了，很满意', '2026-04-26 15:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (63, 213, 12, 10, 'user4', 5, '服务很到位，美容师很有经验，造型做得很好，猫咪变帅了！', '2026-04-26 16:00:00', '谢谢！', '2026-04-26 17:00:00');
INSERT INTO `service_review` VALUES (64, 214, 29, 11, 'user5', 5, '检查得很细致，报告写得很详细，感觉物超所值，很推荐！', '2026-04-26 17:00:00', '感谢好评！', '2026-04-26 18:00:00');
INSERT INTO `service_review` VALUES (65, 215, 1, 7, 'user1', 5, '预约很方便，服务一如既往地好，店员态度热情，很满意！', '2026-04-27 10:00:00', '感谢支持！', '2026-04-27 11:00:00');
INSERT INTO `service_review` VALUES (66, 216, 5, 8, 'user2', 5, '洗护效果很好，毛发顺滑有光泽，性价比高，推荐！', '2026-04-27 14:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (67, 217, 8, 9, 'user3', 5, '泡泡浴太舒服了，猫咪做完毛发蓬松发亮，还会再来！', '2026-04-27 15:30:00', '谢谢！', '2026-04-27 16:00:00');
INSERT INTO `service_review` VALUES (68, 218, 13, 10, 'user4', 5, '医生很专业，检查得很仔细，环境也很干净，很放心', '2026-04-27 16:00:00', '祝宠物健康！', '2026-04-27 17:00:00');
INSERT INTO `service_review` VALUES (69, 219, 14, 7, 'user1', 4, '美容效果不错，造型师很用心，就是价格小贵', '2026-04-28 10:00:00', '感谢反馈，会有更多优惠活动！', '2026-04-28 10:30:00');
INSERT INTO `service_review` VALUES (70, 220, 18, 8, 'user2', 5, '寄养期间每天都有视频反馈，猫咪吃得好睡得好，很安心！', '2026-04-28 14:00:00', '感谢信任！', '2026-04-28 15:00:00');
INSERT INTO `service_review` VALUES (71, 221, 28, 9, 'user3', 5, '体检项目全面，医生专业又耐心，给了详细的养护方案', '2026-04-28 15:30:00', NULL, NULL);
INSERT INTO `service_review` VALUES (72, 222, 29, 10, 'user4', 5, '检查得很细致，还发现了一些小问题及时处理了，很负责', '2026-04-28 16:00:00', '定期体检很重要，感谢认可！', '2026-04-28 17:00:00');
INSERT INTO `service_review` VALUES (73, 223, 30, 7, 'user1', 5, '设备很先进，医生讲解详细，整体体验非常好，强烈推荐！', '2026-04-29 10:00:00', '感谢好评！', '2026-04-29 11:00:00');
INSERT INTO `service_review` VALUES (74, 224, 20, 8, 'user2', 5, '寄养环境干净整洁，有专人照顾，还给猫咪准备了玩具，很贴心', '2026-04-29 14:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (75, 225, 22, 9, 'user3', 5, '高级美容套餐超值得，毛发护理得很到位，猫咪变美美的！', '2026-04-29 15:00:00', '谢谢您的认可！', '2026-04-29 16:00:00');
INSERT INTO `service_review` VALUES (76, 226, 6, 10, 'user4', 5, '大型犬洗护服务很专业，店员很有耐心，赞一个！', '2026-04-29 16:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (77, 227, 28, 7, 'user1', 5, '医生的专业度很高，检查后发现了一些小问题及时处理了，很满意', '2026-04-30 10:00:00', '定期检查很重要！', '2026-04-30 11:00:00');
INSERT INTO `service_review` VALUES (78, 228, 12, 8, 'user2', 4, '服务很好，环境也不错，就是当天人有点多，建议提前预约', '2026-04-30 14:00:00', '感谢反馈！', '2026-04-30 15:00:00');
INSERT INTO `service_review` VALUES (79, 229, 29, 9, 'user3', 5, '检查项目非常全面，医生给了很多实用的养宠建议，很值得', '2026-04-30 14:30:00', NULL, NULL);
INSERT INTO `service_review` VALUES (80, 230, 13, 10, 'user4', 5, '医护人员态度很好，对宠物很有爱心，检查也很仔细，好评！', '2026-04-30 15:00:00', '感谢支持！', '2026-04-30 16:00:00');
INSERT INTO `service_review` VALUES (81, 231, 1, 7, 'user1', 5, '一早来洗澡，人不多，服务很顺畅，猫咪洗完很舒服，值得推荐', '2026-04-29 10:00:00', NULL, NULL);
INSERT INTO `service_review` VALUES (82, 232, 5, 8, 'user2', 5, '驱虫服务很专业，用的药很好，猫咪没有任何不良反应，很满意', '2026-04-29 14:00:00', '感谢认可！', '2026-04-29 15:00:00');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('add', 'iconfont icon-r-add', 'icon');
INSERT INTO `sys_dict` VALUES ('edit', 'iconfont icon-r-edit', 'icon');
INSERT INTO `sys_dict` VALUES ('delete', 'iconfont icon-r-delete', 'icon');
INSERT INTO `sys_dict` VALUES ('find', 'iconfont icon-r-find', 'icon');
INSERT INTO `sys_dict` VALUES ('user1', 'iconfont icon-r-user1', 'icon');
INSERT INTO `sys_dict` VALUES ('user2', 'iconfont icon-r-user2', 'icon');
INSERT INTO `sys_dict` VALUES ('user3', 'iconfont icon-r-user3', 'icon');
INSERT INTO `sys_dict` VALUES ('team', 'iconfont icon-r-team', 'icon');
INSERT INTO `sys_dict` VALUES ('home', 'iconfont icon-r-home', 'icon');
INSERT INTO `sys_dict` VALUES ('buildin', 'iconfont icon-r-building', 'icon');
INSERT INTO `sys_dict` VALUES ('mark1', 'iconfont icon-r-mark1', 'icon');
INSERT INTO `sys_dict` VALUES ('mark2', 'iconfont icon-r-mark2', 'icon');
INSERT INTO `sys_dict` VALUES ('mark3', 'iconfont icon-r-mark3', 'icon');
INSERT INTO `sys_dict` VALUES ('shield', 'iconfont icon-r-shield', 'icon');
INSERT INTO `sys_dict` VALUES ('refresh', 'iconfont icon-r-refresh', 'icon');
INSERT INTO `sys_dict` VALUES ('love', 'iconfont icon-r-love', 'icon');
INSERT INTO `sys_dict` VALUES ('yes', 'iconfont icon-r-yes', 'icon');
INSERT INTO `sys_dict` VALUES ('no', 'iconfont icon-r-no', 'icon');
INSERT INTO `sys_dict` VALUES ('paper', 'iconfont icon-r-paper', 'icon');
INSERT INTO `sys_dict` VALUES ('list', 'iconfont icon-r-list', 'icon');
INSERT INTO `sys_dict` VALUES ('setting', 'iconfont icon-r-setting', 'icon');

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名称',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型',
  `size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小(kb)',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下载链接',
  `md5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件md5',
  `is_delete` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `enable` tinyint(1) NULL DEFAULT 1 COMMENT '是否禁用链接',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 204 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES (90, 'logo2.jpg', 'jpg', 635, '/file/d0f1c679738d4481a1aeb5fd45446982.jpg', '96564de9ed610acff96bc60b5d98e375', 0, 1);
INSERT INTO `sys_file` VALUES (91, '04.jpg', 'jpg', 616, '/file/bb7940b20bb54131a9e3e50cc25c34a4.jpg', '09ea457b9b65cb136584e58579859d4d', 0, 1);
INSERT INTO `sys_file` VALUES (92, '03.jpg', 'jpg', 444, '/file/76b04f48e4a64758b33ecb2fb2f5dcac.jpg', '75e3957bd425eb50fb6976614fb71aa5', 0, 1);
INSERT INTO `sys_file` VALUES (93, '03.jpg', 'jpg', 459, '/file/1c6b1cb486894bd1a91d37b359f97017.jpg', '7c1ef6b581f147cdbbff672cc7c4bd3b', 0, 1);
INSERT INTO `sys_file` VALUES (94, '03.jpg', 'jpg', 631, '/file/baf9051740e9477f986d9a9c7b990911.jpg', '02a0c9cdcb7bb5e5869ef70f5b4a2f30', 0, 1);
INSERT INTO `sys_file` VALUES (95, '10.jpg', 'jpg', 382, '/file/453d3c84562d4f6c968c3ebff42fcd99.jpg', 'b80d6f23dee70c86a744238b84a2711b', 0, 1);
INSERT INTO `sys_file` VALUES (96, '01.jpg', 'jpg', 574, '/file/dd2cfa5daa7a4ca2b82593d21a31b655.jpg', '90dd30ec3c6f0165c14a3dcb203f4093', 0, 1);
INSERT INTO `sys_file` VALUES (97, '05.jpg', 'jpg', 472, '/file/adf6d0a863ca4cf8bf8ac40595b83c93.jpg', '0ea6f890816f6df043f6b9f3e0baf93b', 0, 1);
INSERT INTO `sys_file` VALUES (98, '02.jpg', 'jpg', 540, '/file/32f5b5f61910408aa97acdbc056e2d10.jpg', 'd9a02baae4f4ffc9beda40a6419413df', 0, 1);
INSERT INTO `sys_file` VALUES (99, '06.jpg', 'jpg', 583, '/file/ee49442a3317454a9da737629d1c8895.jpg', 'b95d7cd6ef41116f9769cf8ff6c24aef', 0, 1);
INSERT INTO `sys_file` VALUES (100, '03.jpg', 'jpg', 631, '/file/baf9051740e9477f986d9a9c7b990911.jpg', '02a0c9cdcb7bb5e5869ef70f5b4a2f30', 0, 1);
INSERT INTO `sys_file` VALUES (101, '01.jpg', 'jpg', 574, '/file/dd2cfa5daa7a4ca2b82593d21a31b655.jpg', '90dd30ec3c6f0165c14a3dcb203f4093', 0, 1);
INSERT INTO `sys_file` VALUES (102, '02.jpg', 'jpg', 540, '/file/32f5b5f61910408aa97acdbc056e2d10.jpg', 'd9a02baae4f4ffc9beda40a6419413df', 0, 1);
INSERT INTO `sys_file` VALUES (103, '01.jpg', 'jpg', 574, '/file/dd2cfa5daa7a4ca2b82593d21a31b655.jpg', '90dd30ec3c6f0165c14a3dcb203f4093', 0, 1);
INSERT INTO `sys_file` VALUES (104, '01.jpg', 'jpg', 574, '/file/dd2cfa5daa7a4ca2b82593d21a31b655.jpg', '90dd30ec3c6f0165c14a3dcb203f4093', 0, 1);
INSERT INTO `sys_file` VALUES (105, '01.jpg', 'jpg', 574, '/file/dd2cfa5daa7a4ca2b82593d21a31b655.jpg', '90dd30ec3c6f0165c14a3dcb203f4093', 0, 1);
INSERT INTO `sys_file` VALUES (106, '01.jpg', 'jpg', 574, '/file/dd2cfa5daa7a4ca2b82593d21a31b655.jpg', '90dd30ec3c6f0165c14a3dcb203f4093', 0, 1);
INSERT INTO `sys_file` VALUES (107, '01.jpg', 'jpg', 146, '/file/a0455b12998a4d32b280e5f5542bbcc6.jpg', '5c072037e4e9662831fe448e28795770', 0, 1);
INSERT INTO `sys_file` VALUES (108, '01.jpg', 'jpg', 146, '/file/a0455b12998a4d32b280e5f5542bbcc6.jpg', '5c072037e4e9662831fe448e28795770', 0, 1);
INSERT INTO `sys_file` VALUES (109, '01.jpg', 'jpg', 146, '/file/a0455b12998a4d32b280e5f5542bbcc6.jpg', '5c072037e4e9662831fe448e28795770', 0, 1);
INSERT INTO `sys_file` VALUES (110, 'dog-8448345_960_720.jpg', 'jpg', 49, '/file/297eb3a2b7f447b4b42df981fbcae325.jpg', 'f299c65c7aac9e74bf18d28a9c6f77e1', 0, 1);
INSERT INTO `sys_file` VALUES (111, '微信图片_20231123150622.jpg', 'jpg', 51, '/file/cab99f8cb5be4b9a9e3df12039f75d12.jpg', '6fafba913480f601da46542c42e5b9ff', 0, 1);
INSERT INTO `sys_file` VALUES (112, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);
INSERT INTO `sys_file` VALUES (113, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);
INSERT INTO `sys_file` VALUES (114, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);
INSERT INTO `sys_file` VALUES (115, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);
INSERT INTO `sys_file` VALUES (116, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);
INSERT INTO `sys_file` VALUES (117, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);
INSERT INTO `sys_file` VALUES (118, 'QQ图片20230102135910.png', 'png', 131, '/file/5af9b388d139496ebba41776b1be2e77.png', 'ee8553ebe5120b01b62aadf1825e1717', 0, 1);
INSERT INTO `sys_file` VALUES (119, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);
INSERT INTO `sys_file` VALUES (120, 'QQ图片20230102135910.png', 'png', 131, '/file/5af9b388d139496ebba41776b1be2e77.png', 'ee8553ebe5120b01b62aadf1825e1717', 0, 1);
INSERT INTO `sys_file` VALUES (121, 'QQ图片20230102135922.png', 'png', 135, '/file/1a05fb5b5428498899e1d85ec661fb0d.png', 'd95df88a49d2b4e9e7ebd0e3ed02b332', 0, 1);
INSERT INTO `sys_file` VALUES (122, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);
INSERT INTO `sys_file` VALUES (123, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);
INSERT INTO `sys_file` VALUES (124, '屏幕截图 2026-04-29 204900.png', 'png', 197, '/file/34f055345e844ee0a4e6ff780e0e7efe.png', '40c3e74bb587b05c6b296ca38dad17a0', 0, 1);
INSERT INTO `sys_file` VALUES (125, '屏幕截图 2026-04-29 205245.png', 'png', 888, '/file/d5c0ee31a52640e7be37490c640631ea.png', 'c1e6d35029c1e549e34bae3db7b024f8', 0, 1);
INSERT INTO `sys_file` VALUES (126, '屏幕截图 2026-04-29 205606.png', 'png', 835, '/file/780447a172e4451ca740903136704a04.png', 'ccdda5e001b872ee4592a9e833c44c44', 0, 1);
INSERT INTO `sys_file` VALUES (127, '屏幕截图 2026-04-29 205854.png', 'png', 397, '/file/bf65c94bc974436cbd5c72d84dabb225.png', '9985dfe292f1f4b783774c855912e5ae', 0, 1);
INSERT INTO `sys_file` VALUES (128, '屏幕截图 2026-04-29 212432.png', 'png', 157, '/file/befa829198134c54ba4b77af54ac678c.png', '59fc66313f0546cb85b827ea5e060b18', 0, 1);
INSERT INTO `sys_file` VALUES (129, '屏幕截图 2026-04-30 120300.png', 'png', 977, '/file/d0679f36e33349b5b107b07fb8a81730.png', '7402d0731c84254ea938def336dbf3db', 0, 1);
INSERT INTO `sys_file` VALUES (130, 'image_649913862145861.png', 'png', 16797, '/file/8ab488d9179b4b8ca1b146193a1e1d18.png', '4f92962e738dab7f34aaa9a9a4465e58', 0, 1);
INSERT INTO `sys_file` VALUES (131, 'image_159893037917886.png', 'png', 11570, '/file/e295b608a85c472c935a322aa0a4dc9e.png', '4375fb9151ec9b8baeb9a0f443bb8630', 0, 1);
INSERT INTO `sys_file` VALUES (132, 'image_114514363224386.png', 'png', 14312, '/file/55ee651f0c3947f7a9c194aff8e88a05.png', '465ae0f17ed0174f64d9394ecb5e0af8', 0, 1);
INSERT INTO `sys_file` VALUES (133, 'image_098962887066506.png', 'png', 12802, '/file/c1c01c1565bd4d67bd2c43d5dfd2802d.png', '62b9e84ad4d7e6698644b6741cb946fa', 0, 1);
INSERT INTO `sys_file` VALUES (134, 'image_098962887066506.png', 'png', 12802, '/file/c1c01c1565bd4d67bd2c43d5dfd2802d.png', '62b9e84ad4d7e6698644b6741cb946fa', 0, 1);
INSERT INTO `sys_file` VALUES (135, 'image_459824068664597.png', 'png', 17311, '/file/262806c7620742e781e16f674f65e330.png', 'a174a6bf84f959fda4082d8dd7a28075', 0, 1);
INSERT INTO `sys_file` VALUES (136, 'image_024855354451684.png', 'png', 15771, '/file/936aa6abb1fe42ecbe033e28bf8726de.png', 'd48cea57096844cebb700f636f17f114', 0, 1);
INSERT INTO `sys_file` VALUES (137, 'image_834230762500188.png', 'png', 16868, '/file/92547ea62e304cf69a545447f5de0ee2.png', '8c5011d7a68a795fb2d6d6bbc40b9d70', 0, 1);
INSERT INTO `sys_file` VALUES (138, 'image_522448665878469.png', 'png', 2658, '/file/fe7d9a8119c04d76b15b36f3a0d33c9b.png', '82c274a187b339c618e454cbdbd124e8', 0, 1);
INSERT INTO `sys_file` VALUES (139, 'image_330515835962801.png', 'png', 2391, '/file/79aabf1b0fb545e0b2fb3e947df75105.png', 'd68e4b48ee6bd877c1795a9d5795b1d9', 0, 1);
INSERT INTO `sys_file` VALUES (140, 'image_698201642564934.png', 'png', 2250, '/file/872dba857c6640f38a22985caf81ac93.png', '2d3f4a78adeb76c4c87634d02db6e61f', 0, 1);
INSERT INTO `sys_file` VALUES (141, 'image_562318044665816.png', 'png', 2588, '/file/a3172fb3804d4723824979c9985f59b1.png', '8b9ad42948c241c415ed03c01b9fb6c3', 0, 1);
INSERT INTO `sys_file` VALUES (142, 'image_963352340267501.png', 'png', 2560, '/file/6be2bd6b49064935a814b589bbbdfd58.png', '7f1fd8c016b6b05a744c8b220bd2f525', 0, 1);
INSERT INTO `sys_file` VALUES (143, 'image_126517187881279.png', 'png', 2150, '/file/30caaf0b3bbe4921ba722c97355cfb13.png', 'f6669bcabdc5b7d9b9a6ea57961a6e60', 0, 1);
INSERT INTO `sys_file` VALUES (144, 'image_105523448801750.png', 'png', 12381, '/file/a0dbb724d34c4a6bab8115f26b70c81b.png', 'b2ecf4f42b93da7a04ff507438e64cd8', 0, 1);
INSERT INTO `sys_file` VALUES (145, 'image_105523448801750.png', 'png', 12381, '/file/a0dbb724d34c4a6bab8115f26b70c81b.png', 'b2ecf4f42b93da7a04ff507438e64cd8', 0, 1);
INSERT INTO `sys_file` VALUES (146, 'image_105523448801750.png', 'png', 12381, '/file/a0dbb724d34c4a6bab8115f26b70c81b.png', 'b2ecf4f42b93da7a04ff507438e64cd8', 0, 1);
INSERT INTO `sys_file` VALUES (147, 'image_197584580838696.png', 'png', 9127, '/file/5fd8fe72106f4a50b0788232aab07c15.png', '194e14dffd35ef9531a42022fc59f183', 0, 1);
INSERT INTO `sys_file` VALUES (148, 'image_197584580838696.png', 'png', 9127, '/file/5fd8fe72106f4a50b0788232aab07c15.png', '194e14dffd35ef9531a42022fc59f183', 0, 1);
INSERT INTO `sys_file` VALUES (149, 'image_197584580838696.png', 'png', 9127, '/file/5fd8fe72106f4a50b0788232aab07c15.png', '194e14dffd35ef9531a42022fc59f183', 0, 1);
INSERT INTO `sys_file` VALUES (150, 'image_214693102278261.png', 'png', 8223, '/file/1998ef4452dd4f6a94f7fa5d10fb7250.png', 'd0447deb1a3a5bbddf063b850fa29d04', 0, 1);
INSERT INTO `sys_file` VALUES (151, 'image_375325125547413.png', 'png', 1893, '/file/821df7b79c8946e8958ab151cb09fa1d.png', 'b23f703027e6012e0c74814bf531500d', 0, 1);
INSERT INTO `sys_file` VALUES (152, 'image_572547462607943.png', 'png', 1380, '/file/cfff9849329b4a61aaf40dd811195b3d.png', '6a6aa8900ec9e25a8821b07de831cdf7', 0, 1);
INSERT INTO `sys_file` VALUES (153, 'image_375325125547413.png', 'png', 1893, '/file/821df7b79c8946e8958ab151cb09fa1d.png', 'b23f703027e6012e0c74814bf531500d', 0, 1);
INSERT INTO `sys_file` VALUES (154, 'image_375325125547413.png', 'png', 1893, '/file/821df7b79c8946e8958ab151cb09fa1d.png', 'b23f703027e6012e0c74814bf531500d', 0, 1);
INSERT INTO `sys_file` VALUES (155, 'image_570112921484508.png', 'png', 12487, '/file/001ee2e7a01542e1b9583ed679db7a13.png', 'd909ff21c298f96b2c440ab8bd65675f', 0, 1);
INSERT INTO `sys_file` VALUES (156, 'image_761884022428111.png', 'png', 13134, '/file/8b25134ae1cf4770b74df872006c3e20.png', '607dec4f66811a62ea4fbce9d33eaaa1', 0, 1);
INSERT INTO `sys_file` VALUES (157, 'image_761884022428111.png', 'png', 13134, '/file/8b25134ae1cf4770b74df872006c3e20.png', '607dec4f66811a62ea4fbce9d33eaaa1', 0, 1);
INSERT INTO `sys_file` VALUES (158, 'image_393231613507284.png', 'png', 13017, '/file/f8f7b7907c7c4617a93f9170964a602f.png', '5dc5ccab835be0339050299292bfd94f', 0, 1);
INSERT INTO `sys_file` VALUES (159, 'image_393231613507284.png', 'png', 13017, '/file/f8f7b7907c7c4617a93f9170964a602f.png', '5dc5ccab835be0339050299292bfd94f', 0, 1);
INSERT INTO `sys_file` VALUES (160, 'JUnit 5 @MethodSource 使用.png', 'png', 5765, '/file/fcf2eb6127a141e7bb3e43288980b9da.png', '0d23089597a3a5a14762c4824be4232d', 0, 1);
INSERT INTO `sys_file` VALUES (161, 'JUnit 5 @MethodSource 使用.png', 'png', 5765, '/file/fcf2eb6127a141e7bb3e43288980b9da.png', '0d23089597a3a5a14762c4824be4232d', 0, 1);
INSERT INTO `sys_file` VALUES (162, 'JUnit 5 @MethodSource 使用 (1).png', 'png', 5063, '/file/6a099abc4e8b4cd8a1b1aaf45141d7d1.png', 'd7aababe329e5ddd81f59abf030fd795', 0, 1);
INSERT INTO `sys_file` VALUES (163, 'JUnit 5 @MethodSource 使用 (2).png', 'png', 4927, '/file/a197a07ed33b41d4ba519a0daa85b620.png', '804a7b11dc7e12758a190c1c469d3416', 0, 1);
INSERT INTO `sys_file` VALUES (164, 'JUnit 5 @MethodSource 使用 (4).png', 'png', 4846, '/file/03126de2ee824e53904367970304da8b.png', '6e2579ed62386d58bbd1d7c63babab5d', 0, 1);
INSERT INTO `sys_file` VALUES (165, 'JUnit 5 @MethodSource 使用 (5).png', 'png', 5564, '/file/386844f7a9634c179ccda3305885e1fc.png', '3fb3af53d5cece50c0cdaf34a801eef4', 0, 1);
INSERT INTO `sys_file` VALUES (166, 'JUnit 5 @MethodSource 使用 (6).png', 'png', 4778, '/file/0f5917da669a4745a8c234984f8d0350.png', '3e6b4203b0820e8beb4d947370643f7f', 0, 1);
INSERT INTO `sys_file` VALUES (167, 'JUnit 5 @MethodSource 使用 (7).png', 'png', 4700, '/file/4925c64ec4af4a30875176ab0c67fe97.png', '6dc9f3c508fb12c2f4bb9fefca47952f', 0, 1);
INSERT INTO `sys_file` VALUES (168, 'image_551258666208205.png', 'png', 13559, '/file/f4fe265f77744fca80750a8992942c37.png', '7b869f26783959531324d9e9f804be7d', 0, 1);
INSERT INTO `sys_file` VALUES (169, 'image_714995506303621.png', 'png', 12676, '/file/7b5dcdffdb414cddaf2d45c8241beef8.png', '83409a3a12589c05bf5d07cc631ff128', 0, 1);
INSERT INTO `sys_file` VALUES (170, 'image_467895999337002.png', 'png', 14326, '/file/22a3694761474590be5705fa6f614ef7.png', '35efeceab6f376d58b4430e504b7ae74', 0, 1);
INSERT INTO `sys_file` VALUES (171, 'image_352883429492511.png', 'png', 2754, '/file/a38e04f190454a1397e481c93f0084c9.png', 'c59c00159a45337df262749199da9cce', 0, 1);
INSERT INTO `sys_file` VALUES (172, 'image_317700770140356.png', 'png', 2186, '/file/07487468e70346d3abefee5f6188cca1.png', '306c403bd58360f4419ee386f2fd2436', 0, 1);
INSERT INTO `sys_file` VALUES (173, 'image_334369824600764.png', 'png', 1847, '/file/6768b3bf4bdd468ab97e113b75ecba0e.png', '4bb679cbf5a5e46825eb5e70eac3eeb3', 0, 1);
INSERT INTO `sys_file` VALUES (174, 'image_499289137529087.png', 'png', 4575, '/file/0386be8a1a8e4e45a509eeea167cdb35.png', '054f191a381f820d2d93375fa7923a9c', 0, 1);
INSERT INTO `sys_file` VALUES (175, 'image_499289137529087.png', 'png', 4575, '/file/0386be8a1a8e4e45a509eeea167cdb35.png', '054f191a381f820d2d93375fa7923a9c', 0, 1);
INSERT INTO `sys_file` VALUES (176, 'image_750408733324885.png', 'png', 5081, '/file/e4ddaa6fbbbe4a8a8b9384678c3f7b1e.png', '75852681f795ac512abc7c6e2bfdf1a8', 0, 1);
INSERT INTO `sys_file` VALUES (177, 'image_486964502035963.png', 'png', 13466, '/file/3fc9adaea3d44949ae51b87106a70c3a.png', 'a89072d901763cb6796a91d8ff3847c7', 0, 1);
INSERT INTO `sys_file` VALUES (178, 'image_902371647477972.png', 'png', 2156, '/file/69624a81ac594329bbbe92bcf4840b2a.png', 'cbe1411e1cb21850df8a1d9c112202c5', 0, 1);
INSERT INTO `sys_file` VALUES (179, 'image_732234976620262.png', 'png', 1438, '/file/6a637ac16f624363bbe1e2b726163ce8.png', '65230e80934d70002f6cdf48203ee0e9', 0, 1);
INSERT INTO `sys_file` VALUES (180, 'image_918667826683891.png', 'png', 2179, '/file/2d5dba928bf241fa99e5f2a18b96eb64.png', '95b5173b533bf0abf7ae0284e0ffbfb5', 0, 1);
INSERT INTO `sys_file` VALUES (181, 'image_168812001354393.png', 'png', 1544, '/file/4df7e115b3fb4aaa842aaf4fc8c9e35c.png', 'f24d3f060e1ca2ec126f92079f87f6cc', 0, 1);
INSERT INTO `sys_file` VALUES (182, 'image_765761087410674.png', 'png', 2048, '/file/81b1b521c1274ffc94453fd6a0fe1103.png', '87078346c3ef9c4cae48a0fdd4474cbc', 0, 1);
INSERT INTO `sys_file` VALUES (183, 'image_775504519869778.png', 'png', 2403, '/file/7cf9bedc69354a8dae60bdab53f919da.png', '13da88828f74b2ab872234c136d86dbc', 0, 1);
INSERT INTO `sys_file` VALUES (184, 'image_908043860362888.png', 'png', 2531, '/file/34adef4accb244a1a838f2910eedde1b.png', '50fce57d91a8704083b3461ca931aff9', 0, 1);
INSERT INTO `sys_file` VALUES (185, 'image_740884867970118.png', 'png', 2531, '/file/34adef4accb244a1a838f2910eedde1b.png', '50fce57d91a8704083b3461ca931aff9', 0, 1);
INSERT INTO `sys_file` VALUES (186, 'image_134145331691584.png', 'png', 2514, '/file/54b9b97e82154ea8a8ab0b10e1af40df.png', '0d4c82e072b3032f70e8c15f01764218', 0, 1);
INSERT INTO `sys_file` VALUES (187, 'image_110777031681130.png', 'png', 2402, '/file/a496994355a340ed8d0d50747a064d43.png', 'e6747f5aee50e495a80a07bab2fc32f6', 0, 1);
INSERT INTO `sys_file` VALUES (188, 'image_488550229570923.png', 'png', 2450, '/file/85c95623720145c4898cce1ec3a62a9d.png', '428731ae2e7442d7951fe19c7cf066ce', 0, 1);
INSERT INTO `sys_file` VALUES (189, 'image_667542479860579.png', 'png', 2379, '/file/e893810859b84931a4d560cdd9d187ec.png', 'ad17caf46362cf38e6d71fd8c8222776', 0, 1);
INSERT INTO `sys_file` VALUES (190, 'image_872779894317086.png', 'png', 2510, '/file/a01da851d0224d24921ff87519b0d9c5.png', 'cde9fefef8be83fb38947c428972121d', 0, 1);
INSERT INTO `sys_file` VALUES (191, 'image_292064295320672.png', 'png', 2262, '/file/69b2a2964ace45c1a79268379bad1d01.png', '561073a9bca8dd823dbeaf32e89308b8', 0, 1);
INSERT INTO `sys_file` VALUES (192, 'image_872779894317086.png', 'png', 2510, '/file/a01da851d0224d24921ff87519b0d9c5.png', 'cde9fefef8be83fb38947c428972121d', 0, 1);
INSERT INTO `sys_file` VALUES (193, 'image_096676859747549.png', 'png', 2363, '/file/89b1cdeba6464d13b857ebe339053899.png', '6e7525ae64f43d5e1eb97af3df4f0f32', 0, 1);
INSERT INTO `sys_file` VALUES (194, 'image_819279527810663.png', 'png', 2282, '/file/7e2752505ec0499fbb6188e9a899283b.png', '65e0184f77aca4da34a78bc09d9bb6f7', 0, 1);
INSERT INTO `sys_file` VALUES (195, 'image_867206057518954.png', 'png', 2385, '/file/e64134bf4fea4038ae2a868dda6c95bd.png', 'cef58102d83c892589a8066248ba1bed', 0, 1);
INSERT INTO `sys_file` VALUES (196, 'image_547599837449875.png', 'png', 2249, '/file/0f79dcbced974f68a6272e9345a36d85.png', '4b3d4f8a9c73320177baabdbb35ac1e6', 0, 1);
INSERT INTO `sys_file` VALUES (197, 'image_320029619395757.png', 'png', 2504, '/file/90afd5752f4d427183c1dde078dc1c12.png', '364ae07d724c302a0db26f0d7865af17', 0, 1);
INSERT INTO `sys_file` VALUES (198, 'image_832483347077928.png', 'png', 2558, '/file/f8a48c78012c4557a13f696cb45559c3.png', '8f8a0b04ffb79b3112fa86056ae78f96', 0, 1);
INSERT INTO `sys_file` VALUES (199, 'image_987489150084917.png', 'png', 2354, '/file/3d0923aa7f114b19bb842c3db1c04e04.png', '39874e9a181bb321312212769152d222', 0, 1);
INSERT INTO `sys_file` VALUES (200, 'image_178286687030502.png', 'png', 2275, '/file/d27cf4fd2c2c49b884c098eeff74beae.png', '359556b6c1e55b8e9281097b63f50c7b', 0, 1);
INSERT INTO `sys_file` VALUES (201, 'image_671605590243184.png', 'png', 2361, '/file/6c2f56e09fab4cceae21eefb36686a54.png', 'fa205ba55b38ae6ea2f77f2cf387dca8', 0, 1);
INSERT INTO `sys_file` VALUES (202, 'image_861053552390484.png', 'png', 2496, '/file/b4dc164ec0694eccb7bb7b7f82affc7f.png', 'ece6c03c4f46279f835130476f9a72a4', 0, 1);
INSERT INTO `sys_file` VALUES (203, '2023-01-02 (2).png', 'png', 365, '/file/fb19b0f3801f4fd5b32a922bf8398142.png', 'b1f4a923264788633839ec37b12d90a6', 0, 1);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路径',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `pid` int(11) NULL DEFAULT NULL COMMENT '父级id',
  `page_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '页面路径',
  `sort_num` int(11) NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 53 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (4, '系统管理', NULL, 'iconfont icon-r-setting', NULL, NULL, NULL, 300);
INSERT INTO `sys_menu` VALUES (5, '用户管理', '/user', 'iconfont icon-r-user2', NULL, 4, 'User', 301);
INSERT INTO `sys_menu` VALUES (6, '角色管理', '/role', 'iconfont icon-r-user3', NULL, 4, 'Role', 302);
INSERT INTO `sys_menu` VALUES (7, '菜单管理', '/menu', 'iconfont icon-r-setting', NULL, 4, 'Menu', 303);
INSERT INTO `sys_menu` VALUES (8, '文件管理', '/file', 'iconfont icon-r-paper', NULL, 4, 'File', 304);
INSERT INTO `sys_menu` VALUES (10, '主页', '/home', 'iconfont icon-r-home', NULL, NULL, 'Home', 0);
INSERT INTO `sys_menu` VALUES (11, '流浪动物管理', '/animal', 'iconfont icon-r-love', NULL, 25, 'Animal', 401);
INSERT INTO `sys_menu` VALUES (13, '申请领养管理', '/applcation', 'iconfont icon-r-add', NULL, 25, 'Applcation', 407);
INSERT INTO `sys_menu` VALUES (14, '评论管理', '/comment', 'iconfont icon-r-team', NULL, 26, 'Comment', 501);
INSERT INTO `sys_menu` VALUES (16, '喂养点管理', '/feed', 'iconfont icon-r-building', NULL, 25, 'Feed', 405);
INSERT INTO `sys_menu` VALUES (19, '救助站管理', '/rescue', 'iconfont icon-r-building', NULL, 25, 'Rescue', 406);
INSERT INTO `sys_menu` VALUES (20, '帖子管理', '/article', 'iconfont icon-r-edit', NULL, 26, 'Article', 502);
INSERT INTO `sys_menu` VALUES (23, '科普文章管理', '/articleKp', 'iconfont icon-r-edit', NULL, 26, 'ArticleKp', 504);
INSERT INTO `sys_menu` VALUES (24, '活动管理', '/activity', 'iconfont icon-r-mark1', NULL, 26, 'Activity', 505);
INSERT INTO `sys_menu` VALUES (25, '宠物管理', NULL, 'iconfont icon-r-love', NULL, NULL, NULL, 400);
INSERT INTO `sys_menu` VALUES (26, '其他管理', NULL, 'iconfont icon-r-list', NULL, NULL, NULL, 500);
INSERT INTO `sys_menu` VALUES (27, '健康记录', '/healthRecord', 'iconfont icon-r-love', NULL, NULL, 'front/HealthRecord', NULL);
INSERT INTO `sys_menu` VALUES (28, '站内信', '/userNotice', 'iconfont icon-r-paper', NULL, NULL, 'front/Notice', NULL);
INSERT INTO `sys_menu` VALUES (29, '预约审核管理', '/serviceOrderAdmin', 'el-icon-s-order', '预约审核管理', 25, 'ServiceOrderAdmin', 402);
INSERT INTO `sys_menu` VALUES (51, '服务预约管理', '/serviceItemAdmin', 'el-icon-s-order', '服务预约管理', 25, 'ServiceItemAdmin', 404);
INSERT INTO `sys_menu` VALUES (52, '算法配置管理', '/algorithmConfig', 'el-icon-s-tools', '算法参数配置管理', 4, 'AlgorithmConfig', 305);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `flag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '唯一标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', '管理员', 'ROLE_ADMIN');
INSERT INTO `sys_role` VALUES (2, '普通用户', '普通用户', 'ROLE_USER');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` int(11) NOT NULL COMMENT '角色id',
  `menu_id` int(11) NOT NULL COMMENT '菜单id',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (1, 4);
INSERT INTO `sys_role_menu` VALUES (1, 5);
INSERT INTO `sys_role_menu` VALUES (1, 6);
INSERT INTO `sys_role_menu` VALUES (1, 7);
INSERT INTO `sys_role_menu` VALUES (1, 8);
INSERT INTO `sys_role_menu` VALUES (1, 10);
INSERT INTO `sys_role_menu` VALUES (1, 11);
INSERT INTO `sys_role_menu` VALUES (1, 13);
INSERT INTO `sys_role_menu` VALUES (1, 14);
INSERT INTO `sys_role_menu` VALUES (1, 16);
INSERT INTO `sys_role_menu` VALUES (1, 19);
INSERT INTO `sys_role_menu` VALUES (1, 20);
INSERT INTO `sys_role_menu` VALUES (1, 23);
INSERT INTO `sys_role_menu` VALUES (1, 24);
INSERT INTO `sys_role_menu` VALUES (1, 25);
INSERT INTO `sys_role_menu` VALUES (1, 26);
INSERT INTO `sys_role_menu` VALUES (1, 29);
INSERT INTO `sys_role_menu` VALUES (1, 51);
INSERT INTO `sys_role_menu` VALUES (1, 52);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 8);
INSERT INTO `sys_role_menu` VALUES (2, 10);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地址',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `birth` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出生年月',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', '管理员', '123@qq.com', '19911111111', '广州市', '2023-06-22 21:10:27', '/file/34f055345e844ee0a4e6ff780e0e7efe.png', 'ROLE_ADMIN', '男', '1999-07-01');
INSERT INTO `sys_user` VALUES (7, 'user1', '123456', '爱猫人士小王', NULL, '13800001111', NULL, '2025-03-22 23:34:11', '/file/d5c0ee31a52640e7be37490c640631ea.png', 'ROLE_USER', NULL, NULL);
INSERT INTO `sys_user` VALUES (8, 'user2', '123456', '美容控MM', NULL, '13800002222', NULL, '2025-05-11 23:34:11', '/file/780447a172e4451ca740903136704a04.png', 'ROLE_USER', NULL, NULL);
INSERT INTO `sys_user` VALUES (9, 'user3', '123456', '健康守护者', NULL, '13800003333', NULL, '2025-06-30 23:34:11', '/file/bf65c94bc974436cbd5c72d84dabb225.png', 'ROLE_USER', NULL, NULL);
INSERT INTO `sys_user` VALUES (10, 'user4', '123456', '出差达人', NULL, '13800004444', NULL, '2025-08-19 23:34:11', '/file/befa829198134c54ba4b77af54ac678c.png', 'ROLE_USER', NULL, NULL);
INSERT INTO `sys_user` VALUES (11, 'user5', '123456', '铲屎官小李', NULL, '13800005555', NULL, '2025-10-08 23:34:11', '/file/d0679f36e33349b5b107b07fb8a81730.png', 'ROLE_USER', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
