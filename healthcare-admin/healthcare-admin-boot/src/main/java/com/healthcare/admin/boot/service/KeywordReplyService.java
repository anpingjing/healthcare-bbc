package com.healthcare.admin.boot.service;

import com.healthcare.admin.boot.dto.KeywordReplyDTO;
import com.healthcare.admin.boot.vo.KeywordReplyVO;
import com.healthcare.admin.common.result.PageResult;

import java.util.List;

public interface KeywordReplyService {

    /**
     * 分页查询关键词回复列表
     */
    PageResult<KeywordReplyVO> getReplyList(Long groupId, Integer pageNum, Integer pageSize);

    /**
     * 获取关键词回复详情
     */
    KeywordReplyVO getReplyById(Long replyId);

    /**
     * 创建关键词回复
     */
    KeywordReplyVO createReply(KeywordReplyDTO dto);

    /**
     * 更新关键词回复
     */
    KeywordReplyVO updateReply(Long replyId, KeywordReplyDTO dto);

    /**
     * 删除关键词回复
     */
    void deleteReply(Long replyId);

    /**
     * 批量更新状态
     */
    void updateStatus(List<Long> replyIds, Integer status);

    /**
     * 匹配关键词并返回回复内容
     */
    KeywordReplyVO matchKeyword(Long groupId, String content);
}
