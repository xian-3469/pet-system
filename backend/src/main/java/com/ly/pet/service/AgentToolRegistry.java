package com.ly.pet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ly.pet.common.Constants;
import com.ly.pet.controller.dto.ServiceOrderDTO;
import com.ly.pet.dto.GlmTool;
import com.ly.pet.entity.Activity;
import com.ly.pet.entity.Animal;
import com.ly.pet.entity.Applcation;
import com.ly.pet.entity.ArticleKp;
import com.ly.pet.entity.Feed;
import com.ly.pet.entity.HealthAdvice;
import com.ly.pet.entity.HealthRecord;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.entity.Rescue;
import com.ly.pet.entity.ServiceItem;
import com.ly.pet.entity.ServiceOrder;
import com.ly.pet.entity.User;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.service.IActivityService;
import com.ly.pet.service.IAnimalService;
import com.ly.pet.service.IApplcationService;
import com.ly.pet.service.IArticleKpService;
import com.ly.pet.service.IFeedService;
import com.ly.pet.service.IHealthAdviceService;
import com.ly.pet.service.IHealthRecordService;
import com.ly.pet.service.IPetProfileService;
import com.ly.pet.service.IRescueService;
import com.ly.pet.service.IServiceItemService;
import com.ly.pet.service.IServiceOrderService;
import com.ly.pet.service.IServiceRecommendationService;
import com.ly.pet.service.IUserService;
import com.ly.pet.service.impl.AdoptMatchService;
import com.ly.pet.utils.TokenUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Agent 工具注册中心
 * 将平台各业务模块的查询、写入、统计能力包装为 LLM 可调用的工具（function calling）
 * 查询工具按当前登录用户过滤数据权限；写工具走两阶段确认；统计工具仅管理员可用
 */
@Service
public class AgentToolRegistry {

    /** 健康记录类型合法值 */
    private static final Set<String> RECORD_TYPES = new java.util.HashSet<>(Arrays.asList("疫苗", "驱虫", "体检", "治疗"));

    @Resource
    private IAnimalService animalService;
    @Resource
    private IApplcationService applcationService;
    @Resource
    private IPetProfileService petProfileService;
    @Resource
    private IHealthRecordService healthRecordService;
    @Resource
    private IHealthAdviceService healthAdviceService;
    @Resource
    private IServiceRecommendationService recommendationService;
    @Resource
    private IRescueService rescueService;
    @Resource
    private IFeedService feedService;
    @Resource
    private IActivityService activityService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IServiceItemService serviceItemService;

    @Resource
    private IServiceOrderService serviceOrderService;

    @Resource
    private IUserService userService;

    @Resource
    private IArticleKpService articleKpService;

    @Resource
    private AdoptMatchService adoptMatchService;

    @Resource
    private HealthAdviceAiService healthAdviceAiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 单个工具执行结果：payload 回传给 LLM，brief 用于前端展示工具调用轨迹
     */
    public static class ToolExecutionResult {
        private final String payload;
        private final String brief;

        public ToolExecutionResult(String payload, String brief) {
            this.payload = payload;
            this.brief = brief;
        }

        public String getPayload() {
            return payload;
        }

        public String getBrief() {
            return brief;
        }
    }

    // ==================== 工具定义 ====================

