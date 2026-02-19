package com.healthcare.admin.boot.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.healthcare.admin.boot.dto.LoginDTO;
import com.healthcare.admin.boot.entity.SysUser;
import com.healthcare.admin.boot.mapper.SysUserMapper;
import com.healthcare.admin.boot.service.AuthService;
import com.healthcare.admin.boot.vo.LoginVO;
import com.healthcare.admin.common.exception.BusinessException;
import com.healthcare.admin.common.result.ResultCode;
import com.healthcare.admin.common.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String USER_TOKEN_PREFIX = "user:token:";

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 1. 查询用户
        SysUser user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 2. 验证密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        if (user.getStatus() == 2) {
            throw new BusinessException(ResultCode.USER_LOCKED);
        }

        // 4. 生成Token
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getUsername());

        // 5. 存储用户Token到Redis
        String userTokenKey = USER_TOKEN_PREFIX + user.getUserId();
        redisTemplate.opsForValue().set(userTokenKey, accessToken, 
                jwtUtil.getAccessTokenExpiration(), TimeUnit.MILLISECONDS);

        // 6. 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(jwtUtil.getAccessTokenExpiration() / 1000);
        loginVO.setTokenType("Bearer");

        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        // TODO: 查询用户角色和权限
        userInfo.setRoles(Arrays.asList("ROLE_ADMIN"));
        userInfo.setPermissions(Arrays.asList("*:*:*"));
        loginVO.setUser(userInfo);

        log.info("用户登录成功: {}", user.getUsername());
        return loginVO;
    }

    @Override
    public void logout(String token) {
        // 将Token加入黑名单
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(blacklistKey, "1", 
                jwtUtil.getAccessTokenExpiration(), TimeUnit.MILLISECONDS);
        
        // 清除用户Token
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            redisTemplate.delete(USER_TOKEN_PREFIX + userId);
        }
        
        log.info("用户登出成功: {}", userId);
    }

    @Override
    public LoginVO refreshToken(String refreshToken) {
        // 1. 验证刷新令牌
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 2. 获取用户信息
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        // 3. 查询用户
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }

        // 4. 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 5. 生成新的Token
        String newAccessToken = jwtUtil.generateAccessToken(userId, username);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);

        // 6. 更新Redis中的Token
        String userTokenKey = USER_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(userTokenKey, newAccessToken, 
                jwtUtil.getAccessTokenExpiration(), TimeUnit.MILLISECONDS);

        // 7. 将旧Token加入黑名单
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(blacklistKey, "1", 
                jwtUtil.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

        // 8. 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(newAccessToken);
        loginVO.setRefreshToken(newRefreshToken);
        loginVO.setExpiresIn(jwtUtil.getAccessTokenExpiration() / 1000);
        loginVO.setTokenType("Bearer");

        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoles(Arrays.asList("ROLE_ADMIN"));
        userInfo.setPermissions(Arrays.asList("*:*:*"));
        loginVO.setUser(userInfo);

        log.info("Token刷新成功: {}", username);
        return loginVO;
    }
}
