package com.healthcare.admin.boot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.admin.boot.dto.LoginDTO;
import com.healthcare.admin.boot.service.AuthService;
import com.healthcare.admin.boot.vo.LoginVO;
import com.healthcare.admin.common.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginDTO loginDTO;
    private LoginVO loginVO;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setUsername("admin");
        loginDTO.setPassword("admin123");

        loginVO = new LoginVO();
        loginVO.setAccessToken("test_access_token");
        loginVO.setRefreshToken("test_refresh_token");
        loginVO.setExpiresIn(7200L);
        loginVO.setTokenType("Bearer");

        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setUserId(1L);
        userInfo.setUsername("admin");
        userInfo.setRealName("系统管理员");
        loginVO.setUser(userInfo);
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Given
        when(authService.login(any(LoginDTO.class))).thenReturn(loginVO);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.accessToken").value("test_access_token"))
                .andExpect(jsonPath("$.data.user.username").value("admin"));
    }

    @Test
    void testLoginValidationError() throws Exception {
        // Given
        LoginDTO invalidDTO = new LoginDTO();
        invalidDTO.setUsername(""); // 空用户名
        invalidDTO.setPassword("password");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1001));
    }

    @Test
    void testRefreshToken() throws Exception {
        // Given
        String refreshToken = "valid_refresh_token";
        when(authService.refreshToken(refreshToken)).thenReturn(loginVO);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/refresh")
                .param("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").value("test_access_token"));
    }
}
