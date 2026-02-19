package com.healthcare.admin.boot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("group_info")
public class GroupInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long groupId;

    private String groupName;

    private Integer groupType;

    private String chatId;

    private Long ownerId;

    private String description;

    private String avatar;

    private Integer maxMembers;

    private Integer memberCount;

    private Long doctorId;

    private Long healthManagerId;

    private Long benefitAdvisorId;

    private Long insuranceAdvisorId;

    private Integer joinType;

    private String welcomeMsg;

    private Long welcomeFormId;

    private Integer muteType;

    private LocalTime muteStartTime;

    private LocalTime muteEndTime;

    private Integer adFilter;

    private String sensitiveWords;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
