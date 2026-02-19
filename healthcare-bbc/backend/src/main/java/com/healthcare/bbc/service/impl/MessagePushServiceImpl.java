package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.bbc.config.WeChatWorkProperties;
import com.healthcare.bbc.dto.MessagePushDTO;
import com.healthcare.bbc.entity.User;
import com.healthcare.bbc.service.MessagePushService;
import com.healthcare.bbc.service.UserService;
import com.healthcare.bbc.service.WeChatWorkService;
import com.healthcare.bbc.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class MessagePushServiceImpl implements MessagePushService {

    private final WeChatWorkService weChatWorkService;
    private final UserService userService;
    private final WeChatWorkProperties properties;
    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String ACCESS_TOKEN_KEY = "wechat:access_token";

    public MessagePushServiceImpl(WeChatWorkService weChatWorkService, UserService userService,
                               WeChatWorkProperties properties, RedisUtil redisUtil,
                               RestTemplate restTemplate) {
        this.weChatWorkService = weChatWorkService;
        this.userService = userService;
        this.properties = properties;
        this.redisUtil = redisUtil;
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void pushTextMessage(Long userId, String content) {
        User user = userService.getById(userId);
        if (user == null) {
            log.warn("用户不存在: {}", userId);
            return;
        }
        weChatWorkService.sendTextMessage(user.getWechatId(), content);
        log.info("发送文本消息成功: userId={}, content={}", userId, content);
    }

    @Override
    public void pushNewsMessage(Long userId, String title, String description, String url, String picUrl) {
        User user = userService.getById(userId);
        if (user == null) {
            log.warn("用户不存在: {}", userId);
            return;
        }

        String accessToken = weChatWorkService.getAccessToken();
        String apiUrl = String.format(
                "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s",
                accessToken
        );

        try {
            String requestBody = String.format(
                    "{\"touser\":\"%s\",\"msgtype\":\"news\",\"agentid\":%s,\"news\":{\"articles\":[{\"title\":\"%s\",\"description\":\"%s\",\"url\":\"%s\",\"picurl\":\"%s\"}]}}",
                    user.getWechatId(),
                    properties.getAgentId(),
                    title,
                    description,
                    url,
                    picUrl
            );

            String response = restTemplate.postForObject(apiUrl, requestBody, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                log.error("发送图文消息失败: {}", response);
                throw new RuntimeException("发送图文消息失败");
            }

            log.info("发送图文消息成功: userId={}, title={}", userId, title);
        } catch (Exception e) {
            log.error("发送图文消息异常", e);
            throw new RuntimeException("发送图文消息异常", e);
        }
    }

    @Override
    public void pushTaskReminder(Long userId, String taskName, String taskTime) {
        String content = String.format("【任务提醒】您有一个健康任务待完成：\n任务名称：%s\n任务时间：%s\n请及时完成！", taskName, taskTime);
        pushTextMessage(userId, content);
    }

    @Override
    public void pushHealthAlert(Long userId, String alertType, String alertMessage) {
        String content = String.format("【健康预警】%s\n%s\n建议您及时关注并采取相应措施。", alertType, alertMessage);
        pushTextMessage(userId, content);
    }

    @Override
    public void pushInsuranceRecommendation(Long userId, String productName, String productDescription) {
        String content = String.format("【保险推荐】为您推荐以下保险产品：\n产品名称：%s\n产品介绍：%s\n如需了解更多详情，请联系您的专属保险顾问。", productName, productDescription);
        pushTextMessage(userId, content);
    }

    @Override
    public void pushMessage(MessagePushDTO messagePushDTO) {
        switch (messagePushDTO.getMessageType()) {
            case TEXT:
                pushTextMessage(messagePushDTO.getUserId(), messagePushDTO.getContent());
                break;
            case NEWS:
                pushNewsMessage(messagePushDTO.getUserId(), messagePushDTO.getTitle(),
                        messagePushDTO.getContent(), messagePushDTO.getUrl(), messagePushDTO.getPicUrl());
                break;
            case TASK_REMINDER:
                pushTaskReminder(messagePushDTO.getUserId(), messagePushDTO.getTitle(), messagePushDTO.getContent());
                break;
            case HEALTH_ALERT:
                pushHealthAlert(messagePushDTO.getUserId(), messagePushDTO.getTitle(), messagePushDTO.getContent());
                break;
            case INSURANCE_RECOMMENDATION:
                pushInsuranceRecommendation(messagePushDTO.getUserId(), messagePushDTO.getTitle(), messagePushDTO.getContent());
                break;
            default:
                pushTextMessage(messagePushDTO.getUserId(), messagePushDTO.getContent());
        }
    }
}
