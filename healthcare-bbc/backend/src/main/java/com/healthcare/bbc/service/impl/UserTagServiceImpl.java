package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.entity.UserTag;
import com.healthcare.bbc.mapper.UserTagMapper;
import com.healthcare.bbc.service.UserTagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTagServiceImpl extends ServiceImpl<UserTagMapper, UserTag> implements UserTagService {
    @Override
    public List<UserTag> getByUserId(Long userId) {
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, userId);
        wrapper.orderByDesc(UserTag::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<UserTag> getByUserIdAndCategory(Long userId, String tagCategory) {
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, userId);
        wrapper.eq(UserTag::getTagCategory, tagCategory);
        wrapper.orderByDesc(UserTag::getCreateTime);
        return list(wrapper);
    }

    @Override
    public boolean removeByUserId(Long userId) {
        LambdaQueryWrapper<UserTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTag::getUserId, userId);
        return remove(wrapper);
    }
}
