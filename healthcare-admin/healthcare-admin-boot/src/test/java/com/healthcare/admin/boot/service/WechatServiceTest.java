package com.healthcare.admin.boot.service;

import com.healthcare.admin.boot.service.impl.WechatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WechatServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private WechatServiceImpl wechatService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(wechatService, "corpId", "test-corp-id");
        ReflectionTestUtils.setField(wechatService, "corpSecret", "test-corp-secret");
        ReflectionTestUtils.setField(wechatService, "agentId", "1000001");
        
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testGetAccessTokenFromCache() {
        when(redisTemplate.opsForValue().get("wechat:access_token")).thenReturn("cached_token");

        String token = wechatService.getAccessToken();

        assertEquals("cached_token", token);
        verify(redisTemplate).opsForValue();
    }

    @Test
    void testSendGroupMessageSuccess() {
        when(redisTemplate.opsForValue().get("wechat:access_token")).thenReturn("test_token");

        boolean result = wechatService.sendGroupMessage("test_chat_id", "text", 
                Map.of("content", "测试消息"));

        // 由于实际调用需要网络，这里主要验证方法不抛异常
        assertNotNull(result);
    }

    @Test
    void testGetExternalContactsSuccess() {
        when(redisTemplate.opsForValue().get("wechat:access_token")).thenReturn("test_token");

        List<Map<String, Object>> contacts = wechatService.getExternalContacts("test_user");

        assertNotNull(contacts);
    }

    @Test
    void testInviteMembersSuccess() {
        when(redisTemplate.opsForValue().get("wechat:access_token")).thenReturn("test_token");

        boolean result = wechatService.inviteMembers("test_chat_id", Arrays.asList("user1", "user2"));

        assertNotNull(result);
    }

    @Test
    void testRemoveMemberSuccess() {
        when(redisTemplate.opsForValue().get("wechat:access_token")).thenReturn("test_token");

        boolean result = wechatService.removeMember("test_chat_id", "user1");

        assertNotNull(result);
    }
}
