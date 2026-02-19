package com.healthcare.bbc.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    @NotBlank(message = "企业微信ID不能为空")
    private String wechatId;
}
