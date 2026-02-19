package com.healthcare.bbc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.PageQueryDTO;
import com.healthcare.bbc.entity.InsurancePolicy;
import com.healthcare.bbc.service.InsurancePolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "保单管理", description = "用户保单信息管理")
@RestController
@RequestMapping("/api/v1/insurance-policies")
@Validated
public class InsurancePolicyController {

    @Autowired
    private InsurancePolicyService insurancePolicyService;

    @Operation(summary = "分页查询保单")
    @GetMapping
    public Result<Page<InsurancePolicy>> list(PageQueryDTO queryDTO) {
        Page<InsurancePolicy> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return Result.success(insurancePolicyService.page(page));
    }

    @Operation(summary = "根据用户ID获取保单")
    @GetMapping("/user/{userId}")
    public Result<List<InsurancePolicy>> getByUserId(@PathVariable @NotNull Long userId) {
        return Result.success(insurancePolicyService.getByUserId(userId));
    }

    @Operation(summary = "根据用户ID和状态获取保单")
    @GetMapping("/user/{userId}/status/{status}")
    public Result<List<InsurancePolicy>> getByUserIdAndStatus(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Integer status) {
        return Result.success(insurancePolicyService.getByUserIdAndStatus(userId, status));
    }

    @Operation(summary = "根据ID获取保单")
    @GetMapping("/{id}")
    public Result<InsurancePolicy> getById(@PathVariable @NotNull Long id) {
        InsurancePolicy policy = insurancePolicyService.getById(id);
        if (policy == null) {
            return Result.error("保单不存在");
        }
        return Result.success(policy);
    }

    @Operation(summary = "创建保单")
    @PostMapping
    public Result<InsurancePolicy> create(@RequestBody InsurancePolicy policy) {
        insurancePolicyService.save(policy);
        return Result.success(policy);
    }

    @Operation(summary = "更新保单")
    @PutMapping("/{id}")
    public Result<InsurancePolicy> update(@PathVariable @NotNull Long id, @RequestBody InsurancePolicy policy) {
        policy.setPolicyId(id);
        insurancePolicyService.updateById(policy);
        return Result.success(policy);
    }

    @Operation(summary = "删除保单")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.success(insurancePolicyService.removeById(id));
    }
}
