package com.healthcare.admin.boot.controller;

import com.healthcare.admin.boot.dto.KeywordReplyDTO;
import com.healthcare.admin.boot.service.KeywordReplyService;
import com.healthcare.admin.boot.vo.KeywordReplyVO;
import com.healthcare.admin.common.result.PageResult;
import com.healthcare.admin.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/keyword-replies")
@Tag(name = "关键词回复管理", description = "社群关键词自动回复配置")
public class KeywordReplyController {

    @Autowired
    private KeywordReplyService keywordReplyService;

    @GetMapping
    @Operation(summary = "获取关键词回复列表", description = "分页查询关键词回复配置")
    public Result<PageResult<KeywordReplyVO>> getReplyList(
            @Parameter(description = "社群ID") @RequestParam Long groupId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        PageResult<KeywordReplyVO> result = keywordReplyService.getReplyList(groupId, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{replyId}")
    @Operation(summary = "获取关键词回复详情", description = "根据ID获取关键词回复详情")
    public Result<KeywordReplyVO> getReplyById(
            @Parameter(description = "回复ID") @PathVariable Long replyId) {
        KeywordReplyVO vo = keywordReplyService.getReplyById(replyId);
        return Result.success(vo);
    }

    @PostMapping
    @Operation(summary = "创建关键词回复", description = "创建新的关键词回复配置")
    public Result<KeywordReplyVO> createReply(@Valid @RequestBody KeywordReplyDTO dto) {
        log.info("创建关键词回复: groupId={}, keyword={}", dto.getGroupId(), dto.getKeyword());
        KeywordReplyVO vo = keywordReplyService.createReply(dto);
        return Result.success("创建成功", vo);
    }

    @PutMapping("/{replyId}")
    @Operation(summary = "更新关键词回复", description = "更新关键词回复配置")
    public Result<KeywordReplyVO> updateReply(
            @Parameter(description = "回复ID") @PathVariable Long replyId,
            @Valid @RequestBody KeywordReplyDTO dto) {
        log.info("更新关键词回复: replyId={}", replyId);
        KeywordReplyVO vo = keywordReplyService.updateReply(replyId, dto);
        return Result.success("更新成功", vo);
    }

    @DeleteMapping("/{replyId}")
    @Operation(summary = "删除关键词回复", description = "删除关键词回复配置")
    public Result<Void> deleteReply(
            @Parameter(description = "回复ID") @PathVariable Long replyId) {
        log.info("删除关键词回复: replyId={}", replyId);
        keywordReplyService.deleteReply(replyId);
        return Result.success("删除成功");
    }

    @PutMapping("/status")
    @Operation(summary = "批量更新状态", description = "批量启用/禁用关键词回复")
    public Result<Void> updateStatus(
            @RequestBody List<Long> replyIds,
            @Parameter(description = "状态") @RequestParam Integer status) {
        log.info("批量更新关键词回复状态: count={}, status={}", replyIds.size(), status);
        keywordReplyService.updateStatus(replyIds, status);
        return Result.success("状态更新成功");
    }
}
