package com.healthcare.bbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer_message")
public class CustomerMessage {
    @TableId(type = IdType.AUTO)
    private Long messageId;
    private String wechatExternalUserId;
    private String fromUserId;
    private String toUserId;
    private String messageType;
    private String content;
    private String mediaUrl;
    private Integer direction;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
