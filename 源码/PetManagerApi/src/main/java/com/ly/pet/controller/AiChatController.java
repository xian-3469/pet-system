package com.ly.pet.controller;

import com.ly.pet.common.Constants;
import com.ly.pet.common.Result;
import com.ly.pet.entity.Applcation;
import com.ly.pet.entity.PetProfile;
import com.ly.pet.entity.User;
import com.ly.pet.exception.ServiceException;
import com.ly.pet.dto.AiChatRequest;
import com.ly.pet.service.AdoptAnalysisService;
import com.ly.pet.service.AgentChatService;
import com.ly.pet.service.HealthAdviceAiService;
import com.ly.pet.service.IApplcationService;
import com.ly.pet.service.IPetProfileService;
import com.ly.pet.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * AI 智能助手 Controller
 * 提供全局对话助手、AI 健康建议生成、领养匹配 AI 分析
 */
@Api(tags = "AI 智能助手")
@RestController
@RequestMapping("/ai")
public class AiChatController {

    @Resource
    private AgentChatService agentChatService;

    @Resource
    private HealthAdviceAiService healthAdviceAiService;

    @Resource
    private AdoptAnalysisService adoptAnalysisService;

    @Resource
    private IPetProfileService petProfileService;

    @Resource
    private IApplcationService applcationService;

