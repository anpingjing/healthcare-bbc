package com.healthcare.bbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("health_record")
public class HealthRecord {
    @TableId(type = IdType.AUTO)
    private Long recordId;
    private Long userId;
    private BigDecimal height;
    private BigDecimal weight;
    private String bloodType;
    private String medicalHistory;
    private String allergyHistory;
    private String familyHistory;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
