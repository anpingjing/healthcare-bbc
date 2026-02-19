package com.healthcare.admin.boot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "角色VO")
public class RoleVO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色类型: 1-系统预设 2-自定义")
    private Integer roleType;

    @Schema(description = "数据范围: 1-全部 2-部门 3-个人 4-自定义")
    private Integer dataScope;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
