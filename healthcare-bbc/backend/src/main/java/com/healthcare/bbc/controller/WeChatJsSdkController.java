package com.healthcare.bbc.controller;

import com.healthcare.bbc.common.Result;
import com.healthcare.bbc.service.WeChatWorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.security.MessageDigest;
import java.util.Formatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wechat/js-sdk")
@Tag(name = "企业微信JS-SDK", description = "JS-SDK配置接口")
public class WeChatJsSdkController {

    @Autowired
    private WeChatWorkService weChatWorkService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Operation(summary = "获取JS-SDK配置")
    @GetMapping("/config")
    public Result<JsSdkConfig> getJsSdkConfig(@RequestParam String url) {
        try {
            String accessToken = weChatWorkService.getAccessToken();
            String jsapiTicket = getJsApiTicket(accessToken);

            String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String signature = generateSignature(jsapiTicket, nonceStr, timestamp, url);

            JsSdkConfig config = new JsSdkConfig();
            config.setAppId("wwace533e386c63f72");
            config.setTimestamp(timestamp);
            config.setNonceStr(nonceStr);
            config.setSignature(signature);

            return Result.success(config);
        } catch (Exception e) {
            return Result.error("获取JS-SDK配置失败: " + e.getMessage());
        }
    }

    private String getJsApiTicket(String accessToken) {
        String url = String.format(
                "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=%s",
                accessToken
        );

        RestTemplate restTemplate = new RestTemplate();
        JsApiTicketResponse response = restTemplate.getForObject(url, JsApiTicketResponse.class);

        if (response != null && response.getTicket() != null) {
            return response.getTicket();
        }
        throw new RuntimeException("获取jsapi_ticket失败");
    }

    private String generateSignature(String jsapiTicket, String nonceStr, String timestamp, String url) {
        String string1 = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s",
                jsapiTicket, nonceStr, timestamp, url);

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            return byteToHex(crypt.digest());
        } catch (Exception e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    @Data
    public static class JsSdkConfig {
        private String appId;
        private String timestamp;
        private String nonceStr;
        private String signature;
    }

    @Data
    public static class JsApiTicketResponse {
        private Integer errcode;
        private String errmsg;
        private String ticket;
        private Integer expires_in;
    }
}
