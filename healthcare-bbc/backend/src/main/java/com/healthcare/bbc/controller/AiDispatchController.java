package com.healthcare.bbc.controller;

import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.entity.AiIntent;
import com.healthcare.bbc.entity.CustomerMessage;
import com.healthcare.bbc.service.AiDispatchService;
import com.healthcare.bbc.service.CustomerMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI智能调度", description = "AI意图识别与消息调度接口")
public class AiDispatchController {

    @Autowired
    private AiDispatchService aiDispatchService;

    @Autowired
    private CustomerMessageService customerMessageService;

    @Operation(summary = "识别消息意图")
    @PostMapping("/intent/recognize")
    public Result<AiIntent> recognizeIntent(
            @Parameter(description = "消息内容") @RequestParam String messageContent) {
        AiIntent intent = aiDispatchService.recognizeIntent(messageContent);
        return Result.success(intent);
    }

    @Operation(summary = "分析消息")
    @PostMapping("/analyze")
    public Result<Map<String, Object>> analyzeMessage(
            @Parameter(description = "消息内容") @RequestParam String messageContent) {
        Map<String, Object> analysis = aiDispatchService.analyzeMessage(messageContent);
        return Result.success(analysis);
    }

    @Operation(summary = "生成AI回复")
    @PostMapping("/response/generate")
    public Result<String> generateResponse(
            @Parameter(description = "外部用户ID") @RequestParam String externalUserId,
            @Parameter(description = "消息内容") @RequestParam String messageContent) {
        List<CustomerMessage> context = customerMessageService.getRecentMessages(externalUserId, 5);
        String response = aiDispatchService.generateAiResponse(externalUserId, messageContent, context);
        return Result.success(response);
    }

    @Operation(summary = "提交AI反馈")
    @PostMapping("/feedback")
    public Result<Void> submitFeedback(
            @Parameter(description = "消息ID") @RequestParam Long messageId,
            @Parameter(description = "是否有帮助") @RequestParam boolean isHelpful,
            @Parameter(description = "修正内容") @RequestParam(required = false) String correction) {
        aiDispatchService.learnFromFeedback(messageId, isHelpful, correction);
        return Result.success();
    }
}
