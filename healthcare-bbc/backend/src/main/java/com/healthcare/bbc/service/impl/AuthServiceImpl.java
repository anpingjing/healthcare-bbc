package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.healthcare.bbc.config.JwtUtil;
import com.healthcare.bbc.dto.LoginDTO;
import com.healthcare.bbc.entity.User;
import com.healthcare.bbc.service.AuthService;
import com.healthcare.bbc.service.UserService;
import com.healthcare.bbc.util.RedisUtil;
import com.healthcare.bbc.vo.LoginVO;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public AuthServiceImpl(UserService userService, JwtUtil jwtUtil, RedisUtil redisUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.redisUtil = redisUtil;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getWechatId, loginDTO.getWechatId());
        User user = userService.getOne(wrapper);

        if (user == null) {
            user = new User();
            user.setWechatId(loginDTO.getWechatId());
            user.setName("用户" + loginDTO.getWechatId().substring(0, 6));
            user.setStatus(1);
            userService.save(user);
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getWechatId());
        String redisKey = "token:" + user.getUserId();
        redisUtil.set(redisKey, token, 24, TimeUnit.HOURS);

        return new LoginVO(token, user.getUserId(), user.getWechatId(), user.getName());
    }

    @Override
    public void logout(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        String redisKey = "token:" + userId;
        redisUtil.delete(redisKey);
    }
}
