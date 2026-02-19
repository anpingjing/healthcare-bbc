package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.annotation.CacheEvict;
import com.healthcare.bbc.annotation.Cacheable;
import com.healthcare.bbc.entity.User;
import com.healthcare.bbc.mapper.UserMapper;
import com.healthcare.bbc.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    @Cacheable(key = "user:wechatId:#wechatId", expire = 60, timeUnit = java.util.concurrent.TimeUnit.MINUTES)
    public User getByWechatId(String wechatId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getWechatId, wechatId);
        return getOne(wrapper);
    }
}
