package com.healthcare.admin.boot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthcare.admin.boot.entity.HealthProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface HealthProfileMapper extends BaseMapper<HealthProfile> {

    @Select("SELECT * FROM health_profile WHERE user_id = #{userId} AND deleted = 0")
    HealthProfile selectByUserId(@Param("userId") Long userId);

    @Select("SELECT health_level, COUNT(*) as count FROM health_profile WHERE deleted = 0 GROUP BY health_level")
    List<Map<String, Object>> countByHealthLevel();

    @Select("SELECT COUNT(*) FROM health_profile WHERE doctor_id = #{doctorId} AND deleted = 0")
    Long countByDoctorId(@Param("doctorId") Long doctorId);
}
