package com.healthcare.bbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("insurance_product")
public class InsuranceProduct {
    @TableId(type = IdType.AUTO)
    private Long productId;
    private String productCode;
    private String productName;
    private String productType;
    private Integer minAge;
    private Integer maxAge;
    private BigDecimal minPremium;
    private BigDecimal maxPremium;
    private BigDecimal coverageAmount;
    private String coveragePeriod;
    private String paymentPeriod;
    private String healthRequirements;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
}
