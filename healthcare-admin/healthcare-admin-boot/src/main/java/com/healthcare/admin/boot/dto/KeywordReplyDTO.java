package com.healthcare.admin.boot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "关键词回复DTO")
public class KeywordReplyDTO {

    @Schema(description = "回复ID")
    private Long replyId;

    @NotNull(message = "社群ID不能为空")
    @Schema(description = "社群ID", required = true)
    private Long groupId;

    @NotBlank(message = "关键词不能为空")
    @Schema(description = "关键词", required = true)
    private String keyword;

    @NotNull(message = "匹配类型不能为空")
    @Schema(description = "匹配类型: 1-精确匹配 2-包含匹配 3-开头匹配 4-结尾匹配 5-正则匹配", required = true)
    private Integer matchType;

    @NotBlank(message = "回复内容不能为空")
    @Schema(description = "回复内容", required = true)
    private String replyContent;

    @Schema(description = "内容类型: 1-文本 2-图片 3-链接 4-小程序")
    private Integer contentType;

    @Schema(description = "媒体URL")
    private String mediaUrl;

    @Schema(description = "优先级，数字越大优先级越高")
    private Integer priority;

    @Schema(description = "生效开始时间")
    private LocalTime startTime;

    @Schema(description = "生效结束时间")
    private LocalTime endTime;

    @Schema(description = "每人每天触发次数限制，0表示不限制")
    private Integer triggerLimit;

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;
}
