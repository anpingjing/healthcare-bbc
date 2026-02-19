package com.healthcare.admin.boot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "角色DTO")
public class RoleDTO {

    @Schema(description = "角色ID")
    private Long roleId;

    @NotBlank(message = "角色编码不能为空")
    @Schema(description = "角色编码", required = true)
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    @Schema(description = "角色名称", required = true)
    private String roleName;

    @NotNull(message = "角色类型不能为空")
    @Schema(description = "角色类型: 1-系统预设 2-自定义", required = true)
    private Integer roleType;

    @Schema(description = "数据范围: 1-全部 2-部门 3-个人 4-自定义")
    private Integer dataScope;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;
}
