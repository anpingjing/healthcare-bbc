package com.healthcare.admin.boot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_plan")
public class HealthPlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long planId;

    private Long userId;

    private Long profileId;

    private String planName;

    private Integer planType;

    private String healthGoals;

    private String interventions;

    private String dietAdvice;

    private String exercisePlan;

    private String medicationReminder;

    private Integer visitFrequency;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer riskLevel;

    private String benefits;

    private Long doctorId;

    private Integer doctorApproved;

    private LocalDateTime approveTime;

    private Integer status;

    private Integer completionRate;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
