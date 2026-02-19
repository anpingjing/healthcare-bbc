package com.healthcare.admin.boot.service;

import com.healthcare.admin.boot.dto.LoginDTO;
import com.healthcare.admin.boot.vo.LoginVO;

public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 刷新令牌
     */
    LoginVO refreshToken(String refreshToken);
}
