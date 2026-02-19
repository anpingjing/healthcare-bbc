package com.healthcare.admin.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.admin.boot.dto.HealthProfileDTO;
import com.healthcare.admin.boot.entity.HealthProfile;
import com.healthcare.admin.boot.mapper.HealthProfileMapper;
import com.healthcare.admin.boot.service.HealthProfileService;
import com.healthcare.admin.boot.vo.HealthProfileVO;
import com.healthcare.admin.common.exception.BusinessException;
import com.healthcare.admin.common.result.PageResult;
import com.healthcare.admin.common.result.ResultCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HealthProfileServiceImpl implements HealthProfileService {

    @Autowired
    private HealthProfileMapper healthProfileMapper;

    @Override
    public PageResult<HealthProfileVO> getProfileList(Integer pageNum, Integer pageSize, String keyword, Integer healthLevel, Long doctorId) {
        Page<HealthProfile> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<HealthProfile> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(HealthProfile::getRealName, keyword)
                   .or()
                   .like(HealthProfile::getPhone, keyword);
        }
        
        if (healthLevel != null) {
            wrapper.eq(HealthProfile::getHealthLevel, healthLevel);
        }
        
        if (doctorId != null) {
            wrapper.eq(HealthProfile::getDoctorId, doctorId);
        }
        
        wrapper.eq(HealthProfile::getDeleted, 0)
               .orderByDesc(HealthProfile::getCreateTime);
        
        Page<HealthProfile> profilePage = healthProfileMapper.selectPage(page, wrapper);
        
        List<HealthProfileVO> voList = profilePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageResult.of(voList, profilePage.getTotal(), (long) pageNum, (long) pageSize);
    }

    @Override
    public HealthProfileVO getProfileById(Long profileId) {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null || profile.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "健康档案不存在");
        }
        return convertToVO(profile);
    }

    @Override
    public HealthProfileVO getProfileByUserId(Long userId) {
        HealthProfile profile = healthProfileMapper.selectByUserId(userId);
        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "健康档案不存在");
        }
        return convertToVO(profile);
    }

    @Override
    @Transactional
    public HealthProfileVO createProfile(HealthProfileDTO dto) {
        HealthProfile profile = new HealthProfile();
        BeanUtils.copyProperties(dto, profile);
        
        // 计算BMI
        if (profile.getHeight() != null && profile.getWeight() != null) {
            BigDecimal bmi = profile.getWeight().divide(
                    profile.getHeight().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).pow(2),
                    2, RoundingMode.HALF_UP);
            profile.setBmi(bmi);
        }
        
        // 初始健康评分
        profile.setHealthScore(calculateInitialScore(profile));
        profile.setHealthLevel("low");
        profile.setStatus(1);
        
        healthProfileMapper.insert(profile);
        return convertToVO(profile);
    }

    @Override
    @Transactional
    public HealthProfileVO updateProfile(Long profileId, HealthProfileDTO dto) {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null || profile.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "健康档案不存在");
        }
        
        BeanUtils.copyProperties(dto, profile);
        profile.setProfileId(profileId);
        
        // 重新计算BMI
        if (profile.getHeight() != null && profile.getWeight() != null) {
            BigDecimal bmi = profile.getWeight().divide(
                    profile.getHeight().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).pow(2),
                    2, RoundingMode.HALF_UP);
            profile.setBmi(bmi);
        }
        
        healthProfileMapper.updateById(profile);
        return convertToVO(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(Long profileId) {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null || profile.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "健康档案不存在");
        }
        healthProfileMapper.deleteById(profileId);
    }

    @Override
    public Integer calculateHealthScore(Long profileId) {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "健康档案不存在");
        }
        
        int score = 100;
        
        // BMI评分
        if (profile.getBmi() != null) {
            double bmi = profile.getBmi().doubleValue();
            if (bmi < 18.5 || bmi >= 28) {
                score -= 15;
            } else if (bmi < 24) {
                score -= 0;
            } else {
                score -= 10;
            }
        }
        
        // 慢性病评分
        if (StringUtils.hasText(profile.getChronicDiseases())) {
            int diseaseCount = profile.getChronicDiseases().split(",").length;
            score -= diseaseCount * 10;
        }
        
        // 过敏史评分
        if (StringUtils.hasText(profile.getAllergies())) {
            score -= 5;
        }
        
        // 家族病史评分
        if (StringUtils.hasText(profile.getFamilyHistory())) {
            score -= 5;
        }
        
        score = Math.max(0, Math.min(100, score));
        
        // 更新评分
        profile.setHealthScore(score);
        profile.setHealthLevel(getHealthLevel(score));
        healthProfileMapper.updateById(profile);
        
        return score;
    }

    @Override
    @Transactional
    public void updateHealthLevel(Long profileId, String healthLevel) {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "健康档案不存在");
        }
        profile.setHealthLevel(healthLevel);
        healthProfileMapper.updateById(profile);
    }

    @Override
    @Transactional
    public void assignServiceRoles(Long profileId, Long doctorId, Long healthManagerId, Long benefitAdvisorId, Long insuranceAdvisorId) {
        HealthProfile profile = healthProfileMapper.selectById(profileId);
        if (profile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "健康档案不存在");
        }
        
        profile.setDoctorId(doctorId);
        profile.setHealthManagerId(healthManagerId);
        profile.setBenefitAdvisorId(benefitAdvisorId);
        profile.setInsuranceAdvisorId(insuranceAdvisorId);
        
        healthProfileMapper.updateById(profile);
    }

    @Override
    public Map<String, Object> getHealthStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总档案数
        Long total = healthProfileMapper.selectCount(
            new LambdaQueryWrapper<HealthProfile>().eq(HealthProfile::getDeleted, 0)
        );
        statistics.put("total", total);
        
        // 各健康等级人数
        List<Map<String, Object>> levelCounts = healthProfileMapper.countByHealthLevel();
        Map<String, Long> levelMap = new HashMap<>();
        for (Map<String, Object> item : levelCounts) {
            levelMap.put((String) item.get("health_level"), (Long) item.get("count"));
        }
        statistics.put("levelDistribution", levelMap);
        
        // 平均健康评分
        // TODO: 实现平均分计算
        
        return statistics;
    }

    private HealthProfileVO convertToVO(HealthProfile profile) {
        HealthProfileVO vo = new HealthProfileVO();
        BeanUtils.copyProperties(profile, vo);
        return vo;
    }

    private Integer calculateInitialScore(HealthProfile profile) {
        int score = 100;
        
        if (profile.getBmi() != null) {
            double bmi = profile.getBmi().doubleValue();
            if (bmi >= 18.5 && bmi < 24) {
                score -= 0;
            } else if (bmi < 18.5 || bmi >= 28) {
                score -= 15;
            } else {
                score -= 10;
            }
        }
        
        if (StringUtils.hasText(profile.getChronicDiseases())) {
            score -= profile.getChronicDiseases().split(",").length * 10;
        }
        
        return Math.max(0, Math.min(100, score));
    }

    private String getHealthLevel(int score) {
        if (score >= 80) return "low";
        if (score >= 60) return "medium";
        return "high";
    }
}
