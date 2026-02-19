package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.HealthMonitorData;

import java.time.LocalDateTime;
import java.util.List;

public interface HealthMonitorDataService extends IService<HealthMonitorData> {
    List<HealthMonitorData> getByUserId(Long userId);
    List<HealthMonitorData> getByUserIdAndDataType(Long userId, String dataType);
    List<HealthMonitorData> getByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