    public List<GlmTool> listTools() {
        List<GlmTool> tools = new ArrayList<>();
        tools.add(GlmTool.of("search_adoptable_animals", "搜索当前可领养的流浪动物。可按品种类型（猫/狗等）、体型（小型/中型/大型）、名字或品种关键词筛选，返回动物概要信息。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("keyword", strProp("名字或品种关键词，如：金毛、橘猫"));
                    put("type", strProp("品种类型精确匹配，如：猫、狗、兔子"));
                    put("bodyType", strProp("体型：小型/中型/大型"));
                    put("limit", intProp("返回数量上限，默认8，最大20"));
                }}, null)));
        tools.add(GlmTool.of("get_animal_detail", "根据动物ID获取一只流浪动物的完整详情（性格、健康状况、领养状态等）。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("animalId", intProp("动物ID"));
                }}, Arrays.asList("animalId"))));
        tools.add(GlmTool.of("get_my_applications", "查询当前用户的领养申请记录及审核状态（待审核/审核通过/审核不通过），附带所申请动物的信息。",
                objectSchema(new HashMap<>(), null)));
        tools.add(GlmTool.of("get_my_pets", "查询当前用户的宠物档案列表（名称、品种、年龄、体重、绝育状态、标签等）。",
                objectSchema(new HashMap<>(), null)));
        tools.add(GlmTool.of("get_pet_health_records", "查询指定宠物的健康记录（疫苗、驱虫、体检、治疗），包括下次提醒日期。需要宠物档案ID；若不知道ID，请先调用 get_my_pets 查询。仅能查询本人宠物。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("petId", intProp("宠物档案ID"));
                    put("petName", strProp("宠物名字，不知道ID时提供名字，系统会自动匹配"));
                }}, null)));
        tools.add(GlmTool.of("get_pending_health_advice", "查询当前用户宠物未处理的健康建议/提醒（如疫苗到期、复诊提醒），可按宠物过滤。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("petId", intProp("宠物档案ID，不传则查询所有宠物"));
                }}, null)));
        tools.add(GlmTool.of("recommend_services", "推荐平台服务项目（洗护/美容/寄养/医疗），支持个性化推荐与热门推荐。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("recommendType", strProp("personalized个性化推荐（默认）/ hot热门推荐"));
                    put("limit", intProp("返回数量上限，默认5，最大10"));
                }}, null)));
        tools.add(GlmTool.of("search_rescue_stations", "按名称或区域关键词搜索流浪动物救助站，返回地址、联系人、电话。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("keyword", strProp("救助站名称或区域关键词"));
                }}, null)));
        tools.add(GlmTool.of("search_feed_points", "按区域关键词搜索流浪动物喂食点。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("keyword", strProp("区域或地址关键词"));
                }}, null)));
        tools.add(GlmTool.of("get_upcoming_activities", "查询平台发布的公益救助活动列表（名称、时间、地点、报名人数）。",
                objectSchema(new HashMap<>(), null)));
        tools.add(GlmTool.of("submit_adopt_application", "代表当前用户提交领养申请（写操作）。必须两步执行：第一步不传 confirm 调用做资格预检，把返回的申请摘要展示给用户并明确询问是否确认提交；只有当用户在对话中明确回复同意后，才允许以 confirm=true 再次调用正式提交。未经用户确认严禁提交。动物必须处于可领养状态；若用户未说明领养理由，先询问。不知道动物ID时可直接传 animalName 按名字解析。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("animalId", intProp("要领养的动物ID（对话中有明确 ID 时使用）"));
                    put("animalName", strProp("动物名字，如：小橘。不知道ID时用名字，系统自动匹配"));
                    put("reason", strProp("必填。领养理由：从对话历史中提取用户表述的原话（即使是很早之前说的也要回溯查找），不要自己编造"));
                    put("experience", strProp("养宠经验（用户提到才填）"));
                    put("housing", strProp("住房条件（用户提到才填）"));
                    put("confirm", new HashMap<String, Object>() {{
                        put("type", "boolean");
                        put("description", "false/缺省=仅预检并等待用户确认；true=正式提交申请");
                    }});
                }}, null)));
        tools.add(GlmTool.of("add_health_record", "为当前用户的宠物新增一条健康记录（写操作）。必须两步执行：第一步不传 confirm 调用做预检，把记录摘要展示给用户并明确询问是否确认录入；用户同意后才以 confirm=true 正式写入。recordType 必须是 疫苗/驱虫/体检/治疗 之一；recordDate 用 yyyy-MM-dd 格式（当前日期已在系统提示中给出，相对时间如“昨天”需自行换算）；不完整的字段先向用户询问。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("petName", strProp("宠物名字，如：花花。不知道ID时用名字，系统自动匹配"));
                    put("petId", intProp("宠物档案ID（对话中有明确 ID 时使用）"));
                    put("recordType", strProp("记录类型：疫苗/驱虫/体检/治疗"));
                    put("itemName", strProp("必填。项目名称：从对话历史中提取用户表述（如用户说'打了猫三联第三针'则传'猫三联疫苗第三针'）"));
                    put("recordDate", strProp("记录日期，yyyy-MM-dd 格式"));
                    put("hospital", strProp("医院/机构（用户提到才填）"));
                    put("doctor", strProp("医生（用户提到才填）"));
                    put("nextDate", strProp("下次提醒日期，yyyy-MM-dd（用户提到才填）"));
                    put("weight", new HashMap<String, Object>() {{
                        put("type", "number");
                        put("description", "当时体重 kg（用户提到才填）");
                    }});
                    put("remark", strProp("备注（用户提到才填）"));
                    put("confirm", new HashMap<String, Object>() {{
                        put("type", "boolean");
                        put("description", "false/缺省=仅预检等待确认；true=正式写入");
                    }});
                }}, null)));
        tools.add(GlmTool.of("create_service_order", "为当前用户的宠物创建服务预约订单（写操作）。必须两步执行：先不传 confirm 预检生成订单预览（含服务、门店、价格、时间）请用户确认；用户同意后才以 confirm=true 正式下单。appointmentTime 用 yyyy-MM-dd HH:mm 格式（相对时间需按系统提示中的当前时间换算）；同服务同一时段若已被预约会提交失败。不知道服务或宠物 ID 时直接传名字。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("serviceName", strProp("服务项目名称，如：基础洗护套餐。不知道ID时用名字"));
                    put("serviceId", intProp("服务项目ID（对话中有明确 ID 时使用）"));
                    put("petName", strProp("宠物名字，如：球球"));
                    put("petId", intProp("宠物档案ID（对话中有明确 ID 时使用）"));
                    put("appointmentTime", strProp("预约时间，yyyy-MM-dd HH:mm 格式，不能早于当前时间"));
                    put("remark", strProp("备注（可选）"));
                    put("confirm", new HashMap<String, Object>() {{
                        put("type", "boolean");
                        put("description", "false/缺省=仅预检等待确认；true=正式下单");
                    }});
                }}, null)));
        tools.add(GlmTool.of("get_adoption_statistics", "【仅管理员】查询领养业务统计数据：领养申请总数/待审核/通过/不通过/通过率，以及流浪动物总数、可领养数、已领养数、体型分布。",
                objectSchema(new HashMap<>(), null)));
        tools.add(GlmTool.of("get_service_statistics", "【仅管理员】查询服务业务统计数据：订单总数与各状态分布、本月订单量、已完成订单总收入、订单量最高的前5个服务。",
                objectSchema(new HashMap<>(), null)));
        tools.add(GlmTool.of("get_user_statistics", "【仅管理员】查询用户统计数据：注册用户总数、本月新增、有宠物档案的用户数。",
                objectSchema(new HashMap<>(), null)));
        tools.add(GlmTool.of("get_my_service_orders", "查询当前用户的服务预约订单列表（订单号、服务名、预约时间、状态、门店），可按状态过滤（PENDING待确认/CONFIRMED已确认/COMPLETED已完成/CANCELLED已取消）。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("status", strProp("可选，按状态过滤，如 PENDING"));
                }}, null)));
        tools.add(GlmTool.of("cancel_service_order", "取消当前用户自己的服务预约订单（写操作）。必须两步执行：先不传 confirm 预检展示订单信息并询问取消原因与确认；用户同意后才以 confirm=true 正式取消。仅待确认/已确认状态的订单可取消。不知道订单号时先调用 get_my_service_orders 查询。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("orderNo", strProp("订单号（与 orderId 二选一）"));
                    put("orderId", intProp("订单ID（与 orderNo 二选一）"));
                    put("cancelReason", strProp("必填。取消原因：从对话历史中提取用户表述的原话（即使是很早之前说的也要回溯查找），不要自己编造"));
                    put("confirm", new HashMap<String, Object>() {{
                        put("type", "boolean");
                        put("description", "false/缺省=仅预检等待确认；true=正式取消");
                    }});
                }}, null)));
        tools.add(GlmTool.of("recommend_pets_for_me", "根据用户最新领养申请的画像（经验/住房/收入/家庭结构），用平台领养匹配算法对所有可领养动物打分，返回匹配度最高的前3只。用户问'推荐适合我的宠物'时使用。",
                objectSchema(new HashMap<>(), null)));
        tools.add(GlmTool.of("generate_pet_health_advice", "为当前用户的一只宠物生成 AI 个性化健康建议（写操作，会覆盖该宠物现有建议）。必须两步执行：先不传 confirm 说明将要进行的操作并征得用户同意；同意后以 confirm=true 执行。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("petName", strProp("宠物名字"));
                    put("petId", intProp("宠物档案ID（二选一）"));
                    put("confirm", new HashMap<String, Object>() {{
                        put("type", "boolean");
                        put("description", "false/缺省=仅预检等待确认；true=正式生成");
                    }});
                }}, null)));
        tools.add(GlmTool.of("publish_knowledge_article", "【仅管理员】撰写并发布一篇宠物知识科普文章（写操作）。根据用户给定的主题撰写 markdown 格式正文（600-1000字，实用、结构清晰）。服务端强制两阶段：第一步不传 confirm 调用会返回标题/字数/开头预览，必须把预览展示给管理员并等待其明确同意；第二步仅传 confirm=true 正式发布（服务器使用预检时保存的定稿，无需重复传正文）。未经过预检的发布请求会被拒绝。",
                objectSchema(new LinkedHashMap<String, Object>() {{
                    put("title", strProp("文章标题"));
                    put("content", strProp("markdown 格式的文章正文"));
                    put("confirm", new HashMap<String, Object>() {{
                        put("type", "boolean");
                        put("description", "false/缺省=仅预检（生成待确认草稿）；true=正式发布用户已同意的草稿");
                    }});
                }}, Arrays.asList("title"))));
        return tools;
    }

    /**
     * 执行工具调用
     *
     * @param name          工具名
     * @param argumentsJson JSON 字符串形式的参数
     */
    public ToolExecutionResult execute(String name, String argumentsJson) {
        User user = TokenUtils.getCurrentUser();
        if (user == null) {
            throw new ServiceException(Constants.CODE_401, "请先登录后再使用 AI 助手");
        }
        JsonNode args = parseArgs(argumentsJson);
        try {
            switch (name) {
                case "search_adoptable_animals":
                    return searchAdoptableAnimals(args);
                case "get_animal_detail":
                    return getAnimalDetail(args);
                case "get_my_applications":
                    return getMyApplications(user);
                case "get_my_pets":
                    return getMyPets(user);
                case "get_pet_health_records":
                    return getPetHealthRecords(args, user);
                case "get_pending_health_advice":
                    return getPendingHealthAdvice(args, user);
                case "recommend_services":
                    return recommendServices(args, user);
                case "search_rescue_stations":
                    return searchRescueStations(args);
                case "search_feed_points":
                    return searchFeedPoints(args);
                case "get_upcoming_activities":
                    return getUpcomingActivities();
                case "submit_adopt_application":
                    return submitAdoptApplication(args, user);
                case "add_health_record":
                    return addHealthRecord(args, user);
                case "create_service_order":
                    return createServiceOrder(args, user);
                case "get_adoption_statistics":
                    return getAdoptionStatistics(user);
                case "get_service_statistics":
                    return getServiceStatistics(user);
                case "get_user_statistics":
                    return getUserStatistics(user);
                case "get_my_service_orders":
                    return getMyServiceOrders(args, user);
                case "cancel_service_order":
                    return cancelServiceOrder(args, user);
                case "recommend_pets_for_me":
                    return recommendPetsForMe(user);
                case "generate_pet_health_advice":
                    return generatePetHealthAdvice(args, user);
                case "publish_knowledge_article":
                    return publishKnowledgeArticle(args, user);
                default:
                    throw new ServiceException(Constants.CODE_400, "未知工具：" + name);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "工具执行失败：" + e.getClass().getSimpleName());
        }
    }

    // ==================== 工具实现 ====================

    /**
     * 提交领养申请（写操作，两阶段：预检 → 用户确认 → 正式提交）
     * 支持按 animalId 或 animalName 定位动物，容忍模型跨轮次丢失 ID
     */
    private ToolExecutionResult submitAdoptApplication(JsonNode args, User user) {
        Integer animalId = optInt(args, "animalId");
        String animalName = textArg(args, "animalName");
        Animal animal;
        if (animalId != null) {
            animal = animalService.getById(animalId);
            if (animal == null) {
                return new ToolExecutionResult("{\"error\":\"该ID的动物不存在，若知道名字请改传 animalName，或先调用 search_adoptable_animals 查询\"}", "动物不存在");
            }
        } else if (notEmpty(animalName)) {
            List<Animal> matched = animalService.list(
                    new QueryWrapper<Animal>().like("nickname", animalName.trim()));
            matched.removeIf(a -> !"可领养".equals(a.getAdopt()) || !"否".equals(a.getIsAdopt()));
            if (matched.isEmpty()) {
                return new ToolExecutionResult("{\"error\":\"没有找到可领养的「" + animalName + "」，可调用 search_adoptable_animals 查看当前可领养列表\"}", "未找到可领养的「" + animalName + "」");
            }
            if (matched.size() > 1) {
                List<Map<String, Object>> candidates = new ArrayList<>();
                for (Animal a : matched) {
                    Map<String, Object> c = new LinkedHashMap<>();
                    c.put("animalId", a.getId());
                    c.put("nickname", a.getNickname());
                    c.put("type", a.getType());
                    c.put("age", a.getAge());
                    candidates.add(c);
                }
                return new ToolExecutionResult("{\"needConfirm\":true,\"message\":\"多只同名/相似动物，请让用户选择\",\"candidates\":" + toJson(candidates) + "}",
                        "找到 " + matched.size() + " 只相似动物，需要用户选择");
            }
            animal = matched.get(0);
        } else {
            return new ToolExecutionResult("{\"error\":\"缺少 animalId 或 animalName\"}", "缺少动物参数");
        }
        if (!"可领养".equals(animal.getAdopt()) || !"否".equals(animal.getIsAdopt())) {
            return new ToolExecutionResult("{\"error\":\"该动物当前不可领养\"}", "「" + animal.getNickname() + "」当前不可领养");
        }
        Applcation dup = applcationService.getOne(new QueryWrapper<Applcation>()
                .eq("user_id", user.getId())
                .eq("animal_id", animal.getId())
                .eq("state", "待审核"));
        if (dup != null) {
            return new ToolExecutionResult("{\"error\":\"您已有该宠物的待审核申请，请勿重复提交\"}", "重复申请被拒绝");
        }
        String reason = textArg(args, "reason");
        if (!notEmpty(reason)) {
            return new ToolExecutionResult("{\"error\":\"缺少领养理由，请先向用户询问领养理由\"}", "缺少领养理由");
        }
        boolean confirm = args.path("confirm").asBoolean(false);
        if (!confirm) {
            Map<String, Object> preview = new LinkedHashMap<>();
            preview.put("needConfirm", true);
            preview.put("animalId", animal.getId());
            preview.put("animalName", animal.getNickname());
            preview.put("animalInfo", animal.getType() + " / " + animal.getAge() + " / " + animal.getBodyType() + " / 性格" + animal.getPersonality());
            preview.put("applicant", user.getNickname());
            preview.put("reason", reason);
            preview.put("message", "请把以上申请摘要展示给用户并明确询问是否确认提交；用户同意后才允许 confirm=true 正式提交");
            return new ToolExecutionResult(toJson(preview), "生成了领养「" + animal.getNickname() + "」的申请预览，等待用户确认");
        }
        Applcation app = new Applcation();
        app.setUserId(user.getId());
        app.setAnimalId(animal.getId());
        app.setName(user.getNickname());
        app.setPhone(user.getPhone());
        app.setReason(reason);
        app.setState("待审核");
        app.setExperience(orDefault(textArg(args, "experience")));
        app.setHousing(orDefault(textArg(args, "housing")));
        app.setFamilyStructure(orDefault(textArg(args, "familyStructure")));
        applcationService.save(app);
        try {
            stringRedisTemplate.opsForValue().set("applcation:pending:" + app.getId(), "1", 48, java.util.concurrent.TimeUnit.HOURS);
        } catch (Exception ignore) {
        }
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("success", true);
        ok.put("applicationId", app.getId());
        ok.put("animalName", animal.getNickname());
        ok.put("state", "待审核");
        return new ToolExecutionResult(toJson(ok), "已为用户正式提交领养「" + animal.getNickname() + "」的申请（编号" + app.getId() + "）");
    }

    private String orDefault(String s) {
        return notEmpty(s) ? s : "未填写";
    }

    /**
     * 按 petId 或 petName 解析本人宠物（管理员可操作任意宠物）
     * 返回 PetProfile 或 ToolExecutionResult（错误/需确认时）
     */
    private Object resolvePet(JsonNode args, User user) {
        Integer petId = optInt(args, "petId");
        String petName = textArg(args, "petName");
        if (petId != null) {
            PetProfile pet = petProfileService.getById(petId);
            if (pet == null) {
                return new ToolExecutionResult("{\"error\":\"该ID的宠物不存在，可改传 petName 或先调用 get_my_pets 查询\"}", "宠物不存在");
            }
            boolean isAdmin = user.getRole() != null && !user.getRole().equals("ROLE_USER");
            if (!isAdmin && !user.getId().equals(pet.getOwnerId())) {
                return new ToolExecutionResult("{\"error\":\"只能操作自己的宠物\"}", "越权访问被拒绝");
            }
            return pet;
        }
        if (notEmpty(petName)) {
            List<PetProfile> matched = petProfileService.list(new QueryWrapper<PetProfile>()
                    .eq("owner_id", user.getId())
                    .like("pet_name", petName.trim()));
            if (matched.isEmpty()) {
                return new ToolExecutionResult("{\"error\":\"未找到名为「" + petName + "」的宠物，可调用 get_my_pets 查看宠物列表\"}",
                        "未找到宠物「" + petName + "」");
            }
            if (matched.size() > 1) {
                List<Map<String, Object>> candidates = new ArrayList<>();
                for (PetProfile p : matched) {
                    Map<String, Object> c = new LinkedHashMap<>();
                    c.put("petId", p.getId());
                    c.put("petName", p.getPetName());
                    c.put("breed", p.getBreed());
                    candidates.add(c);
                }
                return new ToolExecutionResult("{\"needConfirm\":true,\"message\":\"多只同名宠物，请让用户选择\",\"candidates\":" + toJson(candidates) + "}",
                        "找到 " + matched.size() + " 只同名宠物，需要确认");
            }
            return matched.get(0);
        }
        return new ToolExecutionResult("{\"error\":\"缺少 petId 或 petName 参数\"}", "缺少宠物参数");
    }

    // ==================== 写操作：健康记录录入 ====================

    private ToolExecutionResult addHealthRecord(JsonNode args, User user) {
        Object resolved = resolvePet(args, user);
        if (resolved instanceof ToolExecutionResult) {
            return (ToolExecutionResult) resolved;
        }
        PetProfile pet = (PetProfile) resolved;

        String recordType = textArg(args, "recordType");
        if (!notEmpty(recordType) || !RECORD_TYPES.contains(recordType.trim())) {
            return new ToolExecutionResult("{\"error\":\"recordType 必须是 疫苗/驱虫/体检/治疗 之一\"}", "记录类型不合法");
        }
        String itemName = textArg(args, "itemName");
        if (!notEmpty(itemName)) {
            return new ToolExecutionResult("{\"error\":\"缺少 itemName（项目名称），请向用户询问\"}", "缺少项目名称");
        }
        String recordDateStr = textArg(args, "recordDate");
        LocalDate recordDate;
        if (!notEmpty(recordDateStr)) {
            return new ToolExecutionResult("{\"error\":\"缺少 recordDate（记录日期），请向用户询问\"}", "缺少记录日期");
        }
        try {
            recordDate = LocalDate.parse(recordDateStr.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return new ToolExecutionResult("{\"error\":\"recordDate 格式错误，必须为 yyyy-MM-dd，请换算后重新调用\"}", "日期格式不合法");
        }
        LocalDate nextDate = null;
        if (notEmpty(textArg(args, "nextDate"))) {
            try {
                nextDate = LocalDate.parse(textArg(args, "nextDate").trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                return new ToolExecutionResult("{\"error\":\"nextDate 格式错误，必须为 yyyy-MM-dd\"}", "下次提醒日期格式不合法");
            }
        }
        BigDecimal weight = null;
        if (args.hasNonNull("weight") && args.get("weight").isNumber()) {
            weight = args.get("weight").decimalValue();
        }

        boolean confirm = args.path("confirm").asBoolean(false);
        if (!confirm) {
            Map<String, Object> preview = new LinkedHashMap<>();
            preview.put("needConfirm", true);
            preview.put("petName", pet.getPetName());
            preview.put("recordType", recordType.trim());
            preview.put("itemName", itemName.trim());
            preview.put("recordDate", recordDate.toString());
            preview.put("hospital", textArg(args, "hospital"));
            preview.put("doctor", textArg(args, "doctor"));
            preview.put("nextDate", nextDate == null ? null : nextDate.toString());
            preview.put("weight", weight);
            preview.put("message", "请把以上记录摘要展示给用户并明确询问是否确认录入；用户同意后才允许 confirm=true 正式写入");
            return new ToolExecutionResult(toJson(preview), "生成了「" + pet.getPetName() + "」的健康记录预览，等待用户确认");
        }

        HealthRecord record = new HealthRecord();
        record.setPetId(pet.getId());
        record.setRecordType(recordType.trim());
        record.setItemName(itemName.trim());
        record.setRecordDate(recordDate);
        record.setHospital(textArg(args, "hospital"));
        record.setDoctor(textArg(args, "doctor"));
        record.setNextDate(nextDate);
        record.setRemark(textArg(args, "remark"));
        record.setWeight(weight);
        healthRecordService.save(record);

        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("success", true);
        ok.put("recordId", record.getId());
        ok.put("petName", pet.getPetName());
        return new ToolExecutionResult(toJson(ok),
                "已为「" + pet.getPetName() + "」录入健康记录「" + itemName.trim() + "」");
    }

    // ==================== 写操作：服务预约下单 ====================

    private ToolExecutionResult createServiceOrder(JsonNode args, User user) {
        // 解析服务项
        Integer serviceId = optInt(args, "serviceId");
        String serviceName = textArg(args, "serviceName");
        ServiceItem service;
        if (serviceId != null) {
            service = serviceItemService.getById(serviceId);
            if (service == null || service.getStatus() == null || service.getStatus() != 1) {
                return new ToolExecutionResult("{\"error\":\"该ID的服务不存在或已停用，可改传 serviceName 或调用 recommend_services 查询\"}", "服务不存在或已停用");
            }
        } else if (notEmpty(serviceName)) {
            List<ServiceItem> matched = serviceItemService.list(new QueryWrapper<ServiceItem>()
                    .like("name", serviceName.trim())
                    .eq("status", 1));
            if (matched.isEmpty()) {
                return new ToolExecutionResult("{\"error\":\"没有找到名为「" + serviceName + "」的启用服务，可调用 recommend_services 查看服务列表\"}",
                        "未找到服务「" + serviceName + "」");
            }
            if (matched.size() > 1) {
                List<Map<String, Object>> candidates = new ArrayList<>();
                for (ServiceItem s : matched) {
                    Map<String, Object> c = new LinkedHashMap<>();
                    c.put("serviceId", s.getId());
                    c.put("name", s.getName());
                    c.put("price", s.getPrice() == null ? null : s.getPrice().toString());
                    c.put("storeName", s.getStoreName());
                    candidates.add(c);
                }
                return new ToolExecutionResult("{\"needConfirm\":true,\"message\":\"多个相似服务，请让用户选择\",\"candidates\":" + toJson(candidates) + "}",
                        "找到 " + matched.size() + " 个相似服务，需要用户选择");
            }
            service = matched.get(0);
        } else {
            return new ToolExecutionResult("{\"error\":\"缺少 serviceName 或 serviceId 参数\"}", "缺少服务参数");
        }

        // 解析宠物
        Object resolved = resolvePet(args, user);
        if (resolved instanceof ToolExecutionResult) {
            return (ToolExecutionResult) resolved;
        }
        PetProfile pet = (PetProfile) resolved;

        // 解析预约时间
        String timeStr = textArg(args, "appointmentTime");
        if (!notEmpty(timeStr)) {
            return new ToolExecutionResult("{\"error\":\"缺少 appointmentTime，请向用户询问期望的预约时间\"}", "缺少预约时间");
        }
        LocalDateTime appointmentTime;
        try {
            appointmentTime = LocalDateTime.parse(timeStr.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            return new ToolExecutionResult("{\"error\":\"appointmentTime 格式错误，必须为 yyyy-MM-dd HH:mm，请结合当前时间换算后重新调用\"}", "预约时间格式不合法");
        }
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            return new ToolExecutionResult("{\"error\":\"预约时间不能早于当前时间\"}", "预约时间已是过去");
        }

        // 冲突预检（与正式下单同一标准：同服务+同时间+非取消）
        long conflict = serviceOrderService.count(new QueryWrapper<ServiceOrder>()
                .eq("service_id", service.getId())
                .eq("appointment_time", appointmentTime)
                .ne("status", "CANCELLED"));

        boolean confirm = args.path("confirm").asBoolean(false);
        if (!confirm) {
            Map<String, Object> preview = new LinkedHashMap<>();
            preview.put("needConfirm", true);
            preview.put("serviceName", service.getName());
            preview.put("price", service.getPrice() == null ? null : service.getPrice().toString());
            preview.put("durationMinutes", service.getDuration());
            preview.put("storeName", service.getStoreName());
            preview.put("storePhone", service.getStorePhone());
            preview.put("petName", pet.getPetName());
            preview.put("appointmentTime", appointmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            preview.put("timeConflict", conflict > 0);
            preview.put("message", conflict > 0
                    ? "该时间段已被预约，请让用户换一个时间"
                    : "请把以上订单预览展示给用户并明确询问是否确认下单；用户同意后才允许 confirm=true 正式提交");
            return new ToolExecutionResult(toJson(preview),
                    conflict > 0 ? "该时段已被预约" : "生成了「" + service.getName() + "」的预约预览，等待用户确认");
        }
        if (conflict > 0) {
            return new ToolExecutionResult("{\"error\":\"该时间段已被预约，请让用户选择其他时间\"}", "时段冲突，下单被拒绝");
        }

        // 正式下单：复用平台现有下单逻辑（并发锁 + 二次冲突校验 + 订单号生成 + 门店信息带出）
        ServiceOrderDTO dto = new ServiceOrderDTO();
        dto.setServiceId(service.getId());
        dto.setPetId(pet.getId());
        dto.setAppointmentTime(appointmentTime);
        dto.setRemark(textArg(args, "remark"));
        String orderNo = serviceOrderService.submitOrder(dto, user.getId());

        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("success", true);
        ok.put("orderNo", orderNo);
        ok.put("serviceName", service.getName());
        ok.put("storeName", service.getStoreName());
        ok.put("appointmentTime", appointmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        ok.put("status", "PENDING（待商家确认）");
        return new ToolExecutionResult(toJson(ok),
                "已为「" + pet.getPetName() + "」提交服务预约（订单号 " + orderNo + "）");
    }

    // ==================== 统计工具（仅管理员） ====================

    private void assertAdmin(User user) {
        boolean admin = user.getRole() != null && !"ROLE_USER".equals(user.getRole());
        if (!admin) {
            throw new ServiceException(Constants.CODE_401, "无权限：平台统计数据仅管理员可查看");
        }
    }

    private ToolExecutionResult getAdoptionStatistics(User user) {
        assertAdmin(user);
        long totalApps = applcationService.count();
        long pending = applcationService.count(new QueryWrapper<Applcation>().eq("state", "待审核"));
        long passed = applcationService.count(new QueryWrapper<Applcation>().eq("state", "审核通过"));
        long rejected = applcationService.count(new QueryWrapper<Applcation>().eq("state", "审核不通过"));
        String passRate = (passed + rejected) == 0 ? "暂无已审结申请"
                : Math.round(passed * 1000.0 / (passed + rejected)) / 10.0 + "%";
        long animals = animalService.count();
        long adoptable = animalService.count(new QueryWrapper<Animal>().eq("adopt", "可领养").eq("is_adopt", "否"));
        long adopted = animalService.count(new QueryWrapper<Animal>().eq("is_adopt", "是"));
        Map<String, Long> byBodyType = new LinkedHashMap<>();
        for (Animal a : animalService.list()) {
            String key = a.getBodyType() == null ? "未知" : a.getBodyType();
            byBodyType.merge(key, 1L, Long::sum);
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("领养申请总数", totalApps);
        data.put("待审核", pending);
        data.put("已通过", passed);
        data.put("已拒绝", rejected);
        data.put("审核通过率", passRate);
        data.put("流浪动物总数", animals);
        data.put("可领养", adoptable);
        data.put("已被领养", adopted);
        data.put("体型分布", byBodyType);
        return new ToolExecutionResult(toJson(data), "统计了领养业务数据");
    }

    private ToolExecutionResult getServiceStatistics(User user) {
        assertAdmin(user);
        List<ServiceOrder> orders = serviceOrderService.list();
        long total = orders.size();
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ServiceOrder o : orders) {
            byStatus.merge(o.getStatus() == null ? "未知" : o.getStatus(), 1L, Long::sum);
        }
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long monthOrders = orders.stream().filter(o -> o.getCreateTime() != null && o.getCreateTime().isAfter(monthStart)).count();
        // 已完成订单收入（订单表不存价格，按关联服务项目价格求和，与现有统计口径一致）
        BigDecimal income = BigDecimal.ZERO;
        Map<Integer, Long> serviceCount = new java.util.HashMap<>();
        for (ServiceOrder o : orders) {
            if (o.getServiceId() != null) {
                serviceCount.merge(o.getServiceId(), 1L, Long::sum);
            }
        }
        Map<Integer, ServiceItem> itemMap = new HashMap<>();
        if (!serviceCount.isEmpty()) {
            for (ServiceItem s : serviceItemService.listByIds(serviceCount.keySet())) {
                itemMap.put(s.getId(), s);
            }
        }
        for (ServiceOrder o : orders) {
            if ("COMPLETED".equals(o.getStatus()) && itemMap.get(o.getServiceId()) != null && itemMap.get(o.getServiceId()).getPrice() != null) {
                income = income.add(itemMap.get(o.getServiceId()).getPrice());
            }
        }
        List<Map<String, Object>> top5 = new ArrayList<>();
        serviceCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .forEach(e -> {
                    Map<String, Object> t = new LinkedHashMap<>();
                    ServiceItem s = itemMap.get(e.getKey());
                    t.put("服务", s == null ? "已删除服务#" + e.getKey() : s.getName());
                    t.put("订单量", e.getValue());
                    top5.add(t);
                });
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("订单总数", total);
        data.put("状态分布", byStatus);
        data.put("本月新增订单", monthOrders);
        data.put("已完成订单总收入", income.toPlainString() + " 元");
        data.put("订单量Top5服务", top5);
        return new ToolExecutionResult(toJson(data), "统计了服务业务数据");
    }

    private ToolExecutionResult getUserStatistics(User user) {
        assertAdmin(user);
        long total = userService.count();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        java.util.Date monthStartDate = java.sql.Timestamp.valueOf(monthStart);
        List<com.ly.pet.entity.User> users = userService.list();
        long monthNew = users.stream()
                .filter(u -> u.getCreateTime() != null && u.getCreateTime().after(monthStartDate))
                .count();
        long withPet = petProfileService.list().stream().map(PetProfile::getOwnerId).distinct().count();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("注册用户总数", total);
        data.put("本月新增", monthNew);
        data.put("有宠物档案的用户数", withPet);
        return new ToolExecutionResult(toJson(data), "统计了用户数据");
    }

    // ==================== 订单查询与取消 ====================

    private ToolExecutionResult getMyServiceOrders(JsonNode args, User user) {
        QueryWrapper<ServiceOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId());
        String status = textArg(args, "status");
        if (notEmpty(status)) {
            wrapper.eq("status", status.trim().toUpperCase());
        }
        wrapper.orderByDesc("id");
        wrapper.last("LIMIT 10");
        List<ServiceOrder> orders = serviceOrderService.list(wrapper);
        List<Map<String, Object>> items = new ArrayList<>();
        for (ServiceOrder o : orders) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("orderId", o.getId());
            item.put("orderNo", o.getOrderNo());
            ServiceItem s = o.getServiceId() == null ? null : serviceItemService.getById(o.getServiceId());
            item.put("serviceName", s == null ? "已删除服务" : s.getName());
            item.put("appointmentTime", o.getAppointmentTime() == null ? null
                    : o.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            item.put("status", o.getStatus());
            item.put("storeName", o.getStoreName());
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items), "查询到 " + items.size() + " 个服务订单");
    }

    private ToolExecutionResult cancelServiceOrder(JsonNode args, User user) {
        Integer orderId = optInt(args, "orderId");
        String orderNo = textArg(args, "orderNo");
        ServiceOrder order;
        if (orderId != null) {
            order = serviceOrderService.getById(orderId);
        } else if (notEmpty(orderNo)) {
            order = serviceOrderService.getOne(new QueryWrapper<ServiceOrder>().eq("order_no", orderNo.trim()));
        } else {
            return new ToolExecutionResult("{\"error\":\"缺少 orderId 或 orderNo，可调用 get_my_service_orders 查询\"}", "缺少订单参数");
        }
        if (order == null) {
            return new ToolExecutionResult("{\"error\":\"订单不存在，可调用 get_my_service_orders 查询自己的订单\"}", "订单不存在");
        }
        if (!user.getId().equals(order.getUserId())) {
            return new ToolExecutionResult("{\"error\":\"只能取消自己的订单\"}", "越权取消被拒绝");
        }
        String cancelReason = textArg(args, "cancelReason");
        if (!notEmpty(cancelReason)) {
            return new ToolExecutionResult("{\"error\":\"缺少取消原因，请向用户询问\"}", "缺少取消原因");
        }
        boolean confirm = args.path("confirm").asBoolean(false);
        if (!confirm) {
            ServiceItem s = order.getServiceId() == null ? null : serviceItemService.getById(order.getServiceId());
            Map<String, Object> preview = new LinkedHashMap<>();
            preview.put("needConfirm", true);
            preview.put("orderNo", order.getOrderNo());
            preview.put("serviceName", s == null ? "已删除服务" : s.getName());
            preview.put("appointmentTime", order.getAppointmentTime() == null ? null
                    : order.getAppointmentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            preview.put("status", order.getStatus());
            preview.put("cancelReason", cancelReason);
            preview.put("message", "请把以上订单信息展示给用户并明确询问是否确认取消；用户同意后才允许 confirm=true 正式取消");
            return new ToolExecutionResult(toJson(preview), "生成了订单 " + order.getOrderNo() + " 的取消预览，等待用户确认");
        }
        // 复用平台现有取消逻辑（本人校验 + 状态校验 + 取消时间记录），异常会带回给模型
        serviceOrderService.cancelOrder(order.getId(), user.getId(), cancelReason);
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("success", true);
        ok.put("orderNo", order.getOrderNo());
        ok.put("status", "CANCELLED");
        return new ToolExecutionResult(toJson(ok), "已取消订单 " + order.getOrderNo());
    }

    // ==================== 智能推荐（算法融合） ====================

    private ToolExecutionResult recommendPetsForMe(User user) {
        Applcation latest = applcationService.getOne(new QueryWrapper<Applcation>()
                .eq("user_id", user.getId())
                .orderByDesc("id")
                .last("LIMIT 1"));
        if (latest == null) {
            return new ToolExecutionResult("{\"error\":\"您还没有领养申请记录，暂时无法构建领养画像；请先在领养页提交一份申请，或告诉我您的养宠经验、住房条件等，我按经验给您建议\"}",
                    "用户无申请画像");
        }
        Map<String, String> userProfile = new HashMap<>();
        userProfile.put("experience", latest.getExperience());
        userProfile.put("housing", latest.getHousing());
        userProfile.put("income", latest.getIncome());
        userProfile.put("familyStructure", latest.getFamilyStructure());
        List<Animal> adoptable = animalService.list(new QueryWrapper<Animal>()
                .eq("adopt", "可领养")
                .eq("is_adopt", "否"));
        if (adoptable.isEmpty()) {
            return new ToolExecutionResult("[]", "暂无可领养动物");
        }
        List<Map<String, Object>> ranked = new ArrayList<>();
        for (Animal a : adoptable) {
            int score = adoptMatchService.calculateMatchScore(a, userProfile);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("animalId", a.getId());
            item.put("nickname", a.getNickname());
            item.put("type", a.getType());
            item.put("age", a.getAge());
            item.put("bodyType", a.getBodyType());
            item.put("personality", a.getPersonality());
            item.put("matchScore", score);
            item.put("matchLevel", adoptMatchService.getMatchLevel(score));
            ranked.add(item);
        }
        ranked.sort((x, y) -> Integer.compare((Integer) y.get("matchScore"), (Integer) x.get("matchScore")));
        List<Map<String, Object>> top3 = ranked.subList(0, Math.min(3, ranked.size()));
        StringBuilder brief = new StringBuilder("用匹配算法为用户评估了 " + ranked.size() + " 只动物，推荐：");
        for (Map<String, Object> t : top3) {
            brief.append(t.get("nickname")).append("(").append(t.get("matchScore")).append("分) ");
        }
        return new ToolExecutionResult(toJson(top3), brief.toString());
    }

    // ==================== 对话式健康建议 ====================

    private ToolExecutionResult generatePetHealthAdvice(JsonNode args, User user) {
        Object resolved = resolvePet(args, user);
        if (resolved instanceof ToolExecutionResult) {
            return (ToolExecutionResult) resolved;
        }
        PetProfile pet = (PetProfile) resolved;
        boolean confirm = args.path("confirm").asBoolean(false);
        if (!confirm) {
            Map<String, Object> preview = new LinkedHashMap<>();
            preview.put("needConfirm", true);
            preview.put("petName", pet.getPetName());
            preview.put("message", "AI 将根据「" + pet.getPetName() + "」的档案和健康记录生成个性化健康建议（5-8条），并覆盖该宠物现有的建议。请向用户展示本说明并询问是否继续；同意后以 confirm=true 执行");
            return new ToolExecutionResult(toJson(preview), "说明了为「" + pet.getPetName() + "」生成健康建议的操作，等待用户确认");
        }
        Map<String, Object> result = healthAdviceAiService.generateForPet(pet.getId());
        return new ToolExecutionResult(toJson(result),
                "已为「" + pet.getPetName() + "」生成 " + result.get("count") + " 条健康建议（来源：" + result.get("source") + "）");
    }

    // ==================== 科普文章创作（仅管理员） ====================

    private ToolExecutionResult publishKnowledgeArticle(JsonNode args, User user) {
        assertAdmin(user);
        String title = textArg(args, "title");
        if (!notEmpty(title)) {
            return new ToolExecutionResult("{\"error\":\"缺少文章标题\"}", "缺少标题");
        }
        String content = textArg(args, "content");
        if (!notEmpty(content) || content.trim().length() < 50) {
            return new ToolExecutionResult("{\"error\":\"文章正文缺失或过短（至少50字），请撰写完整正文后重新调用\"}", "正文缺失或过短");
        }
        // 服务端强制两阶段：草稿挂在当前用户名下（Redis 10 分钟），confirm=true 只会发布已预检过的定稿
        // 同一用户重新预检会覆盖旧草稿；未预检直接 confirm 会被拒绝
        boolean confirm = args.path("confirm").asBoolean(false);
        String pendingKey = "ai:article:pending:" + user.getId();
        if (!confirm) {
            try {
                stringRedisTemplate.opsForValue().set(pendingKey, title.trim() + "|" + content.trim(), 10, java.util.concurrent.TimeUnit.MINUTES);
            } catch (Exception e) {
                throw new ServiceException(Constants.CODE_500, "草稿暂存失败（Redis 不可用），请稍后重试");
            }
            Map<String, Object> preview = new LinkedHashMap<>();
            preview.put("needConfirm", true);
            preview.put("title", title.trim());
            preview.put("wordCount", content.trim().length());
            String plain = content.replaceAll("[#*`\\-\\n]", "");
            preview.put("preview", plain.length() > 120 ? plain.substring(0, 120) + "…" : plain);
            preview.put("message", "请把标题与开头预览展示给管理员并明确询问是否发布；管理员同意后仅传 confirm=true 正式发布");
            return new ToolExecutionResult(toJson(preview), "完成了科普文章「" + title.trim() + "」的撰写预览，等待管理员确认");
        }
        // 校验挂起草稿：发布内容以预检时服务器保存的定稿为准（避免模型无法逐字复述长文）
        String stored = null;
        try {
            stored = stringRedisTemplate.opsForValue().get(pendingKey);
        } catch (Exception ignore) {
        }
        if (stored == null || !stored.contains("|")) {
            return new ToolExecutionResult("{\"error\":\"服务端要求两阶段发布：当前没有已预检的草稿。请立即再次调用本工具，不传 confirm、传入你撰写的 title 与 content 完成预检，然后把预览展示给管理员，等待其明确同意后再以 confirm=true 发布\"}", "未预检直接发布被拒绝");
        }
        int sep = stored.indexOf('|');
        String finalTitle = stored.substring(0, sep);
        String finalContent = stored.substring(sep + 1);
        try {
            stringRedisTemplate.delete(pendingKey);
        } catch (Exception ignore) {
        }
        ArticleKp article = new ArticleKp();
        article.setName(finalTitle);
        article.setContent(finalContent);
        article.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        article.setRead1(0);
        articleKpService.save(article);
        Map<String, Object> ok = new LinkedHashMap<>();
        ok.put("success", true);
        ok.put("articleId", article.getId());
        ok.put("title", finalTitle);
        return new ToolExecutionResult(toJson(ok), "已发布科普文章「" + finalTitle + "」");
    }

    private ToolExecutionResult searchAdoptableAnimals(JsonNode args) {
        QueryWrapper<Animal> wrapper = new QueryWrapper<>();
        wrapper.eq("adopt", "可领养");
        wrapper.eq("is_adopt", "否");
        String keyword = textArg(args, "keyword");
        if (notEmpty(keyword)) {
            wrapper.and(w -> w.like("nickname", keyword).or().like("type", keyword));
        }
        String type = textArg(args, "type");
        if (notEmpty(type)) {
            wrapper.like("type", type);
        }
        String bodyType = textArg(args, "bodyType");
        if (notEmpty(bodyType)) {
            wrapper.eq("body_type", bodyType);
        }
        wrapper.last("LIMIT " + limitArg(args, 8, 20));
        List<Animal> animals = animalService.list(wrapper);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Animal a : animals) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("nickname", a.getNickname());
            item.put("type", a.getType());
            item.put("sex", a.getSex());
            item.put("age", a.getAge());
            item.put("bodyType", a.getBodyType());
            item.put("personality", a.getPersonality());
            item.put("clusterTag", a.getClusterTag());
            item.put("vaccine", a.getVaccine());
            item.put("sterilization", a.getSterilization());
            item.put("address", a.getAddress());
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items),
                "查询到 " + items.size() + " 只可领养动物");
    }

    private ToolExecutionResult getAnimalDetail(JsonNode args) {
        Integer animalId = intArg(args, "animalId");
        Animal a = animalService.getById(animalId);
        if (a == null) {
            return new ToolExecutionResult("{\"error\":\"动物不存在\"}", "动物不存在");
        }
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", a.getId());
        item.put("nickname", a.getNickname());
        item.put("type", a.getType());
        item.put("sex", a.getSex());
        item.put("age", a.getAge());
        item.put("bodyType", a.getBodyType());
        item.put("personality", a.getPersonality());
        item.put("clusterTag", a.getClusterTag());
        item.put("status", a.getStatus());
        item.put("vaccine", a.getVaccine());
        item.put("sterilization", a.getSterilization());
        item.put("adopt", a.getAdopt());
        item.put("isAdopt", a.getIsAdopt());
        item.put("address", a.getAddress());
        item.put("information", a.getInformation());
        return new ToolExecutionResult(toJson(item), "查询了动物「" + a.getNickname() + "」的详情");
    }

    private ToolExecutionResult getMyApplications(User user) {
        QueryWrapper<Applcation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getId());
        wrapper.orderByDesc("id");
        List<Applcation> list = applcationService.list(wrapper);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Applcation app : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", app.getId());
            item.put("animalId", app.getAnimalId());
            Animal animal = app.getAnimalId() == null ? null : animalService.getById(app.getAnimalId());
            item.put("animalName", animal == null ? null : animal.getNickname());
            item.put("animalType", animal == null ? null : animal.getType());
            item.put("state", app.getState());
            item.put("experience", app.getExperience());
            item.put("housing", app.getHousing());
            item.put("familyStructure", app.getFamilyStructure());
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items), "查询到 " + items.size() + " 条领养申请记录");
    }

    private ToolExecutionResult getMyPets(User user) {
        QueryWrapper<PetProfile> wrapper = new QueryWrapper<>();
        wrapper.eq("owner_id", user.getId());
        wrapper.orderByDesc("id");
        List<PetProfile> pets = petProfileService.list(wrapper);
        List<Map<String, Object>> items = new ArrayList<>();
        for (PetProfile p : pets) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", p.getId());
            item.put("petName", p.getPetName());
            item.put("breed", p.getBreed());
            item.put("petType", p.getPetType());
            item.put("ageMonth", p.getAge());
            item.put("gender", p.getGender());
            item.put("weight", p.getWeight() == null ? null : p.getWeight().toString());
            item.put("neutered", p.getNeutered());
            item.put("tags", p.getTags());
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items), "查询到 " + items.size() + " 只宠物档案");
    }

    private ToolExecutionResult getPetHealthRecords(JsonNode args, User user) {
        Object resolved = resolvePet(args, user);
        if (resolved instanceof ToolExecutionResult) {
            return (ToolExecutionResult) resolved;
        }
        PetProfile pet = (PetProfile) resolved;
        QueryWrapper<HealthRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("pet_id", pet.getId());
        wrapper.orderByDesc("record_date");
        wrapper.last("LIMIT 30");
        List<HealthRecord> records = healthRecordService.list(wrapper);
        List<Map<String, Object>> items = new ArrayList<>();
        for (HealthRecord r : records) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("recordType", r.getRecordType());
            item.put("itemName", r.getItemName());
            item.put("recordDate", r.getRecordDate() == null ? null : r.getRecordDate().toString());
            item.put("nextDate", r.getNextDate() == null ? null : r.getNextDate().toString());
            item.put("hospital", r.getHospital());
            item.put("weight", r.getWeight() == null ? null : r.getWeight().toString());
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items),
                "查询了宠物「" + pet.getPetName() + "」的 " + items.size() + " 条健康记录");
    }

    private ToolExecutionResult getPendingHealthAdvice(JsonNode args, User user) {
        List<PetProfile> myPets = petProfileService.list(
                new QueryWrapper<PetProfile>().eq("owner_id", user.getId()));
        if (myPets.isEmpty()) {
            return new ToolExecutionResult("[]", "暂无宠物档案");
        }
        Integer petIdFilter = args == null ? null : optInt(args, "petId");
        List<Integer> petIds = new ArrayList<>();
        Map<Integer, String> petNameMap = new HashMap<>();
        for (PetProfile p : myPets) {
            if (petIdFilter != null && !petIdFilter.equals(p.getId())) {
                continue;
            }
            petIds.add(p.getId());
            petNameMap.put(p.getId(), p.getPetName());
        }
        if (petIds.isEmpty()) {
            return new ToolExecutionResult("[]", "未找到该宠物的健康建议");
        }
        QueryWrapper<HealthAdvice> wrapper = new QueryWrapper<>();
        wrapper.in("pet_id", petIds);
        wrapper.eq("is_handled", 0);
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT 20");
        List<HealthAdvice> advices = healthAdviceService.list(wrapper);
        List<Map<String, Object>> items = new ArrayList<>();
        for (HealthAdvice a : advices) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("petName", petNameMap.get(a.getPetId()));
            item.put("adviceType", a.getAdviceType());
            item.put("title", a.getTitle());
            item.put("content", a.getContent());
            item.put("priority", a.getPriority());
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items), "查询到 " + items.size() + " 条待处理健康建议");
    }

    private ToolExecutionResult recommendServices(JsonNode args, User user) {
        String type = textArg(args, "recommendType");
        int limit = limitArg(args, 5, 10);
        List<com.ly.pet.entity.RecommendedService> list;
        String label;
        if ("hot".equalsIgnoreCase(type)) {
            list = recommendationService.getHotRecommendations(limit);
            label = "热门";
        } else {
            list = recommendationService.getRecommendations(user.getId(), limit);
            label = "个性化";
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (com.ly.pet.entity.RecommendedService s : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("serviceId", s.getServiceId());
            item.put("name", s.getName());
            item.put("type", s.getType());
            item.put("price", s.getPrice() == null ? null : s.getPrice().toString());
            item.put("duration", s.getDuration());
            item.put("storeName", s.getStoreName());
            item.put("avgRating", s.getAvgRating() == null ? null : s.getAvgRating().toString());
            item.put("reason", s.getReason());
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items), "获取了 " + items.size() + " 个" + label + "推荐服务");
    }

    private ToolExecutionResult searchRescueStations(JsonNode args) {
        QueryWrapper<Rescue> wrapper = new QueryWrapper<>();
        String keyword = textArg(args, "keyword");
        if (notEmpty(keyword)) {
            wrapper.and(w -> w.like("name", keyword).or().like("addres", keyword));
        }
        wrapper.last("LIMIT 10");
        List<Rescue> list = rescueService.list(wrapper);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Rescue r : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", r.getId());
            item.put("name", r.getName());
            item.put("address", r.getAddres());
            item.put("contact", r.getPerson());
            item.put("phone", r.getPhone());
            item.put("information", r.getInformation());
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items), "查询到 " + items.size() + " 个救助站");
    }

    private ToolExecutionResult searchFeedPoints(JsonNode args) {
        QueryWrapper<Feed> wrapper = new QueryWrapper<>();
        String keyword = textArg(args, "keyword");
        if (notEmpty(keyword)) {
            wrapper.like("address", keyword);
        }
        wrapper.last("LIMIT 10");
        List<Feed> list = feedService.list(wrapper);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Feed f : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", f.getId());
            item.put("address", f.getAddress());
            item.put("information", f.getInformation());
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items), "查询到 " + items.size() + " 个喂食点");
    }

    private ToolExecutionResult getUpcomingActivities() {
        List<Activity> list = activityService.list(
                new QueryWrapper<Activity>().orderByDesc("id").last("LIMIT 10"));
        List<Map<String, Object>> items = new ArrayList<>();
        for (Activity a : list) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("name", a.getName());
            item.put("time", a.getTime());
            item.put("address", a.getAddress());
            item.put("capacity", a.getNum());
            String content = a.getContent();
            item.put("content", content != null && content.length() > 120 ? content.substring(0, 120) + "…" : content);
            items.add(item);
        }
        return new ToolExecutionResult(toJson(items), "查询到 " + items.size() + " 个公益活动");
    }

    // ==================== 工具辅助 ====================

    /**
     * 校验宠物归属：普通用户只能操作自己的宠物，管理员可操作任意宠物
     */
    private PetProfile assertPetOwner(Integer petId, User user) {
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException(Constants.CODE_400, "宠物不存在");
        }
        boolean isAdmin = user.getRole() != null && !user.getRole().equals("ROLE_USER");
        if (!isAdmin && !user.getId().equals(pet.getOwnerId())) {
            throw new ServiceException(Constants.CODE_401, "只能查询自己的宠物");
        }
        return pet;
    }

    private JsonNode parseArgs(String argumentsJson) {
        if (argumentsJson == null || argumentsJson.trim().isEmpty()) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(argumentsJson);
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_400, "工具参数不是合法 JSON");
        }
    }

    private String textArg(JsonNode args, String field) {
        return args == null ? null : args.path(field).asText(null);
    }

    private Integer optInt(JsonNode args, String field) {
        return args != null && args.has(field) && args.get(field).isInt() ? args.get(field).asInt() : null;
    }

    private Integer intArg(JsonNode args, String field) {
        Integer value = optInt(args, field);
        if (value == null) {
            throw new ServiceException(Constants.CODE_400, "缺少必填参数：" + field);
        }
        return value;
    }

    private int limitArg(JsonNode args, int defaultLimit, int maxLimit) {
        Integer limit = optInt(args, "limit");
        if (limit == null || limit <= 0) {
            return defaultLimit;
        }
        return Math.min(limit, maxLimit);
    }

    private boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }

    private Map<String, Object> strProp(String description) {
        Map<String, Object> prop = new HashMap<>();
        prop.put("type", "string");
        prop.put("description", description);
        return prop;
    }

    private Map<String, Object> intProp(String description) {
        Map<String, Object> prop = new HashMap<>();
        prop.put("type", "integer");
        prop.put("description", description);
        return prop;
    }

    private Map<String, Object> objectSchema(Map<String, Object> properties, List<String> required) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        if (required != null && !required.isEmpty()) {
            schema.put("required", required);
        }
        return schema;
    }
}
