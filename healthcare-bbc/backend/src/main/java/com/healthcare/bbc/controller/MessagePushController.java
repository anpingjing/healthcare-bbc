package com.healthcare.bbc.controller;

import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.MessagePushDTO;
import com.healthcare.bbc.service.MessagePushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Tag(name = "消息推送", description = "企业微信消息推送")
@RestController
@RequestMapping("/api/v1/message-push")
@Validated
public class MessagePushController {

    @Autowired
    private MessagePushService messagePushService;

    @Operation(summary = "发送文本消息")
    @PostMapping("/text")
    public Result<Void> pushTextMessage(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull String content) {
        messagePushService.pushTextMessage(userId, content);
        return Result.success();
    }

    @Operation(summary = "发送图文消息")
    @PostMapping("/news")
    public Result<Void> pushNewsMessage(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull String title,
            @RequestParam String description,
            @RequestParam String url,
            @RequestParam String picUrl) {
        messagePushService.pushNewsMessage(userId, title, description, url, picUrl);
        return Result.success();
    }

    @Operation(summary = "发送任务提醒")
    @PostMapping("/task-reminder")
    public Result<Void> pushTaskReminder(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull String taskName,
            @RequestParam @NotNull String taskTime) {
        messagePushService.pushTaskReminder(userId, taskName, taskTime);
        return Result.success();
    }

    @Operation(summary = "发送健康预警")
    @PostMapping("/health-alert")
    public Result<Void> pushHealthAlert(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull String alertType,
            @RequestParam @NotNull String alertMessage) {
        messagePushService.pushHealthAlert(userId, alertType, alertMessage);
        return Result.success();
    }

    @Operation(summary = "发送保险推荐")
    @PostMapping("/insurance-recommendation")
    public Result<Void> pushInsuranceRecommendation(
            @RequestParam @NotNull Long userId,
            @RequestParam @NotNull String productName,
            @RequestParam @NotNull String productDescription) {
        messagePushService.pushInsuranceRecommendation(userId, productName, productDescription);
        return Result.success();
    }

    @Operation(summary = "发送自定义消息")
    @PostMapping("/custom")
    public Result<Void> pushMessage(@Valid @RequestBody MessagePushDTO messagePushDTO) {
        messagePushService.pushMessage(messagePushDTO);
        return Result.success();
    }
}
