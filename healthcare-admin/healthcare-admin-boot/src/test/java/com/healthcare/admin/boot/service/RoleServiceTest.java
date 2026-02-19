package com.healthcare.admin.boot.service;

import com.healthcare.admin.boot.dto.RoleDTO;
import com.healthcare.admin.boot.entity.SysRole;
import com.healthcare.admin.boot.mapper.SysRoleMapper;
import com.healthcare.admin.boot.mapper.SysRolePermissionMapper;
import com.healthcare.admin.boot.mapper.SysUserRoleMapper;
import com.healthcare.admin.boot.service.impl.RoleServiceImpl;
import com.healthcare.admin.boot.vo.RoleVO;
import com.healthcare.admin.common.exception.BusinessException;
import com.healthcare.admin.common.result.PageResult;
import com.healthcare.admin.common.result.ResultCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Mock
    private SysRolePermissionMapper rolePermissionMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private SysRole testRole;
    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        testRole = new SysRole();
        testRole.setRoleId(1L);
        testRole.setRoleCode("ROLE_TEST");
        testRole.setRoleName("测试角色");
        testRole.setRoleType(2);
        testRole.setDataScope(1);
        testRole.setStatus(1);

        roleDTO = new RoleDTO();
        roleDTO.setRoleCode("ROLE_TEST");
        roleDTO.setRoleName("测试角色");
        roleDTO.setRoleType(2);
        roleDTO.setDataScope(1);
        roleDTO.setStatus(1);
    }

    @Test
    void testGetRoleByIdSuccess() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(rolePermissionMapper.selectPermissionIdsByRoleId(1L)).thenReturn(Arrays.asList(1L, 2L, 3L));

        // When
        RoleVO result = roleService.getRoleById(1L);

        // Then
        assertNotNull(result);
        assertEquals("ROLE_TEST", result.getRoleCode());
        assertEquals("测试角色", result.getRoleName());
        assertNotNull(result.getPermissionIds());
        assertEquals(3, result.getPermissionIds().size());
    }

    @Test
    void testGetRoleByIdNotExist() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.getRoleById(1L);
        });

        assertEquals(ResultCode.ROLE_NOT_EXIST.getCode(), exception.getCode());
    }

    @Test
    void testCreateRoleSuccess() {
        // Given
        when(roleMapper.selectByRoleCode("ROLE_TEST")).thenReturn(null);
        when(roleMapper.insert(any(SysRole.class))).thenAnswer(invocation -> {
            SysRole role = invocation.getArgument(0);
            role.setRoleId(1L);
            return 1;
        });

        // When
        RoleVO result = roleService.createRole(roleDTO);

        // Then
        assertNotNull(result);
        assertEquals("ROLE_TEST", result.getRoleCode());
        assertEquals("测试角色", result.getRoleName());
        verify(roleMapper).insert(any(SysRole.class));
    }

    @Test
    void testCreateRoleAlreadyExist() {
        // Given
        when(roleMapper.selectByRoleCode("ROLE_TEST")).thenReturn(testRole);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.createRole(roleDTO);
        });

        assertEquals(ResultCode.ROLE_ALREADY_EXIST.getCode(), exception.getCode());
    }

    @Test
    void testUpdateRoleSuccess() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(roleMapper.selectByRoleCode("ROLE_TEST")).thenReturn(testRole);

        // When
        RoleVO result = roleService.updateRole(1L, roleDTO);

        // Then
        assertNotNull(result);
        verify(roleMapper).updateById(any(SysRole.class));
    }

    @Test
    void testDeleteRoleSuccess() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(userRoleMapper.selectCount(any())).thenReturn(0L);

        // When
        roleService.deleteRole(1L);

        // Then
        verify(roleMapper).deleteById(1L);
        verify(rolePermissionMapper).delete(any());
    }

    @Test
    void testDeleteSystemRole() {
        // Given
        testRole.setRoleType(1);
        when(roleMapper.selectById(1L)).thenReturn(testRole);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.deleteRole(1L);
        });

        assertEquals(ResultCode.PARAM_ERROR.getCode(), exception.getCode());
    }

    @Test
    void testDeleteRoleInUse() {
        // Given
        when(roleMapper.selectById(1L)).thenReturn(testRole);
        when(userRoleMapper.selectCount(any())).thenReturn(5L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            roleService.deleteRole(1L);
        });

        assertEquals(ResultCode.ROLE_IN_USE.getCode(), exception.getCode());
    }

    @Test
    void testAssignPermissions() {
        // Given
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);

        // When
        roleService.assignPermissions(1L, permissionIds);

        // Then
        verify(rolePermissionMapper).delete(any());
        verify(rolePermissionMapper, times(3)).insert(any());
    }
}
