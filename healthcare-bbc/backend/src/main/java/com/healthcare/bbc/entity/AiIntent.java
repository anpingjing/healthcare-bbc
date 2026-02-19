package com.healthcare.bbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_intent")
public class AiIntent {
    @TableId(type = IdType.AUTO)
    private Long intentId;
    private String intentCode;
    private String intentName;
    private String intentCategory;
    private String targetRole;
    private String keywords;
    private Double confidenceThreshold;
    private Integer priority;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
