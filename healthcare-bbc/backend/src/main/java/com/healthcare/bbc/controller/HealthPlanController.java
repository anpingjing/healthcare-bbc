package com.healthcare.bbc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.PageQueryDTO;
import com.healthcare.bbc.entity.HealthPlan;
import com.healthcare.bbc.service.HealthPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "健康计划管理", description = "用户健康计划管理")
@RestController
@RequestMapping("/api/v1/health-plans")
@Validated
public class HealthPlanController {

    @Autowired
    private HealthPlanService healthPlanService;

    @Operation(summary = "分页查询健康计划")
    @GetMapping
    public Result<Page<HealthPlan>> list(PageQueryDTO queryDTO) {
        Page<HealthPlan> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return Result.success(healthPlanService.page(page));
    }

    @Operation(summary = "根据用户ID获取健康计划")
    @GetMapping("/user/{userId}")
    public Result<List<HealthPlan>> getByUserId(@PathVariable @NotNull Long userId) {
        return Result.success(healthPlanService.getByUserId(userId));
    }

    @Operation(summary = "根据用户ID和状态获取健康计划")
    @GetMapping("/user/{userId}/status/{status}")
    public Result<List<HealthPlan>> getByUserIdAndStatus(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Integer status) {
        return Result.success(healthPlanService.getByUserIdAndStatus(userId, status));
    }

    @Operation(summary = "创建健康计划")
    @PostMapping
    public Result<HealthPlan> create(@RequestBody HealthPlan plan) {
        healthPlanService.save(plan);
        return Result.success(plan);
    }

    @Operation(summary = "更新健康计划")
    @PutMapping("/{id}")
    public Result<HealthPlan> update(@PathVariable @NotNull Long id, @RequestBody HealthPlan plan) {
        plan.setPlanId(id);
        healthPlanService.updateById(plan);
        return Result.success(plan);
    }

    @Operation(summary = "删除健康计划")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.success(healthPlanService.removeById(id));
    }
}
