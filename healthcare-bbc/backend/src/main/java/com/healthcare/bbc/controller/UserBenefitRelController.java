package com.healthcare.bbc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.PageQueryDTO;
import com.healthcare.bbc.entity.UserBenefitRel;
import com.healthcare.bbc.service.UserBenefitRelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "用户权益管理", description = "用户权益关联管理")
@RestController
@RequestMapping("/api/v1/user-benefit-rels")
@Validated
public class UserBenefitRelController {

    @Autowired
    private UserBenefitRelService userBenefitRelService;

    @Operation(summary = "分页查询用户权益")
    @GetMapping
    public Result<Page<UserBenefitRel>> list(PageQueryDTO queryDTO) {
        Page<UserBenefitRel> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return Result.success(userBenefitRelService.page(page));
    }

    @Operation(summary = "根据用户ID获取权益")
    @GetMapping("/user/{userId}")
    public Result<List<UserBenefitRel>> getByUserId(@PathVariable @NotNull Long userId) {
        return Result.success(userBenefitRelService.getByUserId(userId));
    }

    @Operation(summary = "根据用户ID和状态获取权益")
    @GetMapping("/user/{userId}/status/{status}")
    public Result<List<UserBenefitRel>> getByUserIdAndStatus(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Integer status) {
        return Result.success(userBenefitRelService.getByUserIdAndStatus(userId, status));
    }

    @Operation(summary = "创建用户权益")
    @PostMapping
    public Result<UserBenefitRel> create(@RequestBody UserBenefitRel rel) {
        userBenefitRelService.save(rel);
        return Result.success(rel);
    }

    @Operation(summary = "更新用户权益")
    @PutMapping("/{id}")
    public Result<UserBenefitRel> update(@PathVariable @NotNull Long id, @RequestBody UserBenefitRel rel) {
        rel.setRelId(id);
        userBenefitRelService.updateById(rel);
        return Result.success(rel);
    }

    @Operation(summary = "删除用户权益")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.success(userBenefitRelService.removeById(id));
    }
}
