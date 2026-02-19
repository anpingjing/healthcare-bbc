package com.healthcare.bbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("health_benefit")
public class HealthBenefit {
    @TableId(type = IdType.AUTO)
    private Long benefitId;
    private String benefitName;
    private String benefitType;
    private String description;
    private Integer validPeriod;
    private Integer status;
    private LocalDateTime createTime;
}
