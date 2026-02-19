package com.healthcare.bbc.dto.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageSendResult {
    private Integer errcode;
    private String errmsg;
    
    @JsonProperty("msgid")
    private String msgId;
    
    @JsonProperty("response_code")
    private String responseCode;
}
