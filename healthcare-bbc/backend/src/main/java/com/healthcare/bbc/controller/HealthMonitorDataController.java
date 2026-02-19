package com.healthcare.bbc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.PageQueryDTO;
import com.healthcare.bbc.entity.HealthMonitorData;
import com.healthcare.bbc.service.HealthMonitorDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "健康监测数据", description = "用户健康监测数据管理")
@RestController
@RequestMapping("/api/v1/health-monitor-data")
@Validated
public class HealthMonitorDataController {

    @Autowired
    private HealthMonitorDataService healthMonitorDataService;

    @Operation(summary = "分页查询健康监测数据")
    @GetMapping
    public Result<Page<HealthMonitorData>> list(PageQueryDTO queryDTO) {
        Page<HealthMonitorData> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return Result.success(healthMonitorDataService.page(page));
    }

    @Operation(summary = "根据用户ID获取健康监测数据")
    @GetMapping("/user/{userId}")
    public Result<List<HealthMonitorData>> getByUserId(@PathVariable @NotNull Long userId) {
        return Result.success(healthMonitorDataService.getByUserId(userId));
    }

    @Operation(summary = "根据用户ID和数据类型获取监测数据")
    @GetMapping("/user/{userId}/type/{dataType}")
    public Result<List<HealthMonitorData>> getByUserIdAndDataType(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull String dataType) {
        return Result.success(healthMonitorDataService.getByUserIdAndDataType(userId, dataType));
    }

    @Operation(summary = "根据用户ID和时间范围获取监测数据")
    @GetMapping("/user/{userId}/time-range")
    public Result<List<HealthMonitorData>> getByUserIdAndTimeRange(
            @PathVariable @NotNull Long userId,
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(healthMonitorDataService.getByUserIdAndTimeRange(userId, startTime, endTime));
    }

    @Operation(summary = "上传健康监测数据")
    @PostMapping
    public Result<HealthMonitorData> create(@RequestBody HealthMonitorData data) {
        healthMonitorDataService.save(data);
        return Result.success(data);
    }

    @Operation(summary = "批量上传健康监测数据")
    @PostMapping("/batch")
    public Result<Boolean> batchCreate(@RequestBody List<HealthMonitorData> dataList) {
        return Result.success(healthMonitorDataService.saveBatch(dataList));
    }

    @Operation(summary = "删除健康监测数据")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.success(healthMonitorDataService.removeById(id));
    }
}
