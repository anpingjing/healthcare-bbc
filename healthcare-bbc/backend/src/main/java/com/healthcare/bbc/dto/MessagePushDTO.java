package com.healthcare.bbc.dto;

import lombok.Data;

@Data
public class MessagePushDTO {
    private Long userId;
    private String wechatId;
    private MessageType messageType;
    private String title;
    private String content;
    private String url;
    private String picUrl;

    public enum MessageType {
        TEXT,
        NEWS,
        TASK_REMINDER,
        HEALTH_ALERT,
        INSURANCE_RECOMMENDATION
    }
}
