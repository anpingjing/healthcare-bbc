package com.healthcare.bbc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthcare.bbc.entity.AiIntent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiIntentMapper extends BaseMapper<AiIntent> {

    @Select("SELECT * FROM ai_intent WHERE status = 1 ORDER BY priority DESC")
    List<AiIntent> selectActiveIntents();

    @Select("SELECT * FROM ai_intent WHERE intent_category = #{category} AND status = 1")
    List<AiIntent> selectByCategory(@Param("category") String category);
}
