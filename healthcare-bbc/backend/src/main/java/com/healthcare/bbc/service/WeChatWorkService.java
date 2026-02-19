package com.healthcare.bbc.service;

import com.healthcare.bbc.dto.wechat.ExternalContactInfo;
import com.healthcare.bbc.dto.wechat.GroupChatInfo;
import com.healthcare.bbc.dto.wechat.GroupChatListResult;
import com.healthcare.bbc.dto.wechat.MessageSendResult;

import java.util.List;
import java.util.Map;

public interface WeChatWorkService {
    
    String getAccessToken();
    
    void refreshAccessToken();
    
    boolean verifyUrl(String msgSignature, String timestamp, String nonce, String echostr);
    
    void handleCallback(String xmlContent);
    
    MessageSendResult sendTextMessage(String userId, String content);
    
    MessageSendResult sendTextMessageToChat(String chatId, String content);
    
    MessageSendResult sendMarkdownMessage(String userId, String content);
    
    MessageSendResult sendTemplateCardMessage(String userId, Map<String, Object> templateCard);
    
    GroupChatListResult getGroupChatList(int limit, String cursor, int statusFilter);
    
    GroupChatInfo getGroupChatInfo(String chatId);
    
    String createGroupChat(String name, String owner, List<String> userIdList, String chatId);
    
    ExternalContactInfo getExternalContactInfo(String externalUserId);
    
    List<ExternalContactInfo> getExternalContactList(String userId);
    
    void handleEventCallback(Map<String, Object> eventMap);
}
