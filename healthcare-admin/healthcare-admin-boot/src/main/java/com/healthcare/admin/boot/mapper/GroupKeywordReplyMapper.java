package com.healthcare.admin.boot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthcare.admin.boot.entity.GroupKeywordReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupKeywordReplyMapper extends BaseMapper<GroupKeywordReply> {

    @Select("SELECT * FROM group_keyword_reply WHERE group_id = #{groupId} AND status = 1 AND deleted = 0 ORDER BY priority DESC")
    List<GroupKeywordReply> selectActiveByGroupId(@Param("groupId") Long groupId);
}
