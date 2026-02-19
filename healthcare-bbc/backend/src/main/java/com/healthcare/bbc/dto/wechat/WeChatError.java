package com.healthcare.bbc.dto.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WeChatError {
    private Integer errcode;
    private String errmsg;
    
    public boolean isSuccess() {
        return errcode == null || errcode == 0;
    }
    
    public static WeChatError of(Integer errcode, String errmsg) {
        WeChatError error = new WeChatError();
        error.setErrcode(errcode);
        error.setErrmsg(errmsg);
        return error;
    }
}
