package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.HealthPlan;

import java.util.List;

public interface HealthPlanService extends IService<HealthPlan> {
    List<HealthPlan> getByUserId(Long userId);
    List<HealthPlan> getByUserIdAndStatus(Long userId, Integer status);
}
