package com.healthcare.bbc.vo;

import com.healthcare.bbc.annotation.Desensitize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private Long userId;
    private String wechatId;
    private String name;
    private Integer gender;
    private String genderText;
    private Integer age;
    @Desensitize(type = Desensitize.DesensitizeType.PHONE)
    private String phone;
    @Desensitize(type = Desensitize.DesensitizeType.EMAIL)
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
    private String statusText;
}
