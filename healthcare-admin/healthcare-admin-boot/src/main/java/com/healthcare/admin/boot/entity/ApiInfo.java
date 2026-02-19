package com.healthcare.admin.boot.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("api_info")
public class ApiInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long apiId;

    private String apiCode;

    private String apiName;

    private Integer apiCategory;

    private String apiVersion;

    private String httpMethod;

    private String apiPath;

    private String contentType;

    private String description;

    private String requestExample;

    private String responseExample;

    private String wechatApiType;

    private Integer rateLimit;

    private String officialDocUrl;

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
