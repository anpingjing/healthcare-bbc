package com.healthcare.bbc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthcare.bbc.entity.HealthMonitorData;
import com.healthcare.bbc.mapper.HealthMonitorDataMapper;
import com.healthcare.bbc.service.HealthMonitorDataService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HealthMonitorDataServiceImpl extends ServiceImpl<HealthMonitorDataMapper, HealthMonitorData> implements HealthMonitorDataService {
    @Override
    public List<HealthMonitorData> getByUserId(Long userId) {
        LambdaQueryWrapper<HealthMonitorData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthMonitorData::getUserId, userId);
        wrapper.orderByDesc(HealthMonitorData::getMeasureTime);
        return list(wrapper);
    }

    @Override
    public List<HealthMonitorData> getByUserIdAndDataType(Long userId, String dataType) {
        LambdaQueryWrapper<HealthMonitorData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthMonitorData::getUserId, userId);
        wrapper.eq(HealthMonitorData::getDataType, dataType);
        wrapper.orderByDesc(HealthMonitorData::getMeasureTime);
        return list(wrapper);
    }

    @Override
    public List<HealthMonitorData> getByUserIdAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<HealthMonitorData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthMonitorData::getUserId, userId);
        wrapper.between(HealthMonitorData::getMeasureTime, startTime, endTime);
        wrapper.orderByDesc(HealthMonitorData::getMeasureTime);
        return list(wrapper);
    }
}
