package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.entity.InsurancePolicy;
import com.healthcare.bbc.mapper.InsurancePolicyMapper;
import com.healthcare.bbc.service.InsurancePolicyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsurancePolicyServiceImpl extends ServiceImpl<InsurancePolicyMapper, InsurancePolicy> implements InsurancePolicyService {
    @Override
    public List<InsurancePolicy> getByUserId(Long userId) {
        LambdaQueryWrapper<InsurancePolicy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InsurancePolicy::getUserId, userId);
        wrapper.orderByDesc(InsurancePolicy::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<InsurancePolicy> getByUserIdAndStatus(Long userId, Integer status) {
        LambdaQueryWrapper<InsurancePolicy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InsurancePolicy::getUserId, userId);
        wrapper.eq(InsurancePolicy::getStatus, status);
        wrapper.orderByDesc(InsurancePolicy::getCreateTime);
        return list(wrapper);
    }
}
