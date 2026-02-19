package com.healthcare.admin.boot.service;

import java.util.List;
import java.util.Map;

public interface WechatService {

    /**
     * 获取AccessToken
     */
    String getAccessToken();

    /**
     * 刷新AccessToken
     */
    String refreshAccessToken();

    /**
     * 创建企业微信群
     */
    Map<String, Object> createGroup(String groupName, List<String> userIds, Long ownerId);

    /**
     * 解散企业微信群
     */
    boolean dismissGroup(String chatId);

    /**
     * 获取群成员列表
     */
    List<Map<String, Object>> getGroupMembers(String chatId);

    /**
     * 邀请成员入群
     */
    boolean inviteMembers(String chatId, List<String> userIds);

    /**
     * 移除群成员
     */
    boolean removeMember(String chatId, String userId);

    /**
     * 发送群消息
     */
    boolean sendGroupMessage(String chatId, String msgType, Map<String, Object> content);

    /**
     * 发送应用消息
     */
    boolean sendAppMessage(String toUser, String msgType, Map<String, Object> content);

    /**
     * 获取外部联系人列表
     */
    List<Map<String, Object>> getExternalContacts(String userId);

    /**
     * 获取客户详情
     */
    Map<String, Object> getExternalContactDetail(String externalUserId);

    /**
     * 配置群欢迎语
     */
    boolean setGroupWelcomeMsg(String chatId, Map<String, Object> welcomeMsg);

    /**
     * 获取群二维码
     */
    String getGroupQrCode(String chatId);
}
