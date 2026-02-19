package com.healthcare.admin.boot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Schema(description = "关键词回复VO")
public class KeywordReplyVO {

    @Schema(description = "回复ID")
    private Long replyId;

    @Schema(description = "社群ID")
    private Long groupId;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "匹配类型: 1-精确匹配 2-包含匹配 3-开头匹配 4-结尾匹配 5-正则匹配")
    private Integer matchType;

    @Schema(description = "回复内容")
    private String replyContent;

    @Schema(description = "内容类型: 1-文本 2-图片 3-链接 4-小程序")
    private Integer contentType;

    @Schema(description = "媒体URL")
    private String mediaUrl;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "生效开始时间")
    private LocalTime startTime;

    @Schema(description = "生效结束时间")
    private LocalTime endTime;

    @Schema(description = "触发次数限制")
    private Integer triggerLimit;

    @Schema(description = "状态: 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
