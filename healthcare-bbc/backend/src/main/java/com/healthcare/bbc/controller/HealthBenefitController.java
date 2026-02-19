package com.healthcare.bbc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.PageQueryDTO;
import com.healthcare.bbc.entity.HealthBenefit;
import com.healthcare.bbc.service.HealthBenefitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "健康权益管理", description = "健康权益定义管理")
@RestController
@RequestMapping("/api/v1/health-benefits")
@Validated
public class HealthBenefitController {

    @Autowired
    private HealthBenefitService healthBenefitService;

    @Operation(summary = "分页查询健康权益")
    @GetMapping
    public Result<Page<HealthBenefit>> list(PageQueryDTO queryDTO) {
        Page<HealthBenefit> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return Result.success(healthBenefitService.page(page));
    }

    @Operation(summary = "获取所有可用健康权益")
    @GetMapping("/available")
    public Result<List<HealthBenefit>> getAvailableBenefits() {
        return Result.success(healthBenefitService.getAvailableBenefits());
    }

    @Operation(summary = "根据权益类型获取健康权益")
    @GetMapping("/type/{benefitType}")
    public Result<List<HealthBenefit>> getByBenefitType(@PathVariable @NotNull String benefitType) {
        return Result.success(healthBenefitService.getByBenefitType(benefitType));
    }

    @Operation(summary = "根据ID获取健康权益")
    @GetMapping("/{id}")
    public Result<HealthBenefit> getById(@PathVariable @NotNull Long id) {
        HealthBenefit benefit = healthBenefitService.getById(id);
        if (benefit == null) {
            return Result.error("健康权益不存在");
        }
        return Result.success(benefit);
    }

    @Operation(summary = "创建健康权益")
    @PostMapping
    public Result<HealthBenefit> create(@RequestBody HealthBenefit benefit) {
        healthBenefitService.save(benefit);
        return Result.success(benefit);
    }

    @Operation(summary = "更新健康权益")
    @PutMapping("/{id}")
    public Result<HealthBenefit> update(@PathVariable @NotNull Long id, @RequestBody HealthBenefit benefit) {
        benefit.setBenefitId(id);
        healthBenefitService.updateById(benefit);
        return Result.success(benefit);
    }

    @Operation(summary = "删除健康权益")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.success(healthBenefitService.removeById(id));
    }
}
