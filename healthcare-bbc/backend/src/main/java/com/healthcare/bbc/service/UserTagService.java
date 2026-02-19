package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.UserTag;

import java.util.List;

public interface UserTagService extends IService<UserTag> {
    List<UserTag> getByUserId(Long userId);
    List<UserTag> getByUserIdAndCategory(Long userId, String tagCategory);
    boolean removeByUserId(Long userId);
}
