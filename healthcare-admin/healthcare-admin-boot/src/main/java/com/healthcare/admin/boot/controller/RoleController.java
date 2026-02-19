package com.healthcare.admin.boot.controller;

import com.healthcare.admin.boot.dto.RoleDTO;
import com.healthcare.admin.boot.service.RoleService;
import com.healthcare.admin.boot.vo.RoleVO;
import com.healthcare.admin.common.result.PageResult;
import com.healthcare.admin.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "角色管理", description = "系统角色CRUD操作")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @Operation(summary = "获取角色列表", description = "分页查询角色列表")
    public Result<PageResult<RoleVO>> getRoleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        PageResult<RoleVO> result = roleService.getRoleList(pageNum, pageSize, keyword);
        return Result.success(result);
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详情")
    public Result<RoleVO> getRoleById(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        RoleVO roleVO = roleService.getRoleById(roleId);
        return Result.success(roleVO);
    }

    @PostMapping
    @Operation(summary = "创建角色", description = "创建新角色")
    public Result<RoleVO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        log.info("创建角色: {}", roleDTO.getRoleName());
        RoleVO roleVO = roleService.createRole(roleDTO);
        return Result.success("创建成功", roleVO);
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public Result<RoleVO> updateRole(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @Valid @RequestBody RoleDTO roleDTO) {
        log.info("更新角色: {}", roleId);
        RoleVO roleVO = roleService.updateRole(roleId, roleDTO);
        return Result.success("更新成功", roleVO);
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色", description = "删除角色")
    public Result<Void> deleteRole(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        log.info("删除角色: {}", roleId);
        roleService.deleteRole(roleId);
        return Result.success("删除成功");
    }

    @GetMapping("/{roleId}/permissions")
    @Operation(summary = "获取角色权限", description = "获取角色的权限ID列表")
    public Result<List<Long>> getRolePermissions(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<Long> permissionIds = roleService.getRolePermissionIds(roleId);
        return Result.success(permissionIds);
    }

    @PutMapping("/{roleId}/permissions")
    @Operation(summary = "配置角色权限", description = "为角色分配权限")
    public Result<Void> assignPermissions(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @RequestBody List<Long> permissionIds) {
        log.info("配置角色权限: roleId={}, permissionCount={}", roleId, permissionIds != null ? permissionIds.size() : 0);
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success("权限配置成功");
    }
}
