package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.HealthBenefit;

import java.util.List;

public interface HealthBenefitService extends IService<HealthBenefit> {
    List<HealthBenefit> getAvailableBenefits();
    List<HealthBenefit> getByBenefitType(String benefitType);
}
