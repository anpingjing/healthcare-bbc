package com.healthcare.bbc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthcare.bbc.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
