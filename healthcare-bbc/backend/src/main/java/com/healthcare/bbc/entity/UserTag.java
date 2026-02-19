package com.healthcare.bbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_tag")
public class UserTag {
    @TableId(type = IdType.AUTO)
    private Long tagId;
    private Long userId;
    private String tagCategory;
    private String tagName;
    private String tagSource;
    private BigDecimal confidenceScore;
    private LocalDateTime createTime;
}
