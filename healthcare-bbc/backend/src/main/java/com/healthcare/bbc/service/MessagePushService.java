package com.healthcare.bbc.service;

import com.healthcare.bbc.dto.MessagePushDTO;

public interface MessagePushService {
    void pushTextMessage(Long userId, String content);
    void pushNewsMessage(Long userId, String title, String description, String url, String picUrl);
    void pushTaskReminder(Long userId, String taskName, String taskTime);
    void pushHealthAlert(Long userId, String alertType, String alertMessage);
    void pushInsuranceRecommendation(Long userId, String productName, String productDescription);
    void pushMessage(MessagePushDTO messagePushDTO);
}