    /**
     * 全局 AI 助手对话（Agent 模式，可调用平台工具查询真实数据）
     */
    @ApiOperation("AI 助手对话")
    @PostMapping("/chat")
    public Result chat(@RequestBody AiChatRequest request) {
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new ServiceException(Constants.CODE_400, "消息内容不能为空");
        }
        User user = TokenUtils.getCurrentUser();
        if (user == null) {
            throw new ServiceException(Constants.CODE_401, "请先登录后再使用 AI 助手");
        }
        return Result.success(agentChatService.chat(request.getHistory(), request.getMessage().trim()));
    }

    /**
     * 为指定宠物生成 AI 健康建议（仅本人或管理员）
     */
    @ApiOperation("生成 AI 健康建议")
    @PostMapping("/health-advice/generate/{petId}")
    public Result generateHealthAdvice(@PathVariable Integer petId) {
        User user = assertLogin();
        PetProfile pet = petProfileService.getById(petId);
        if (pet == null) {
            throw new ServiceException(Constants.CODE_400, "宠物不存在");
        }
        if (!isAdmin(user) && !user.getId().equals(pet.getOwnerId())) {
            throw new ServiceException(Constants.CODE_401, "只能为自己的宠物生成 AI 健康建议");
        }
        return Result.success(healthAdviceAiService.generateForPet(petId));
    }

    /**
     * 领养申请 AI 匹配分析（申请人本人或管理员）
     */
    @ApiOperation("领养申请 AI 匹配分析")
    @GetMapping("/adopt-analysis/{applicationId}")
    public Result adoptAnalysis(@PathVariable Integer applicationId) {
        User user = assertLogin();
        Applcation application = applcationService.getById(applicationId);
        if (application == null) {
            throw new ServiceException(Constants.CODE_400, "领养申请不存在");
        }
        if (!isAdmin(user) && !user.getId().equals(application.getUserId())) {
            throw new ServiceException(Constants.CODE_401, "无权查看该申请的分析");
        }
        return Result.success(adoptAnalysisService.analyze(applicationId));
    }

    /**
     * 宠物照片识别（视觉模型）：根据已上传的头像识别品种并生成建档简介
     */
    @ApiOperation("宠物照片 AI 识别")
    @PostMapping("/pet-profile/recognize")
    public Result recognizePet(@RequestBody Map<String, String> body) {
        assertLogin();
        String imageUrl = body == null ? null : body.get("imageUrl");
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new ServiceException(Constants.CODE_400, "缺少图片地址 imageUrl");
        }
        // 提取文件名（兼容 /file/xxx.png 与完整 http 地址两种形式）
        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        if (fileName.isEmpty() || fileName.contains("..")) {
            throw new ServiceException(Constants.CODE_400, "图片地址不合法");
        }
        java.io.File imageFile = new java.io.File(com.ly.pet.utils.PathUtils.getClassLoadRootPath() + "/files/" + fileName);
        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new ServiceException(Constants.CODE_400, "图片文件不存在");
        }
        // 读取图片；超过 1MB 时压缩为长边 1024px 的 JPEG，避免超过视觉模型体积限制（手机原图通常 5MB+）
        byte[] imageBytes;
        boolean compressed = false;
        try {
            imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "图片读取失败");
        }
        if (imageBytes.length > 1024L * 1024) {
            try {
                java.awt.image.BufferedImage original = javax.imageio.ImageIO.read(imageFile);
                if (original != null) {
                    int w = original.getWidth(), h = original.getHeight();
                    double scale = Math.min(1.0, 1024.0 / Math.max(w, h));
                    int nw = Math.max(1, (int) Math.round(w * scale));
                    int nh = Math.max(1, (int) Math.round(h * scale));
                    java.awt.image.BufferedImage scaled = new java.awt.image.BufferedImage(nw, nh, java.awt.image.BufferedImage.TYPE_INT_RGB);
                    java.awt.Graphics2D g = scaled.createGraphics();
                    g.drawImage(original, 0, 0, nw, nh, java.awt.Color.WHITE, null);
                    g.dispose();
                    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                    javax.imageio.ImageIO.write(scaled, "jpg", baos);
                    imageBytes = baos.toByteArray();
                    compressed = true;
                }
            } catch (Exception ignore) {
                // 压缩失败则按原图继续，由下方体积校验兜底
            }
        }
        if (imageBytes.length > 4L * 1024 * 1024) {
            throw new ServiceException(Constants.CODE_400, "图片过大且压缩失败，请更换 4MB 以内的图片");
        }
        String lower = fileName.toLowerCase();
        String mime = compressed ? "image/jpeg"
                : lower.endsWith(".jpg") || lower.endsWith(".jpeg") ? "image/jpeg"
                : lower.endsWith(".webp") ? "image/webp"
                : lower.endsWith(".gif") ? "image/gif" : "image/png";
        String dataUri = "data:" + mime + ";base64," + java.util.Base64.getEncoder().encodeToString(imageBytes);

        String prompt = "请识别这张照片中的宠物。要求：\n"
                + "1. 若照片中没有明确的动物主体，petType 返回 other，breed 返回空字符串\"\"。\n"
                + "2. 若是动物，必须给出最可能的常见品种中文名，例如：英国短毛猫、美国短毛猫、布偶猫、暹罗猫、中华田园猫、狸花猫、三花猫、橘猫、金毛寻回犬、拉布拉多、柯基、泰迪（贵宾）、比熊、边境牧羊犬、德国牧羊犬、哈士奇、萨摩耶等；本土家猫家犬（中华田园猫/犬、狸花猫、三花猫、橘猫）也是有效答案。严禁返回\"未知\"、\"不知道\"或无法确定的字样，拿不准就给出最接近的常见品种并在结尾加?。\n"
                + "3. 只返回 JSON 对象，不要 markdown 代码块标记和其他文字，格式：\n"
                + "{\"petType\":\"cat/dog/other\",\"breed\":\"品种中文名\",\"color\":\"毛色描述\",\"description\":\"50字以内的宠物建档简介\",\"tags\":\"3-5个形容词标签,逗号分隔\"}";

        String content = glmClientService.recognizeImage(dataUri, prompt);
        // 解析 JSON（容错：剥掉可能的 ```json 包裹）
        String json = content.trim().replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
        int start = json.indexOf('{');
        int end = json.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new ServiceException(Constants.CODE_500, "识别结果解析失败，请手动填写");
        }
        try {
            com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(json.substring(start, end + 1));
            Map<String, Object> result = new java.util.LinkedHashMap<>();
            // petType 规范化为 cat/dog/other（视觉模型可能返回中文）
            String petType = node.path("petType").asText("").toLowerCase();
            if (petType.contains("猫") || petType.contains("cat")) {
                petType = "cat";
            } else if (petType.contains("狗") || petType.contains("犬") || petType.contains("dog")) {
                petType = "dog";
            } else if (!petType.equals("cat") && !petType.equals("dog") && !petType.equals("other")) {
                petType = "other";
            }
            result.put("petType", petType);
            result.put("breed", node.path("breed").asText(""));
            result.put("color", node.path("color").asText(""));
            result.put("description", node.path("description").asText(""));
            result.put("tags", node.path("tags").asText(""));
            return Result.success(result);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "识别结果解析失败，请手动填写");
        }
    }

    @Resource
    private com.ly.pet.service.GlmClient glmClientService;

    private User assertLogin() {
        User user = TokenUtils.getCurrentUser();
        if (user == null) {
            throw new ServiceException(Constants.CODE_401, "请先登录");
        }
        return user;
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null && !user.getRole().equals("ROLE_USER");
    }
}
