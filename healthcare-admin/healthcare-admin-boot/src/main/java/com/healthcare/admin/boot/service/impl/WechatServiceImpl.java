package com.healthcare.admin.boot.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.healthcare.admin.boot.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WechatServiceImpl implements WechatService {

    private static final String WECHAT_API_BASE = "https://qyapi.weixin.qq.com/cgi-bin";
    private static final String ACCESS_TOKEN_KEY = "wechat:access_token";
    private static final long TOKEN_EXPIRE_SECONDS = 7000;

    @Value("${wechat.work.corp-id:}")
    private String corpId;

    @Value("${wechat.work.corp-secret:}")
    private String corpSecret;

    @Value("${wechat.work.agent-id:}")
    private String agentId;

    private final StringRedisTemplate redisTemplate;

    public WechatServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getAccessToken() {
        String token = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
        if (token != null) {
            return token;
        }
        return refreshAccessToken();
    }

    @Override
    public String refreshAccessToken() {
        String url = String.format("%s/gettoken?corpid=%s&corpsecret=%s", 
                WECHAT_API_BASE, corpId, corpSecret);
        
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);
        
        if (json.getInt("errcode") == 0) {
            String accessToken = json.getStr("access_token");
            redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken, 
                    TOKEN_EXPIRE_SECONDS, TimeUnit.SECONDS);
            log.info("AccessToken刷新成功");
            return accessToken;
        } else {
            log.error("AccessToken获取失败: {}", json.getStr("errmsg"));
            throw new RuntimeException("获取AccessToken失败: " + json.getStr("errmsg"));
        }
    }

    @Override
    public Map<String, Object> createGroup(String groupName, List<String> userIds, Long ownerId) {
        String accessToken = getAccessToken();
        String url = String.format("%s/appchat/create?access_token=%s", WECHAT_API_BASE, accessToken);
        
        Map<String, Object> params = new HashMap<>();
        params.put("name", groupName);
        params.put("userlist", userIds);
        params.put("owner", ownerId != null ? String.valueOf(ownerId) : null);
        params.put("chatid", "group_" + System.currentTimeMillis());
        
        String response = HttpUtil.post(url, JSONUtil.toJsonStr(params));
        JSONObject json = JSONUtil.parseObj(response);
        
        if (json.getInt("errcode") == 0) {
            Map<String, Object> result = new HashMap<>();
            result.put("chatId", json.getStr("chatid"));
            result.put("success", true);
            log.info("创建企微群成功: {}", groupName);
            return result;
        } else {
            log.error("创建企微群失败: {}", json.getStr("errmsg"));
            throw new RuntimeException("创建企微群失败: " + json.getStr("errmsg"));
        }
    }

    @Override
    public boolean dismissGroup(String chatId) {
        String accessToken = getAccessToken();
        String url = String.format("%s/appchat/delete?access_token=%s&chatid=%s", 
                WECHAT_API_BASE, accessToken, chatId);
        
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);
        
        if (json.getInt("errcode") == 0) {
            log.info("解散企微群成功: {}", chatId);
            return true;
        } else {
            log.error("解散企微群失败: {}", json.getStr("errmsg"));
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getGroupMembers(String chatId) {
        String accessToken = getAccessToken();
        String url = String.format("%s/appchat/get?access_token=%s&chatid=%s", 
                WECHAT_API_BASE, accessToken, chatId);
        
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);
        
        if (json.getInt("errcode") == 0) {
            JSONObject chatInfo = json.getJSONObject("chat_info");
            List<Map<String, Object>> members = new ArrayList<>();
            for (Object userId : chatInfo.getJSONArray("userlist")) {
                Map<String, Object> member = new HashMap<>();
                member.put("userId", userId);
                members.add(member);
            }
            return members;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean inviteMembers(String chatId, List<String> userIds) {
        String accessToken = getAccessToken();
        String url = String.format("%s/appchat/update?access_token=%s", WECHAT_API_BASE, accessToken);
        
        Map<String, Object> params = new HashMap<>();
        params.put("chatid", chatId);
        params.put("add_user_list", userIds);
        
        String response = HttpUtil.post(url, JSONUtil.toJsonStr(params));
        JSONObject json = JSONUtil.parseObj(response);
        
        return json.getInt("errcode") == 0;
    }

    @Override
    public boolean removeMember(String chatId, String userId) {
        String accessToken = getAccessToken();
        String url = String.format("%s/appchat/update?access_token=%s", WECHAT_API_BASE, accessToken);
        
        Map<String, Object> params = new HashMap<>();
        params.put("chatid", chatId);
        params.put("del_user_list", Collections.singletonList(userId));
        
        String response = HttpUtil.post(url, JSONUtil.toJsonStr(params));
        JSONObject json = JSONUtil.parseObj(response);
        
        return json.getInt("errcode") == 0;
    }

    @Override
    public boolean sendGroupMessage(String chatId, String msgType, Map<String, Object> content) {
        String accessToken = getAccessToken();
        String url = String.format("%s/appchat/send?access_token=%s", WECHAT_API_BASE, accessToken);
        
        Map<String, Object> params = new HashMap<>();
        params.put("chatid", chatId);
        params.put("msgtype", msgType);
        params.put(msgType, content);
        
        String response = HttpUtil.post(url, JSONUtil.toJsonStr(params));
        JSONObject json = JSONUtil.parseObj(response);
        
        if (json.getInt("errcode") == 0) {
            log.info("发送群消息成功: chatId={}, msgType={}", chatId, msgType);
            return true;
        } else {
            log.error("发送群消息失败: {}", json.getStr("errmsg"));
            return false;
        }
    }

    @Override
    public boolean sendAppMessage(String toUser, String msgType, Map<String, Object> content) {
        String accessToken = getAccessToken();
        String url = String.format("%s/message/send?access_token=%s", WECHAT_API_BASE, accessToken);
        
        Map<String, Object> params = new HashMap<>();
        params.put("touser", toUser);
        params.put("msgtype", msgType);
        params.put("agentid", agentId);
        params.put(msgType, content);
        
        String response = HttpUtil.post(url, JSONUtil.toJsonStr(params));
        JSONObject json = JSONUtil.parseObj(response);
        
        return json.getInt("errcode") == 0;
    }

    @Override
    public List<Map<String, Object>> getExternalContacts(String userId) {
        String accessToken = getAccessToken();
        String url = String.format("%s/externalcontact/list?access_token=%s&userid=%s", 
                WECHAT_API_BASE, accessToken, userId);
        
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);
        
        if (json.getInt("errcode") == 0) {
            List<Map<String, Object>> contacts = new ArrayList<>();
            for (Object externalId : json.getJSONArray("external_userid")) {
                Map<String, Object> contact = new HashMap<>();
                contact.put("externalUserId", externalId);
                contacts.add(contact);
            }
            return contacts;
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getExternalContactDetail(String externalUserId) {
        String accessToken = getAccessToken();
        String url = String.format("%s/externalcontact/get?access_token=%s&external_userid=%s", 
                WECHAT_API_BASE, accessToken, externalUserId);
        
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);
        
        if (json.getInt("errcode") == 0) {
            return json.getJSONObject("external_contact").toBean(Map.class);
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean setGroupWelcomeMsg(String chatId, Map<String, Object> welcomeMsg) {
        String accessToken = getAccessToken();
        String url = String.format("%s/externalcontact/group_welcome_template/add?access_token=%s", 
                WECHAT_API_BASE, accessToken);
        
        Map<String, Object> params = new HashMap<>();
        params.put("chat_id", chatId);
        params.putAll(welcomeMsg);
        
        String response = HttpUtil.post(url, JSONUtil.toJsonStr(params));
        JSONObject json = JSONUtil.parseObj(response);
        
        return json.getInt("errcode") == 0;
    }

    @Override
    public String getGroupQrCode(String chatId) {
        String accessToken = getAccessToken();
        String url = String.format("%s/appchat/get?access_token=%s&chatid=%s", 
                WECHAT_API_BASE, accessToken, chatId);
        
        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);
        
        if (json.getInt("errcode") == 0) {
            JSONObject chatInfo = json.getJSONObject("chat_info");
            return chatInfo.getStr("qr_code");
        }
        return null;
    }
}
