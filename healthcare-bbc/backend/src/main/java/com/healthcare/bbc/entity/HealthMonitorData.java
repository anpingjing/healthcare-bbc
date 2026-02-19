package com.healthcare.bbc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("health_monitor_data")
public class HealthMonitorData {
    @TableId(type = IdType.AUTO)
    private Long dataId;
    private Long userId;
    private String dataType;
    private BigDecimal dataValue;
    private String dataUnit;
    private LocalDateTime measureTime;
    private String deviceType;
    private String deviceId;
    private LocalDateTime createTime;
}
