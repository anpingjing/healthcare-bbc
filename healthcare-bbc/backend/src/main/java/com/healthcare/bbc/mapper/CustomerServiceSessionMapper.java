package com.healthcare.bbc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthcare.bbc.entity.CustomerServiceSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CustomerServiceSessionMapper extends BaseMapper<CustomerServiceSession> {

    @Select("SELECT * FROM customer_service_session WHERE wechat_external_user_id = #{externalUserId} AND status = 1 ORDER BY create_time DESC LIMIT 1")
    CustomerServiceSession selectActiveSession(@Param("externalUserId") String externalUserId);

    @Select("SELECT * FROM customer_service_session WHERE agent_id = #{agentId} AND status = 1")
    List<CustomerServiceSession> selectActiveSessionsByAgent(@Param("agentId") Long agentId);

    @Update("UPDATE customer_service_session SET status = 2, end_time = NOW() WHERE session_id = #{sessionId}")
    int closeSession(@Param("sessionId") Long sessionId);
}
