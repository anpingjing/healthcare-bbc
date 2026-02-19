package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.entity.HealthBenefit;
import com.healthcare.bbc.mapper.HealthBenefitMapper;
import com.healthcare.bbc.service.HealthBenefitService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthBenefitServiceImpl extends ServiceImpl<HealthBenefitMapper, HealthBenefit> implements HealthBenefitService {
    @Override
    public List<HealthBenefit> getAvailableBenefits() {
        LambdaQueryWrapper<HealthBenefit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthBenefit::getStatus, 1);
        wrapper.orderByDesc(HealthBenefit::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<HealthBenefit> getByBenefitType(String benefitType) {
        LambdaQueryWrapper<HealthBenefit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthBenefit::getBenefitType, benefitType);
        wrapper.eq(HealthBenefit::getStatus, 1);
        wrapper.orderByDesc(HealthBenefit::getCreateTime);
        return list(wrapper);
    }
}
