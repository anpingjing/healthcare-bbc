package com.healthcare.bbc.controller;

import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.entity.CustomerMessage;
import com.healthcare.bbc.entity.CustomerServiceSession;
import com.healthcare.bbc.service.CustomerMessageService;
import com.healthcare.bbc.service.CustomerServiceSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer-service")
@Tag(name = "客服消息管理", description = "客服消息与会话管理接口")
public class CustomerMessageController {

    @Autowired
    private CustomerMessageService customerMessageService;

    @Autowired
    private CustomerServiceSessionService sessionService;

    @Operation(summary = "获取用户最近消息")
    @GetMapping("/messages/{externalUserId}")
    public Result<List<CustomerMessage>> getRecentMessages(
            @Parameter(description = "外部用户ID") @PathVariable String externalUserId,
            @Parameter(description = "消息数量限制") @RequestParam(defaultValue = "20") int limit) {
        List<CustomerMessage> messages = customerMessageService.getRecentMessages(externalUserId, limit);
        return Result.success(messages);
    }

    @Operation(summary = "发送客服消息")
    @PostMapping("/messages/send")
    public Result<Void> sendMessage(
            @Parameter(description = "外部用户ID") @RequestParam String externalUserId,
            @Parameter(description = "消息内容") @RequestParam String content,
            @Parameter(description = "客服ID") @RequestParam Long agentId) {
        customerMessageService.saveOutgoingMessage(externalUserId, externalUserId, "text", content);
        return Result.success();
    }

    @Operation(summary = "获取活跃会话列表")
    @GetMapping("/sessions/active")
    public Result<List<CustomerServiceSession>> getActiveSessions(
            @Parameter(description = "客服ID") @RequestParam Long agentId) {
        List<CustomerServiceSession> sessions = sessionService.getActiveSessionsByAgent(agentId);
        return Result.success(sessions);
    }

    @Operation(summary = "分配客服")
    @PostMapping("/sessions/assign")
    public Result<Void> assignAgent(
            @Parameter(description = "外部用户ID") @RequestParam String externalUserId,
            @Parameter(description = "客服ID") @RequestParam Long agentId,
            @Parameter(description = "客服名称") @RequestParam String agentName) {
        sessionService.assignAgent(externalUserId, agentId, agentName);
        return Result.success();
    }

    @Operation(summary = "关闭会话")
    @PostMapping("/sessions/close/{sessionId}")
    public Result<Void> closeSession(
            @Parameter(description = "会话ID") @PathVariable Long sessionId) {
        sessionService.closeSession(sessionId);
        return Result.success();
    }
}
