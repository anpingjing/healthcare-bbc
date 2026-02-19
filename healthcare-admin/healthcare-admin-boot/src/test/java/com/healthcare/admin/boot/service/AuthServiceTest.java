package com.healthcare.admin.boot.service;

import cn.hutool.crypto.digest.BCrypt;
import com.healthcare.admin.boot.dto.LoginDTO;
import com.healthcare.admin.boot.entity.SysUser;
import com.healthcare.admin.boot.mapper.SysUserMapper;
import com.healthcare.admin.boot.service.impl.AuthServiceImpl;
import com.healthcare.admin.boot.vo.LoginVO;
import com.healthcare.admin.common.exception.BusinessException;
import com.healthcare.admin.common.result.ResultCode;
import com.healthcare.admin.common.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthServiceImpl authService;

    private SysUser testUser;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        // 准备测试用户
        testUser = new SysUser();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(BCrypt.hashpw("password123", BCrypt.gensalt()));
        testUser.setRealName("测试用户");
        testUser.setStatus(1);

        // 准备登录DTO
        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        // 配置Redis
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testLoginSuccess() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(jwtUtil.generateAccessToken(anyLong(), anyString())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(anyLong(), anyString())).thenReturn("refresh_token");
        when(jwtUtil.getAccessTokenExpiration()).thenReturn(7200000L);

        // When
        LoginVO result = authService.login(loginDTO);

        // Then
        assertNotNull(result);
        assertEquals("access_token", result.getAccessToken());
        assertEquals("refresh_token", result.getRefreshToken());
        assertEquals("Bearer", result.getTokenType());
        assertNotNull(result.getUser());
        assertEquals("testuser", result.getUser().getUsername());
        assertEquals("测试用户", result.getUser().getRealName());

        // Verify
        verify(userMapper).selectByUsername("testuser");
        verify(jwtUtil).generateAccessToken(1L, "testuser");
        verify(jwtUtil).generateRefreshToken(1L, "testuser");
    }

    @Test
    void testLoginUserNotExist() {
        // Given
        when(userMapper.selectByUsername("testuser")).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals(ResultCode.USER_NOT_EXIST.getCode(), exception.getCode());
    }

    @Test
    void testLoginPasswordError() {
        // Given
        loginDTO.setPassword("wrongpassword");
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals(ResultCode.USER_PASSWORD_ERROR.getCode(), exception.getCode());
    }

    @Test
    void testLoginUserDisabled() {
        // Given
        testUser.setStatus(0);
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO);
        });

        assertEquals(ResultCode.USER_DISABLED.getCode(), exception.getCode());
    }

    @Test
    void testLogout() {
        // Given
        String token = "test_token";
        when(jwtUtil.getUserIdFromToken(token)).thenReturn(1L);

        // When
        authService.logout(token);

        // Then
        verify(redisTemplate).opsForValue();
        verify(redisTemplate).delete(anyString());
    }

    @Test
    void testRefreshTokenSuccess() {
        // Given
        String refreshToken = "valid_refresh_token";
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(refreshToken)).thenReturn(1L);
        when(jwtUtil.getUsernameFromToken(refreshToken)).thenReturn("testuser");
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(jwtUtil.generateAccessToken(anyLong(), anyString())).thenReturn("new_access_token");
        when(jwtUtil.generateRefreshToken(anyLong(), anyString())).thenReturn("new_refresh_token");
        when(jwtUtil.getAccessTokenExpiration()).thenReturn(7200000L);
        when(jwtUtil.getRefreshTokenExpiration()).thenReturn(604800000L);

        // When
        LoginVO result = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(result);
        assertEquals("new_access_token", result.getAccessToken());
        assertEquals("new_refresh_token", result.getRefreshToken());
    }

    @Test
    void testRefreshTokenInvalid() {
        // Given
        String refreshToken = "invalid_token";
        when(jwtUtil.validateRefreshToken(refreshToken)).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.refreshToken(refreshToken);
        });

        assertEquals(ResultCode.TOKEN_INVALID.getCode(), exception.getCode());
    }
}
