package com.healthcare.bbc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthcare.bbc.entity.HealthRecord;

public interface HealthRecordService extends IService<HealthRecord> {
    HealthRecord getByUserId(Long userId);
}
