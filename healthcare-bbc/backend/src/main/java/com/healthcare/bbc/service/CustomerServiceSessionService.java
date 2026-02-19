package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.CustomerServiceSession;

import java.util.List;

public interface CustomerServiceSessionService extends IService<CustomerServiceSession> {

    CustomerServiceSession createSession(String externalUserId, Long agentId, String agentName);

    CustomerServiceSession getActiveSession(String externalUserId);

    List<CustomerServiceSession> getActiveSessionsByAgent(Long agentId);

    boolean closeSession(Long sessionId);

    boolean assignAgent(String externalUserId, Long agentId, String agentName);
}
