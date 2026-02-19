package com.healthcare.bbc.service.impl;

import com.healthcare.bbc.entity.AiIntent;
import com.healthcare.bbc.entity.CustomerMessage;
import com.healthcare.bbc.mapper.AiIntentMapper;
import com.healthcare.bbc.service.AiDispatchService;
import com.healthcare.bbc.service.CustomerMessageService;
import com.healthcare.bbc.service.WeChatWorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AiDispatchServiceImpl implements AiDispatchService {

    @Autowired
    private AiIntentMapper aiIntentMapper;

    @Autowired
    private CustomerMessageService customerMessageService;

    @Autowired
    private WeChatWorkService weChatWorkService;

    private static final Map<String, String> ROLE_RESPONSES = new HashMap<>();

    static {
        ROLE_RESPONSES.put("DOCTOR", "您的健康咨询已转接给专业医生，请稍候...");
        ROLE_RESPONSES.put("ADVISOR", "您的保险咨询已转接给保险顾问，请稍候...");
        ROLE_RESPONSES.put("MANAGER", "您的权益咨询已转接给健康管家，请稍候...");
        ROLE_RESPONSES.put("AI", "我是您的AI健康助手，请问有什么可以帮助您？");
    }

    @Override
    public AiIntent recognizeIntent(String messageContent) {
        List<AiIntent> intents = aiIntentMapper.selectActiveIntents();

        for (AiIntent intent : intents) {
            if (matchesIntent(messageContent, intent)) {
                log.info("意图识别成功: content={}, intentCode={}, targetRole={}",
                        messageContent, intent.getIntentCode(), intent.getTargetRole());
                return intent;
            }
        }

        AiIntent defaultIntent = new AiIntent();
        defaultIntent.setIntentCode("GENERAL");
        defaultIntent.setIntentName("通用咨询");
        defaultIntent.setTargetRole("AI");
        return defaultIntent;
    }

    private boolean matchesIntent(String messageContent, AiIntent intent) {
        if (intent.getKeywords() == null || intent.getKeywords().isEmpty()) {
            return false;
        }

        String[] keywords = intent.getKeywords().split(",");
        String lowerContent = messageContent.toLowerCase();

        for (String keyword : keywords) {
            if (lowerContent.contains(keyword.trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String dispatchToRole(AiIntent intent, String externalUserId, String messageContent) {
        String targetRole = intent.getTargetRole();
        String response = ROLE_RESPONSES.getOrDefault(targetRole, "已收到您的消息，正在处理中...");

        weChatWorkService.sendTextMessage(externalUserId, response);

        log.info("消息调度: externalUserId={}, targetRole={}, intentCode={}",
                externalUserId, targetRole, intent.getIntentCode());

        return targetRole;
    }

    @Override
    public String generateAiResponse(String externalUserId, String messageContent, List<CustomerMessage> context) {
        String lowerContent = messageContent.toLowerCase();

        if (lowerContent.contains("你好") || lowerContent.contains("您好") || lowerContent.contains("hi")) {
            return "您好！我是您的AI健康助手，可以为您提供健康咨询、保险推荐等服务。请问有什么可以帮助您？";
        }

        if (lowerContent.contains("血压") || lowerContent.contains("血糖")) {
            return "关于血压/血糖管理，建议您：\n1. 定期监测并记录数据\n2. 保持健康饮食，少盐少糖\n3. 适量运动\n4. 遵医嘱服药\n如需更详细的建议，我可以为您转接专业医生。";
        }

        if (lowerContent.contains("保险") || lowerContent.contains("保单")) {
            return "关于保险咨询，我可以帮您：\n1. 分析保障缺口\n2. 推荐合适的产品\n3. 解答理赔问题\n如需人工服务，我可以为您转接保险顾问。";
        }

        if (lowerContent.contains("权益") || lowerContent.contains("体检") || lowerContent.contains("绿通")) {
            return "关于健康权益，您可以：\n1. 查看您的权益列表\n2. 预约体检服务\n3. 申请专家绿通\n如需帮助，我可以为您转接健康管家。";
        }

        return "感谢您的咨询！我已记录您的问题，会尽快为您处理。如需人工服务，请回复\"转人工\"。";
    }

    @Override
    public Map<String, Object> analyzeMessage(String messageContent) {
        Map<String, Object> analysis = new HashMap<>();

        AiIntent intent = recognizeIntent(messageContent);
        analysis.put("intent", intent);
        analysis.put("sentiment", analyzeSentiment(messageContent));
        analysis.put("urgency", analyzeUrgency(messageContent));
        analysis.put("keywords", extractKeywords(messageContent));

        return analysis;
    }

    private String analyzeSentiment(String messageContent) {
        String lowerContent = messageContent.toLowerCase();
        if (lowerContent.contains("急") || lowerContent.contains("严重") || lowerContent.contains("痛")) {
            return "NEGATIVE";
        }
        if (lowerContent.contains("谢谢") || lowerContent.contains("好") || lowerContent.contains("满意")) {
            return "POSITIVE";
        }
        return "NEUTRAL";
    }

    private String analyzeUrgency(String messageContent) {
        String lowerContent = messageContent.toLowerCase();
        if (lowerContent.contains("紧急") || lowerContent.contains("马上") || lowerContent.contains("立刻")) {
            return "HIGH";
        }
        if (lowerContent.contains("尽快") || lowerContent.contains("今天")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private List<String> extractKeywords(String messageContent) {
        List<String> keywords = new ArrayList<>();
        String[] commonKeywords = {"血压", "血糖", "保险", "体检", "医生", "药品", "症状", "治疗"};

        for (String keyword : commonKeywords) {
            if (messageContent.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        return keywords;
    }

    @Override
    public void learnFromFeedback(Long messageId, boolean isHelpful, String correction) {
        log.info("AI学习反馈: messageId={}, isHelpful={}, correction={}", messageId, isHelpful, correction);
    }
}
