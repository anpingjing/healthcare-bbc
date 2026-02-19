package com.healthcare.admin.boot.service;

import com.healthcare.admin.boot.dto.HealthProfileDTO;
import com.healthcare.admin.boot.vo.HealthProfileVO;
import com.healthcare.admin.common.result.PageResult;

import java.util.Map;

public interface HealthProfileService {

    /**
     * 分页查询健康档案列表
     */
    PageResult<HealthProfileVO> getProfileList(Integer pageNum, Integer pageSize, 
            String keyword, Integer healthLevel, Long doctorId);

    /**
     * 获取健康档案详情
     */
    HealthProfileVO getProfileById(Long profileId);

    /**
     * 根据用户ID获取健康档案
     */
    HealthProfileVO getProfileByUserId(Long userId);

    /**
     * 创建健康档案
     */
    HealthProfileVO createProfile(HealthProfileDTO dto);

    /**
     * 更新健康档案
     */
    HealthProfileVO updateProfile(Long profileId, HealthProfileDTO dto);

    /**
     * 删除健康档案
     */
    void deleteProfile(Long profileId);

    /**
     * 计算健康评分
     */
    Integer calculateHealthScore(Long profileId);

    /**
     * 更新健康等级
     */
    void updateHealthLevel(Long profileId, String healthLevel);

    /**
     * 分配服务角色
     */
    void assignServiceRoles(Long profileId, Long doctorId, Long healthManagerId, 
            Long benefitAdvisorId, Long insuranceAdvisorId);

    /**
     * 获取健康统计数据
     */
    Map<String, Object> getHealthStatistics();
}
