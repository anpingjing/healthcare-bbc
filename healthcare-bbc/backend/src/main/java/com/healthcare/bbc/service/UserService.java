package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.User;

public interface UserService extends IService<User> {
    User getByWechatId(String wechatId);
}
