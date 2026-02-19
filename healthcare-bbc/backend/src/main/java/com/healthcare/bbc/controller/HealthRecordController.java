package com.healthcare.bbc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.PageQueryDTO;
import com.healthcare.bbc.entity.HealthRecord;
import com.healthcare.bbc.service.HealthRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Tag(name = "健康档案管理", description = "用户健康档案 CRUD")
@RestController
@RequestMapping("/api/v1/health-records")
@Validated
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @Operation(summary = "分页查询健康档案")
    @GetMapping
    public Result<Page<HealthRecord>> list(PageQueryDTO queryDTO) {
        Page<HealthRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(HealthRecord::getCreateTime);
        return Result.success(healthRecordService.page(page, wrapper));
    }

    @Operation(summary = "根据用户ID获取健康档案")
    @GetMapping("/user/{userId}")
    public Result<HealthRecord> getByUserId(@PathVariable @NotNull Long userId) {
        HealthRecord record = healthRecordService.getByUserId(userId);
        if (record == null) {
            return Result.error("健康档案不存在");
        }
        return Result.success(record);
    }

    @Operation(summary = "根据ID获取健康档案")
    @GetMapping("/{id}")
    public Result<HealthRecord> getById(@PathVariable @NotNull Long id) {
        HealthRecord record = healthRecordService.getById(id);
        if (record == null) {
            return Result.error("健康档案不存在");
        }
        return Result.success(record);
    }

    @Operation(summary = "创建健康档案")
    @PostMapping
    public Result<HealthRecord> create(@RequestBody HealthRecord record) {
        healthRecordService.save(record);
        return Result.success(record);
    }

    @Operation(summary = "更新健康档案")
    @PutMapping("/{id}")
    public Result<HealthRecord> update(@PathVariable @NotNull Long id, @RequestBody HealthRecord record) {
        record.setRecordId(id);
        healthRecordService.updateById(record);
        return Result.success(record);
    }

    @Operation(summary = "删除健康档案")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.success(healthRecordService.removeById(id));
    }
}
