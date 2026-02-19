package com.healthcare.bbc.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserCreateDTO {
    @NotBlank(message = "企业微信ID不能为空")
    @Size(max = 64, message = "企业微信ID长度不能超过64")
    private String wechatId;

    @NotBlank(message = "用户姓名不能为空")
    @Size(max = 32, message = "用户姓名长度不能超过32")
    private String name;

    private Integer gender;

    private Integer age;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;
}
