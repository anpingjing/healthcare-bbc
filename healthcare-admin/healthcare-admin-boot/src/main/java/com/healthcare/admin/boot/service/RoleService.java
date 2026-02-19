package com.healthcare.admin.boot.service;

import com.healthcare.admin.boot.dto.RoleDTO;
import com.healthcare.admin.boot.vo.RoleVO;
import com.healthcare.admin.common.result.PageResult;

import java.util.List;

public interface RoleService {

    /**
     * 分页查询角色列表
     */
    PageResult<RoleVO> getRoleList(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 获取角色详情
     */
    RoleVO getRoleById(Long roleId);

    /**
     * 创建角色
     */
    RoleVO createRole(RoleDTO roleDTO);

    /**
     * 更新角色
     */
    RoleVO updateRole(Long roleId, RoleDTO roleDTO);

    /**
     * 删除角色
     */
    void deleteRole(Long roleId);

    /**
     * 获取用户的角色列表
     */
    List<RoleVO> getRolesByUserId(Long userId);

    /**
     * 配置角色权限
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
}
