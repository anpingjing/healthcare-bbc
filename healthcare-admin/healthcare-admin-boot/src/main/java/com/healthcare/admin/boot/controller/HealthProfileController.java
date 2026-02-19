package com.healthcare.admin.boot.controller;

import com.healthcare.admin.boot.dto.HealthProfileDTO;
import com.healthcare.admin.boot.service.HealthProfileService;
import com.healthcare.admin.boot.vo.HealthProfileVO;
import com.healthcare.admin.common.result.PageResult;
import com.healthcare.admin.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/health-profiles")
@Tag(name = "健康档案管理", description = "用户健康档案CRUD操作")
public class HealthProfileController {

    @Autowired
    private HealthProfileService healthProfileService;

    @GetMapping
    @Operation(summary = "获取健康档案列表", description = "分页查询健康档案")
    public Result<PageResult<HealthProfileVO>> getProfileList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "健康等级") @RequestParam(required = false) Integer healthLevel,
            @Parameter(description = "医生ID") @RequestParam(required = false) Long doctorId) {
        PageResult<HealthProfileVO> result = healthProfileService.getProfileList(
                pageNum, pageSize, keyword, healthLevel, doctorId);
        return Result.success(result);
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "获取健康档案详情", description = "根据ID获取健康档案详情")
    public Result<HealthProfileVO> getProfileById(
            @Parameter(description = "档案ID") @PathVariable Long profileId) {
        HealthProfileVO vo = healthProfileService.getProfileById(profileId);
        return Result.success(vo);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户健康档案", description = "根据用户ID获取健康档案")
    public Result<HealthProfileVO> getProfileByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        HealthProfileVO vo = healthProfileService.getProfileByUserId(userId);
        return Result.success(vo);
    }

    @PostMapping
    @Operation(summary = "创建健康档案", description = "创建新的健康档案")
    public Result<HealthProfileVO> createProfile(@Valid @RequestBody HealthProfileDTO dto) {
        log.info("创建健康档案: userId={}", dto.getUserId());
        HealthProfileVO vo = healthProfileService.createProfile(dto);
        return Result.success("创建成功", vo);
    }

    @PutMapping("/{profileId}")
    @Operation(summary = "更新健康档案", description = "更新健康档案信息")
    public Result<HealthProfileVO> updateProfile(
            @Parameter(description = "档案ID") @PathVariable Long profileId,
            @Valid @RequestBody HealthProfileDTO dto) {
        log.info("更新健康档案: profileId={}", profileId);
        HealthProfileVO vo = healthProfileService.updateProfile(profileId, dto);
        return Result.success("更新成功", vo);
    }

    @DeleteMapping("/{profileId}")
    @Operation(summary = "删除健康档案", description = "删除健康档案")
    public Result<Void> deleteProfile(
            @Parameter(description = "档案ID") @PathVariable Long profileId) {
        log.info("删除健康档案: profileId={}", profileId);
        healthProfileService.deleteProfile(profileId);
        return Result.success("删除成功");
    }

    @PostMapping("/{profileId}/calculate-score")
    @Operation(summary = "计算健康评分", description = "根据档案信息计算健康评分")
    public Result<Integer> calculateHealthScore(
            @Parameter(description = "档案ID") @PathVariable Long profileId) {
        Integer score = healthProfileService.calculateHealthScore(profileId);
        return Result.success(score);
    }

    @PutMapping("/{profileId}/assign-roles")
    @Operation(summary = "分配服务角色", description = "为用户分配4R服务角色")
    public Result<Void> assignServiceRoles(
            @Parameter(description = "档案ID") @PathVariable Long profileId,
            @Parameter(description = "家庭医生ID") @RequestParam(required = false) Long doctorId,
            @Parameter(description = "健康管家ID") @RequestParam(required = false) Long healthManagerId,
            @Parameter(description = "权益顾问ID") @RequestParam(required = false) Long benefitAdvisorId,
            @Parameter(description = "保险顾问ID") @RequestParam(required = false) Long insuranceAdvisorId) {
        log.info("分配服务角色: profileId={}", profileId);
        healthProfileService.assignServiceRoles(profileId, doctorId, healthManagerId, 
                benefitAdvisorId, insuranceAdvisorId);
        return Result.success("分配成功");
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取健康统计数据", description = "获取健康档案统计数据")
    public Result<Map<String, Object>> getHealthStatistics() {
        Map<String, Object> statistics = healthProfileService.getHealthStatistics();
        return Result.success(statistics);
    }
}
