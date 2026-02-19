package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.UserBenefitRel;

import java.util.List;

public interface UserBenefitRelService extends IService<UserBenefitRel> {
    List<UserBenefitRel> getByUserId(Long userId);
    List<UserBenefitRel> getByUserIdAndStatus(Long userId, Integer status);
}
