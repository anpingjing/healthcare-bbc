package com.healthcare.bbc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.PageQueryDTO;
import com.healthcare.bbc.entity.UserTag;
import com.healthcare.bbc.service.UserTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Tag(name = "用户标签管理", description = "用户立体标签管理")
@RestController
@RequestMapping("/api/v1/user-tags")
@Validated
public class UserTagController {

    @Autowired
    private UserTagService userTagService;

    @Operation(summary = "分页查询用户标签")
    @GetMapping
    public Result<Page<UserTag>> list(PageQueryDTO queryDTO) {
        Page<UserTag> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return Result.success(userTagService.page(page));
    }

    @Operation(summary = "根据用户ID获取标签")
    @GetMapping("/user/{userId}")
    public Result<List<UserTag>> getByUserId(@PathVariable @NotNull Long userId) {
        return Result.success(userTagService.getByUserId(userId));
    }

    @Operation(summary = "根据用户ID和分类获取标签")
    @GetMapping("/user/{userId}/category/{tagCategory}")
    public Result<List<UserTag>> getByUserIdAndCategory(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull String tagCategory) {
        return Result.success(userTagService.getByUserIdAndCategory(userId, tagCategory));
    }

    @Operation(summary = "创建用户标签")
    @PostMapping
    public Result<UserTag> create(@RequestBody UserTag tag) {
        userTagService.save(tag);
        return Result.success(tag);
    }

    @Operation(summary = "批量创建用户标签")
    @PostMapping("/batch")
    public Result<Boolean> batchCreate(@RequestBody List<UserTag> tags) {
        return Result.success(userTagService.saveBatch(tags));
    }

    @Operation(summary = "删除用户标签")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.success(userTagService.removeById(id));
    }

    @Operation(summary = "根据用户ID删除所有标签")
    @DeleteMapping("/user/{userId}")
    public Result<Boolean> deleteByUserId(@PathVariable @NotNull Long userId) {
        return Result.success(userTagService.removeByUserId(userId));
    }
}
