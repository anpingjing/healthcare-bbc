package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.entity.UserBenefitRel;
import com.healthcare.bbc.mapper.UserBenefitRelMapper;
import com.healthcare.bbc.service.UserBenefitRelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBenefitRelServiceImpl extends ServiceImpl<UserBenefitRelMapper, UserBenefitRel> implements UserBenefitRelService {
    @Override
    public List<UserBenefitRel> getByUserId(Long userId) {
        LambdaQueryWrapper<UserBenefitRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBenefitRel::getUserId, userId);
        wrapper.orderByDesc(UserBenefitRel::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<UserBenefitRel> getByUserIdAndStatus(Long userId, Integer status) {
        LambdaQueryWrapper<UserBenefitRel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBenefitRel::getUserId, userId);
        wrapper.eq(UserBenefitRel::getStatus, status);
        wrapper.orderByDesc(UserBenefitRel::getCreateTime);
        return list(wrapper);
    }
}
