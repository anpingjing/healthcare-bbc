package com.healthcare.bbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("insurance_policy")
public class InsurancePolicy {
    @TableId(type = IdType.AUTO)
    private Long policyId;
    private String policyNo;
    private Long userId;
    private Long productId;
    private BigDecimal premiumAmount;
    private BigDecimal coverageAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String paymentPeriod;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
