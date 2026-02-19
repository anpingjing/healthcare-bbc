package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.InsurancePolicy;

import java.util.List;

public interface InsurancePolicyService extends IService<InsurancePolicy> {
    List<InsurancePolicy> getByUserId(Long userId);
    List<InsurancePolicy> getByUserIdAndStatus(Long userId, Integer status);
}
