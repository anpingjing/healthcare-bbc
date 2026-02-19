package com.healthcare.admin.boot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthcare.admin.boot.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.deleted = 0 AND r.status = 1")
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    @Select("SELECT r.* FROM sys_role r WHERE r.role_code = #{roleCode} AND r.deleted = 0")
    SysRole selectByRoleCode(@Param("roleCode") String roleCode);
}
