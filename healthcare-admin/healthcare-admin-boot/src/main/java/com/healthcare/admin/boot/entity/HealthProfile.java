package com.healthcare.admin.boot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_profile")
public class HealthProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long profileId;

    private Long userId;

    private String realName;

    private Integer gender;

    private LocalDate birthDate;

    private String idCard;

    private String phone;

    private String emergencyContact;

    private String emergencyPhone;

    private BigDecimal height;

    private BigDecimal weight;

    private BigDecimal bmi;

    private String bloodType;

    private String allergies;

    private String medicalHistory;

    private String familyHistory;

    private String chronicDiseases;

    private String medications;

    private Integer healthScore;

    private String healthLevel;

    private String riskTags;

    private Long doctorId;

    private Long healthManagerId;

    private Long benefitAdvisorId;

    private Long insuranceAdvisorId;

    private Integer status;

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
