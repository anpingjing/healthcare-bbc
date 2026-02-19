package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.entity.CustomerServiceSession;
import com.healthcare.bbc.mapper.CustomerServiceSessionMapper;
import com.healthcare.bbc.service.CustomerServiceSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CustomerServiceSessionServiceImpl extends ServiceImpl<CustomerServiceSessionMapper, CustomerServiceSession> implements CustomerServiceSessionService {

    @Override
    public CustomerServiceSession createSession(String externalUserId, Long agentId, String agentName) {
        CustomerServiceSession session = new CustomerServiceSession();
        session.setWechatExternalUserId(externalUserId);
        session.setAgentId(agentId);
        session.setAgentName(agentName);
        session.setStatus(1);
        session.setStartTime(LocalDateTime.now());
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());

        save(session);
        log.info("创建客服会话: sessionId={}, externalUserId={}, agentId={}", session.getSessionId(), externalUserId, agentId);
        return session;
    }

    @Override
    public CustomerServiceSession getActiveSession(String externalUserId) {
        return baseMapper.selectActiveSession(externalUserId);
    }

    @Override
    public List<CustomerServiceSession> getActiveSessionsByAgent(Long agentId) {
        return baseMapper.selectActiveSessionsByAgent(agentId);
    }

    @Override
    public boolean closeSession(Long sessionId) {
        int result = baseMapper.closeSession(sessionId);
        if (result > 0) {
            log.info("关闭客服会话: sessionId={}", sessionId);
            return true;
        }
        return false;
    }

    @Override
    public boolean assignAgent(String externalUserId, Long agentId, String agentName) {
        CustomerServiceSession existingSession = getActiveSession(externalUserId);
        if (existingSession != null) {
            closeSession(existingSession.getSessionId());
        }
        createSession(externalUserId, agentId, agentName);
        log.info("分配客服: externalUserId={}, agentId={}, agentName={}", externalUserId, agentId, agentName);
        return true;
    }
}
