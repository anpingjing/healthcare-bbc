package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.CustomerMessage;

import java.util.List;

public interface CustomerMessageService extends IService<CustomerMessage> {

    void saveIncomingMessage(String externalUserId, String fromUserId, String messageType, String content);

    void saveOutgoingMessage(String externalUserId, String toUserId, String messageType, String content);

    List<CustomerMessage> getRecentMessages(String externalUserId, int limit);

    List<CustomerMessage> getSessionMessages(String externalUserId, String startTime);
}
