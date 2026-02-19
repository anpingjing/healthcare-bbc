package com.healthcare.bbc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.work")
public class WeChatWorkProperties {
    private String corpId;
    private String corpSecret;
    private String agentId;
    private String token;
    private String encodingAesKey;
}
