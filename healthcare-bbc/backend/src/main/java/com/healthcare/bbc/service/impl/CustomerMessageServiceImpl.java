package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.entity.CustomerMessage;
import com.healthcare.bbc.mapper.CustomerMessageMapper;
import com.healthcare.bbc.service.CustomerMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CustomerMessageServiceImpl extends ServiceImpl<CustomerMessageMapper, CustomerMessage> implements CustomerMessageService {

    @Override
    public void saveIncomingMessage(String externalUserId, String fromUserId, String messageType, String content) {
        CustomerMessage message = new CustomerMessage();
        message.setWechatExternalUserId(externalUserId);
        message.setFromUserId(fromUserId);
        message.setMessageType(messageType);
        message.setContent(content);
        message.setDirection(1);
        message.setStatus(1);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());

        save(message);
        log.info("保存 incoming 消息: externalUserId={}, content={}", externalUserId, content);
    }

    @Override
    public void saveOutgoingMessage(String externalUserId, String toUserId, String messageType, String content) {
        CustomerMessage message = new CustomerMessage();
        message.setWechatExternalUserId(externalUserId);
        message.setToUserId(toUserId);
        message.setMessageType(messageType);
        message.setContent(content);
        message.setDirection(2);
        message.setStatus(1);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());

        save(message);
        log.info("保存 outgoing 消息: externalUserId={}, content={}", externalUserId, content);
    }

    @Override
    public List<CustomerMessage> getRecentMessages(String externalUserId, int limit) {
        return baseMapper.selectRecentMessages(externalUserId, limit);
    }

    @Override
    public List<CustomerMessage> getSessionMessages(String externalUserId, String startTime) {
        return baseMapper.selectMessagesByTimeRange(externalUserId, startTime);
    }
}
