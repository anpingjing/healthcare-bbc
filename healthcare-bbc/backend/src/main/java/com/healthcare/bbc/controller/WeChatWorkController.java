package com.healthcare.bbc.controller;

import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.dto.wechat.ExternalContactInfo;
import com.healthcare.bbc.dto.wechat.GroupChatInfo;
import com.healthcare.bbc.dto.wechat.GroupChatListResult;
import com.healthcare.bbc.dto.wechat.MessageSendResult;
import com.healthcare.bbc.service.WeChatWorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wechat")
@Tag(name = "企业微信对接", description = "企业微信API对接")
public class WeChatWorkController {

    @Autowired
    private WeChatWorkService weChatWorkService;

    @Operation(summary = "企业微信回调验证")
    @GetMapping("/callback")
    public String verifyCallback(
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {
        
        boolean verified = weChatWorkService.verifyUrl(msgSignature, timestamp, nonce, echostr);
        if (verified) {
            return echostr;
        }
        return "verification failed";
    }

    @Operation(summary = "企业微信消息回调")
    @PostMapping("/callback")
    public String handleCallback(@RequestBody String xmlContent) {
        weChatWorkService.handleCallback(xmlContent);
        return "success";
    }

    @Operation(summary = "发送文本消息")
    @PostMapping("/message/text")
    public Result<MessageSendResult> sendTextMessage(
            @RequestParam String userId,
            @RequestParam String content) {
        MessageSendResult result = weChatWorkService.sendTextMessage(userId, content);
        return Result.success(result);
    }
    
    @Operation(summary = "发送Markdown消息")
    @PostMapping("/message/markdown")
    public Result<MessageSendResult> sendMarkdownMessage(
            @RequestParam String userId,
            @RequestParam String content) {
        MessageSendResult result = weChatWorkService.sendMarkdownMessage(userId, content);
        return Result.success(result);
    }
    
    @Operation(summary = "发送模板卡片消息")
    @PostMapping("/message/template-card")
    public Result<MessageSendResult> sendTemplateCardMessage(
            @RequestParam String userId,
            @RequestBody Map<String, Object> templateCard) {
        MessageSendResult result = weChatWorkService.sendTemplateCardMessage(userId, templateCard);
        return Result.success(result);
    }
    
    @Operation(summary = "发送群聊消息")
    @PostMapping("/message/chat")
    public Result<MessageSendResult> sendChatMessage(
            @RequestParam String chatId,
            @RequestParam String content) {
        MessageSendResult result = weChatWorkService.sendTextMessageToChat(chatId, content);
        return Result.success(result);
    }

    @Operation(summary = "获取群聊列表")
    @GetMapping("/groupchat/list")
    public Result<GroupChatListResult> getGroupChatList(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "0") int statusFilter) {
        GroupChatListResult result = weChatWorkService.getGroupChatList(limit, cursor, statusFilter);
        return Result.success(result);
    }
    
    @Operation(summary = "获取群聊详情")
    @GetMapping("/groupchat/{chatId}")
    public Result<GroupChatInfo> getGroupChatInfo(@PathVariable String chatId) {
        GroupChatInfo result = weChatWorkService.getGroupChatInfo(chatId);
        return Result.success(result);
    }
    
    @Operation(summary = "创建群聊")
    @PostMapping("/groupchat/create")
    public Result<String> createGroupChat(
            @RequestParam String name,
            @RequestParam String owner,
            @RequestBody List<String> userIdList,
            @RequestParam(required = false) String chatId) {
        String result = weChatWorkService.createGroupChat(name, owner, userIdList, chatId);
        return Result.success(result);
    }

    @Operation(summary = "获取外部联系人信息")
    @GetMapping("/external-contact/{externalUserId}")
    public Result<ExternalContactInfo> getExternalContactInfo(@PathVariable String externalUserId) {
        ExternalContactInfo result = weChatWorkService.getExternalContactInfo(externalUserId);
        return Result.success(result);
    }
    
    @Operation(summary = "获取成员的外部联系人列表")
    @GetMapping("/external-contact/list")
    public Result<List<ExternalContactInfo>> getExternalContactList(@RequestParam String userId) {
        List<ExternalContactInfo> result = weChatWorkService.getExternalContactList(userId);
        return Result.success(result);
    }
    
    @Operation(summary = "刷新AccessToken")
    @PostMapping("/token/refresh")
    public Result<Void> refreshAccessToken() {
        weChatWorkService.refreshAccessToken();
        return Result.success();
    }
}
