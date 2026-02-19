package com.healthcare.bbc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthcare.bbc.entity.CustomerMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerMessageMapper extends BaseMapper<CustomerMessage> {

    @Select("SELECT * FROM customer_message WHERE wechat_external_user_id = #{externalUserId} ORDER BY create_time DESC LIMIT #{limit}")
    List<CustomerMessage> selectRecentMessages(@Param("externalUserId") String externalUserId, @Param("limit") int limit);

    @Select("SELECT * FROM customer_message WHERE wechat_external_user_id = #{externalUserId} AND create_time > #{startTime} ORDER BY create_time ASC")
    List<CustomerMessage> selectMessagesByTimeRange(@Param("externalUserId") String externalUserId, @Param("startTime") String startTime);
}
