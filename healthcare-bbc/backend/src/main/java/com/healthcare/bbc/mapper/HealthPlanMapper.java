package com.healthcare.bbc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthcare.bbc.entity.HealthPlan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthPlanMapper extends BaseMapper<HealthPlan> {
}
