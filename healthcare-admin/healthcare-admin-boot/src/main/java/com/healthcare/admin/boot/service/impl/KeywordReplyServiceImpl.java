package com.healthcare.admin.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthcare.admin.boot.dto.KeywordReplyDTO;
import com.healthcare.admin.boot.entity.GroupKeywordReply;
import com.healthcare.admin.boot.mapper.GroupKeywordReplyMapper;
import com.healthcare.admin.boot.service.KeywordReplyService;
import com.healthcare.admin.boot.vo.KeywordReplyVO;
import com.healthcare.admin.common.exception.BusinessException;
import com.healthcare.admin.common.result.PageResult;
import com.healthcare.admin.common.result.ResultCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class KeywordReplyServiceImpl implements KeywordReplyService {

    @Autowired
    private GroupKeywordReplyMapper keywordReplyMapper;

    @Override
    public PageResult<KeywordReplyVO> getReplyList(Long groupId, Integer pageNum, Integer pageSize) {
        Page<GroupKeywordReply> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GroupKeywordReply> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(GroupKeywordReply::getGroupId, groupId)
               .eq(GroupKeywordReply::getDeleted, 0)
               .orderByDesc(GroupKeywordReply::getPriority)
               .orderByDesc(GroupKeywordReply::getCreateTime);
        
        Page<GroupKeywordReply> replyPage = keywordReplyMapper.selectPage(page, wrapper);
        
        List<KeywordReplyVO> voList = replyPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageResult.of(voList, replyPage.getTotal(), (long) pageNum, (long) pageSize);
    }

    @Override
    public KeywordReplyVO getReplyById(Long replyId) {
        GroupKeywordReply reply = keywordReplyMapper.selectById(replyId);
        if (reply == null || reply.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "关键词回复不存在");
        }
        return convertToVO(reply);
    }

    @Override
    @Transactional
    public KeywordReplyVO createReply(KeywordReplyDTO dto) {
        GroupKeywordReply reply = new GroupKeywordReply();
        BeanUtils.copyProperties(dto, reply);
        reply.setStatus(1);
        
        keywordReplyMapper.insert(reply);
        return convertToVO(reply);
    }

    @Override
    @Transactional
    public KeywordReplyVO updateReply(Long replyId, KeywordReplyDTO dto) {
        GroupKeywordReply reply = keywordReplyMapper.selectById(replyId);
        if (reply == null || reply.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "关键词回复不存在");
        }
        
        BeanUtils.copyProperties(dto, reply);
        reply.setReplyId(replyId);
        
        keywordReplyMapper.updateById(reply);
        return convertToVO(reply);
    }

    @Override
    @Transactional
    public void deleteReply(Long replyId) {
        GroupKeywordReply reply = keywordReplyMapper.selectById(replyId);
        if (reply == null || reply.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "关键词回复不存在");
        }
        keywordReplyMapper.deleteById(replyId);
    }

    @Override
    @Transactional
    public void updateStatus(List<Long> replyIds, Integer status) {
        for (Long replyId : replyIds) {
            GroupKeywordReply reply = keywordReplyMapper.selectById(replyId);
            if (reply != null) {
                reply.setStatus(status);
                keywordReplyMapper.updateById(reply);
            }
        }
    }

    @Override
    public KeywordReplyVO matchKeyword(Long groupId, String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        
        List<GroupKeywordReply> replies = keywordReplyMapper.selectActiveByGroupId(groupId);
        LocalTime now = LocalTime.now();
        
        for (GroupKeywordReply reply : replies) {
            // 检查生效时间
            if (reply.getStartTime() != null && reply.getEndTime() != null) {
                if (now.isBefore(reply.getStartTime()) || now.isAfter(reply.getEndTime())) {
                    continue;
                }
            }
            
            // 匹配关键词
            boolean matched = false;
            switch (reply.getMatchType()) {
                case 1: // 精确匹配
                    matched = content.equals(reply.getKeyword());
                    break;
                case 2: // 包含匹配
                    matched = content.contains(reply.getKeyword());
                    break;
                case 3: // 开头匹配
                    matched = content.startsWith(reply.getKeyword());
                    break;
                case 4: // 结尾匹配
                    matched = content.endsWith(reply.getKeyword());
                    break;
                case 5: // 正则匹配
                    try {
                        matched = Pattern.matches(reply.getKeyword(), content);
                    } catch (Exception e) {
                        // 正则表达式错误，跳过
                    }
                    break;
            }
            
            if (matched) {
                return convertToVO(reply);
            }
        }
        
        return null;
    }

    private KeywordReplyVO convertToVO(GroupKeywordReply reply) {
        KeywordReplyVO vo = new KeywordReplyVO();
        BeanUtils.copyProperties(reply, vo);
        return vo;
    }
}
