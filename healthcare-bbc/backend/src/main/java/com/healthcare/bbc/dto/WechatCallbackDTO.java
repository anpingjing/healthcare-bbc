package com.healthcare.bbc.dto;

import lombok.Data;

@Data
public class WechatCallbackDTO {
    private String ToUserName;
    private String FromUserName;
    private String CreateTime;
    private String MsgType;
    private String Content;
    private String MsgId;
    private String AgentID;
}
