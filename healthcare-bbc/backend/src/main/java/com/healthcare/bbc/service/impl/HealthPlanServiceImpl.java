package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.entity.HealthPlan;
import com.healthcare.bbc.mapper.HealthPlanMapper;
import com.healthcare.bbc.service.HealthPlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthPlanServiceImpl extends ServiceImpl<HealthPlanMapper, HealthPlan> implements HealthPlanService {
    @Override
    public List<HealthPlan> getByUserId(Long userId) {
        LambdaQueryWrapper<HealthPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPlan::getUserId, userId);
        wrapper.orderByDesc(HealthPlan::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<HealthPlan> getByUserIdAndStatus(Long userId, Integer status) {
        LambdaQueryWrapper<HealthPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthPlan::getUserId, userId);
        wrapper.eq(HealthPlan::getStatus, status);
        wrapper.orderByDesc(HealthPlan::getCreateTime);
        return list(wrapper);
    }
}
