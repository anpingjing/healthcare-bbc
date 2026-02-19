package com.healthcare.bbc.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.healthcare.bbc.config.WeChatWorkProperties;
import com.healthcare.bbc.dto.wechat.*;
import com.healthcare.bbc.entity.AiIntent;
import com.healthcare.bbc.entity.CustomerServiceSession;
import com.healthcare.bbc.service.AiDispatchService;
import com.healthcare.bbc.service.CustomerMessageService;
import com.healthcare.bbc.service.CustomerServiceSessionService;
import com.healthcare.bbc.service.WeChatWorkService;
import com.healthcare.bbc.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class WeChatWorkServiceImpl implements WeChatWorkService {

    private final WeChatWorkProperties properties;
    private final RedisUtil redisUtil;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper;
    
    private final ReentrantLock tokenLock = new ReentrantLock();
    
    private static final String ACCESS_TOKEN_KEY = "wechat:access_token";
    private static final String ACCESS_TOKEN_LOCK_KEY = "wechat:access_token_lock";
    private static final int MAX_RETRY_COUNT = 3;
    private static final long TOKEN_REFRESH_AHEAD_SECONDS = 300;

    @Autowired
    private CustomerMessageService customerMessageService;

    @Autowired
    @Lazy
    private AiDispatchService aiDispatchService;

    @Autowired
    private CustomerServiceSessionService sessionService;

    public WeChatWorkServiceImpl(WeChatWorkProperties properties, RedisUtil redisUtil) {
        this.properties = properties;
        this.redisUtil = redisUtil;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.xmlMapper = new XmlMapper();
    }

    @Override
    public String getAccessToken() {
        String cachedToken = (String) redisUtil.get(ACCESS_TOKEN_KEY);
        if (cachedToken != null) {
            return cachedToken;
        }
        
        return refreshAccessTokenWithLock();
    }
    
    @Override
    public void refreshAccessToken() {
        refreshAccessTokenWithLock();
    }
    
    private String refreshAccessTokenWithLock() {
        tokenLock.lock();
        try {
            String cachedToken = (String) redisUtil.get(ACCESS_TOKEN_KEY);
            if (cachedToken != null) {
                return cachedToken;
            }
            
            return fetchAccessTokenWithRetry();
        } finally {
            tokenLock.unlock();
        }
    }
    
    private String fetchAccessTokenWithRetry() {
        Exception lastException = null;
        
        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            try {
                String url = String.format(
                        "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s",
                        properties.getCorpId(),
                        properties.getCorpSecret()
                );
                
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                AccessTokenResult result = objectMapper.readValue(response.getBody(), AccessTokenResult.class);
                
                if (result.getErrcode() != null && result.getErrcode() != 0) {
                    log.error("获取access_token失败: errcode={}, errmsg={}", result.getErrcode(), result.getErrmsg());
                    throw new RuntimeException("获取access_token失败: " + result.getErrmsg());
                }
                
                String accessToken = result.getAccessToken();
                int expiresIn = result.getExpiresIn();
                
                redisUtil.set(ACCESS_TOKEN_KEY, accessToken, expiresIn - TOKEN_REFRESH_AHEAD_SECONDS, TimeUnit.SECONDS);
                log.info("成功获取access_token，有效期: {}秒", expiresIn);
                return accessToken;
                
            } catch (Exception e) {
                lastException = e;
                log.warn("获取access_token失败，第{}次重试", i + 1, e);
                
                if (i < MAX_RETRY_COUNT - 1) {
                    try {
                        Thread.sleep(1000 * (i + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        throw new RuntimeException("获取access_token失败，已重试" + MAX_RETRY_COUNT + "次", lastException);
    }

    @Override
    public boolean verifyUrl(String msgSignature, String timestamp, String nonce, String echostr) {
        try {
            String[] arr = new String[] { properties.getToken(), timestamp, nonce, echostr };
            Arrays.sort(arr);
            
            String sortedString = String.join("", arr);
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] digest = sha1.digest(sortedString.getBytes(StandardCharsets.UTF_8));
            
            String signature = bytesToHex(digest);
            return signature.equals(msgSignature);
        } catch (Exception e) {
            log.error("验证URL签名失败", e);
            return false;
        }
    }

    @Override
    public void handleCallback(String xmlContent) {
        try {
            Map<String, Object> callbackMap = xmlMapper.readValue(xmlContent, new TypeReference<Map<String, Object>>() {});
            log.info("收到企业微信回调: {}", callbackMap);
            
            String msgType = (String) callbackMap.get("MsgType");
            String event = (String) callbackMap.get("Event");
            
            if ("event".equals(msgType)) {
                handleEventCallback(callbackMap);
            } else if ("text".equals(msgType)) {
                handleTextMessage(callbackMap);
            }
        } catch (Exception e) {
            log.error("处理回调异常", e);
        }
    }
    
    @Override
    public void handleEventCallback(Map<String, Object> eventMap) {
        String event = (String) eventMap.get("Event");
        String changeType = (String) eventMap.get("ChangeType");
        
        log.info("处理事件回调: event={}, changeType={}", event, changeType);
        
        if ("change_external_contact".equals(event)) {
            handleExternalContactEvent(eventMap, changeType);
        } else if ("change_external_chat".equals(event)) {
            handleExternalChatEvent(eventMap, changeType);
        }
    }
    
    private void handleExternalContactEvent(Map<String, Object> eventMap, String changeType) {
        String externalUserId = (String) eventMap.get("ExternalUserID");
        String userId = (String) eventMap.get("UserID");
        
        switch (changeType) {
            case "add_external_contact":
                log.info("添加外部联系人: externalUserId={}, userId={}", externalUserId, userId);
                break;
            case "edit_external_contact":
                log.info("编辑外部联系人: externalUserId={}, userId={}", externalUserId, userId);
                break;
            case "del_external_contact":
                log.info("删除外部联系人: externalUserId={}, userId={}", externalUserId, userId);
                break;
            case "del_follow_user":
                log.info("删除跟进人: externalUserId={}, userId={}", externalUserId, userId);
                break;
        }
    }
    
    private void handleExternalChatEvent(Map<String, Object> eventMap, String changeType) {
        String chatId = (String) eventMap.get("ChatId");
        
        switch (changeType) {
            case "create":
                log.info("创建群聊: chatId={}", chatId);
                break;
            case "dismiss":
                log.info("解散群聊: chatId={}", chatId);
                break;
            case "update":
                log.info("更新群聊: chatId={}", chatId);
                break;
        }
    }
    
    private void handleTextMessage(Map<String, Object> messageMap) {
        String fromUser = (String) messageMap.get("FromUserName");
        String content = (String) messageMap.get("Content");
        
        log.info("收到文本消息: fromUser={}, content={}", fromUser, content);
        
        customerMessageService.saveIncomingMessage(fromUser, fromUser, "text", content);
        processMessageWithAI(fromUser, content);
    }

    private void processMessageWithAI(String externalUserId, String content) {
        try {
            if (content.contains("转人工") || content.contains("人工客服")) {
                sessionService.assignAgent(externalUserId, 1L, "客服专员");
                sendTextMessage(externalUserId, "已为您转接人工客服，请稍候...");
                return;
            }

            var session = sessionService.getActiveSession(externalUserId);
            if (session != null) {
                log.info("用户 {} 已有活跃会话，消息转给客服 {}", externalUserId, session.getAgentName());
                return;
            }

            var intent = aiDispatchService.recognizeIntent(content);
            log.info("AI意图识别: externalUserId={}, intentCode={}, targetRole={}",
                    externalUserId, intent.getIntentCode(), intent.getTargetRole());

            if (!"AI".equals(intent.getTargetRole())) {
                aiDispatchService.dispatchToRole(intent, externalUserId, content);
            } else {
                var recentMessages = customerMessageService.getRecentMessages(externalUserId, 5);
                String aiResponse = aiDispatchService.generateAiResponse(externalUserId, content, recentMessages);
                sendTextMessage(externalUserId, aiResponse);
                customerMessageService.saveOutgoingMessage(externalUserId, externalUserId, "text", aiResponse);
            }

        } catch (Exception e) {
            log.error("AI处理消息异常: externalUserId={}, content={}", externalUserId, content, e);
            sendTextMessage(externalUserId, "系统繁忙，请稍后再试或联系人工客服。");
        }
    }

    @Override
    public MessageSendResult sendTextMessage(String userId, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("touser", userId);
        message.put("msgtype", "text");
        message.put("agentid", Integer.parseInt(properties.getAgentId()));
        
        Map<String, String> textContent = new HashMap<>();
        textContent.put("content", content);
        message.put("text", textContent);
        
        return sendMessage(message);
    }
    
    @Override
    public MessageSendResult sendTextMessageToChat(String chatId, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("chatid", chatId);
        message.put("msgtype", "text");
        message.put("agentid", Integer.parseInt(properties.getAgentId()));
        
        Map<String, String> textContent = new HashMap<>();
        textContent.put("content", content);
        message.put("text", textContent);
        
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/appchat/send?access_token=%s", getAccessToken());
        return executePost(url, message);
    }
    
    @Override
    public MessageSendResult sendMarkdownMessage(String userId, String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("touser", userId);
        message.put("msgtype", "markdown");
        message.put("agentid", Integer.parseInt(properties.getAgentId()));
        
        Map<String, String> markdownContent = new HashMap<>();
        markdownContent.put("content", content);
        message.put("markdown", markdownContent);
        
        return sendMessage(message);
    }
    
    @Override
    public MessageSendResult sendTemplateCardMessage(String userId, Map<String, Object> templateCard) {
        Map<String, Object> message = new HashMap<>();
        message.put("touser", userId);
        message.put("msgtype", "template_card");
        message.put("agentid", Integer.parseInt(properties.getAgentId()));
        message.put("template_card", templateCard);
        
        return sendMessage(message);
    }
    
    private MessageSendResult sendMessage(Map<String, Object> message) {
        String url = String.format("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s", getAccessToken());
        return executePost(url, message);
    }
    
    private MessageSendResult executePost(String url, Map<String, Object> body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String jsonBody = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return objectMapper.readValue(response.getBody(), MessageSendResult.class);
        } catch (Exception e) {
            log.error("发送请求失败: url={}", url, e);
            MessageSendResult result = new MessageSendResult();
            result.setErrcode(-1);
            result.setErrmsg("请求失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    public GroupChatListResult getGroupChatList(int limit, String cursor, int statusFilter) {
        String url = String.format(
                "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/list?access_token=%s",
                getAccessToken()
        );
        
        Map<String, Object> body = new HashMap<>();
        body.put("limit", limit);
        if (cursor != null && !cursor.isEmpty()) {
            body.put("cursor", cursor);
        }
        body.put("status_filter", statusFilter);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String jsonBody = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return objectMapper.readValue(response.getBody(), GroupChatListResult.class);
        } catch (Exception e) {
            log.error("获取群聊列表失败", e);
            GroupChatListResult result = new GroupChatListResult();
            result.setErrcode(-1);
            result.setErrmsg("请求失败: " + e.getMessage());
            return result;
        }
    }
    
    @Override
    public GroupChatInfo getGroupChatInfo(String chatId) {
        String url = String.format(
                "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/get?access_token=%s",
                getAccessToken()
        );
        
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String jsonBody = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            
            if (root.has("errcode") && root.get("errcode").asInt() != 0) {
                log.error("获取群聊详情失败: {}", response.getBody());
                return null;
            }
            
            JsonNode groupChatNode = root.get("group_chat");
            return objectMapper.treeToValue(groupChatNode, GroupChatInfo.class);
        } catch (Exception e) {
            log.error("获取群聊详情失败: chatId={}", chatId, e);
            return null;
        }
    }
    
    @Override
    public String createGroupChat(String name, String owner, List<String> userIdList, String chatId) {
        String url = String.format(
                "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/groupchat/create?access_token=%s",
                getAccessToken()
        );
        
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("owner", owner);
        body.put("user_list", userIdList);
        if (chatId != null && !chatId.isEmpty()) {
            body.put("chat_id", chatId);
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String jsonBody = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            
            if (root.has("errcode") && root.get("errcode").asInt() != 0) {
                log.error("创建群聊失败: {}", response.getBody());
                return null;
            }
            
            return root.get("chat_id").asText();
        } catch (Exception e) {
            log.error("创建群聊失败", e);
            return null;
        }
    }

    @Override
    public ExternalContactInfo getExternalContactInfo(String externalUserId) {
        String url = String.format(
                "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/get?access_token=%s&external_userid=%s",
                getAccessToken(),
                externalUserId
        );
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return objectMapper.readValue(response.getBody(), ExternalContactInfo.class);
        } catch (Exception e) {
            log.error("获取外部联系人信息失败: externalUserId={}", externalUserId, e);
            ExternalContactInfo result = new ExternalContactInfo();
            result.setErrcode(-1);
            result.setErrmsg("请求失败: " + e.getMessage());
            return result;
        }
    }
    
    @Override
    public List<ExternalContactInfo> getExternalContactList(String userId) {
        String url = String.format(
                "https://qyapi.weixin.qq.com/cgi-bin/externalcontact/list?access_token=%s&userid=%s",
                getAccessToken(),
                userId
        );
        
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            
            if (root.has("errcode") && root.get("errcode").asInt() != 0) {
                log.error("获取外部联系人列表失败: {}", response.getBody());
                return Collections.emptyList();
            }
            
            JsonNode externalUserIdList = root.get("external_userid");
            List<ExternalContactInfo> result = new ArrayList<>();
            
            if (externalUserIdList != null && externalUserIdList.isArray()) {
                for (JsonNode node : externalUserIdList) {
                    String externalUserId = node.asText();
                    ExternalContactInfo info = getExternalContactInfo(externalUserId);
                    if (info != null && info.getExternalContact() != null) {
                        result.add(info);
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("获取外部联系人列表失败: userId={}", userId, e);
            return Collections.emptyList();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
