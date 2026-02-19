package com.healthcare.admin.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.admin.boot.dto.RoleDTO;
import com.healthcare.admin.boot.entity.SysRole;
import com.healthcare.admin.boot.entity.SysRolePermission;
import com.healthcare.admin.boot.entity.SysUserRole;
import com.healthcare.admin.boot.mapper.SysRoleMapper;
import com.healthcare.admin.boot.mapper.SysRolePermissionMapper;
import com.healthcare.admin.boot.mapper.SysUserRoleMapper;
import com.healthcare.admin.boot.service.RoleService;
import com.healthcare.admin.boot.vo.RoleVO;
import com.healthcare.admin.common.exception.BusinessException;
import com.healthcare.admin.common.result.PageResult;
import com.healthcare.admin.common.result.ResultCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRolePermissionMapper rolePermissionMapper;

    @Override
    public PageResult<RoleVO> getRoleList(Integer pageNum, Integer pageSize, String keyword) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysRole::getRoleName, keyword)
                   .or()
                   .like(SysRole::getRoleCode, keyword);
        }
        
        wrapper.eq(SysRole::getDeleted, 0)
               .orderByAsc(SysRole::getSortOrder);
        
        Page<SysRole> rolePage = roleMapper.selectPage(page, wrapper);
        
        List<RoleVO> roleVOList = rolePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageResult.of(roleVOList, rolePage.getTotal(), (long) pageNum, (long) pageSize);
    }

    @Override
    public RoleVO getRoleById(Long roleId) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        
        RoleVO roleVO = convertToVO(role);
        // 查询角色权限
        List<Long> permissionIds = getRolePermissionIds(roleId);
        roleVO.setPermissionIds(permissionIds);
        
        return roleVO;
    }

    @Override
    @Transactional
    public RoleVO createRole(RoleDTO roleDTO) {
        // 检查角色编码是否已存在
        SysRole existingRole = roleMapper.selectByRoleCode(roleDTO.getRoleCode());
        if (existingRole != null) {
            throw new BusinessException(ResultCode.ROLE_ALREADY_EXIST);
        }
        
        SysRole role = new SysRole();
        BeanUtils.copyProperties(roleDTO, role);
        role.setStatus(1);
        
        roleMapper.insert(role);
        
        // 如果有权限配置，保存权限
        if (roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(role.getRoleId(), roleDTO.getPermissionIds());
        }
        
        return convertToVO(role);
    }

    @Override
    @Transactional
    public RoleVO updateRole(Long roleId, RoleDTO roleDTO) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        
        // 系统预设角色不允许修改编码
        if (role.getRoleType() == 1 && !role.getRoleCode().equals(roleDTO.getRoleCode())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "系统预设角色不允许修改编码");
        }
        
        // 检查新编码是否与其他角色冲突
        if (!role.getRoleCode().equals(roleDTO.getRoleCode())) {
            SysRole existingRole = roleMapper.selectByRoleCode(roleDTO.getRoleCode());
            if (existingRole != null) {
                throw new BusinessException(ResultCode.ROLE_ALREADY_EXIST);
            }
        }
        
        BeanUtils.copyProperties(roleDTO, role);
        role.setRoleId(roleId);
        
        roleMapper.updateById(role);
        
        // 更新权限
        if (roleDTO.getPermissionIds() != null) {
            assignPermissions(roleId, roleDTO.getPermissionIds());
        }
        
        return getRoleById(roleId);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        SysRole role = roleMapper.selectById(roleId);
        if (role == null || role.getDeleted() == 1) {
            throw new BusinessException(ResultCode.ROLE_NOT_EXIST);
        }
        
        // 系统预设角色不允许删除
        if (role.getRoleType() == 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "系统预设角色不允许删除");
        }
        
        // 检查角色是否被用户使用
        Long count = userRoleMapper.selectCount(
            new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, roleId)
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.ROLE_IN_USE);
        }
        
        roleMapper.deleteById(roleId);
        
        // 删除角色权限关联
        rolePermissionMapper.delete(
            new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId)
        );
    }

    @Override
    public List<RoleVO> getRolesByUserId(Long userId) {
        List<SysRole> roles = roleMapper.selectRolesByUserId(userId);
        return roles.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除原有权限
        rolePermissionMapper.delete(
            new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId)
        );
        
        // 插入新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                SysRolePermission rolePermission = new SysRolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermissionMapper.insert(rolePermission);
            }
        }
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    private RoleVO convertToVO(SysRole role) {
        RoleVO roleVO = new RoleVO();
        BeanUtils.copyProperties(role, roleVO);
        return roleVO;
    }
}
