package com.healthcare.bbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer_service_session")
public class CustomerServiceSession {
    @TableId(type = IdType.AUTO)
    private Long sessionId;
    private String wechatExternalUserId;
    private Long agentId;
    private String agentName;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
