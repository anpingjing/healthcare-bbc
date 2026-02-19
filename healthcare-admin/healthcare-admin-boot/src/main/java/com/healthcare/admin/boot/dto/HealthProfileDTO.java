package com.healthcare.admin.boot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "健康档案DTO")
public class HealthProfileDTO {

    @Schema(description = "档案ID")
    private Long profileId;

    @Schema(description = "用户ID")
    private Long userId;

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "真实姓名", required = true)
    private String realName;

    @Schema(description = "性别: 0-未知 1-男 2-女")
    private Integer gender;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "身份证号")
    private String idCard;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "紧急联系人")
    private String emergencyContact;

    @Schema(description = "紧急联系电话")
    private String emergencyPhone;

    @Schema(description = "身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "血型")
    private String bloodType;

    @Schema(description = "过敏史")
    private String allergies;

    @Schema(description = "既往病史")
    private String medicalHistory;

    @Schema(description = "家族病史")
    private String familyHistory;

    @Schema(description = "慢性病")
    private String chronicDiseases;

    @Schema(description = "用药情况")
    private String medications;

    @Schema(description = "家庭医生ID")
    private Long doctorId;

    @Schema(description = "健康管家ID")
    private Long healthManagerId;

    @Schema(description = "权益顾问ID")
    private Long benefitAdvisorId;

    @Schema(description = "保险顾问ID")
    private Long insuranceAdvisorId;

    @Schema(description = "状态: 0-禁用 1-正常")
    private Integer status;
}
