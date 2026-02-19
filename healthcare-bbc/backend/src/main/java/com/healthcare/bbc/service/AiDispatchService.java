package com.healthcare.bbc.service;

import com.healthcare.bbc.entity.AiIntent;
import com.healthcare.bbc.entity.CustomerMessage;

import java.util.List;
import java.util.Map;

public interface AiDispatchService {

    AiIntent recognizeIntent(String messageContent);

    String dispatchToRole(AiIntent intent, String externalUserId, String messageContent);

    String generateAiResponse(String externalUserId, String messageContent, List<CustomerMessage> context);

    Map<String, Object> analyzeMessage(String messageContent);

    void learnFromFeedback(Long messageId, boolean isHelpful, String correction);
}
