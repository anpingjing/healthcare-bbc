package com.healthcare.admin.boot.service;

import com.healthcare.admin.boot.dto.KeywordReplyDTO;
import com.healthcare.admin.boot.entity.GroupKeywordReply;
import com.healthcare.admin.boot.mapper.GroupKeywordReplyMapper;
import com.healthcare.admin.boot.service.impl.KeywordReplyServiceImpl;
import com.healthcare.admin.boot.vo.KeywordReplyVO;
import com.healthcare.admin.common.exception.BusinessException;
import com.healthcare.admin.common.result.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KeywordReplyServiceTest {

    @Mock
    private GroupKeywordReplyMapper keywordReplyMapper;

    @InjectMocks
    private KeywordReplyServiceImpl keywordReplyService;

    private GroupKeywordReply testReply;
    private KeywordReplyDTO replyDTO;

    @BeforeEach
    void setUp() {
        testReply = new GroupKeywordReply();
        testReply.setReplyId(1L);
        testReply.setGroupId(1L);
        testReply.setKeyword("血压");
        testReply.setMatchType(2);
        testReply.setReplyContent("正常血压范围：收缩压90-140mmHg");
        testReply.setContentType(1);
        testReply.setPriority(10);
        testReply.setStatus(1);

        replyDTO = new KeywordReplyDTO();
        replyDTO.setGroupId(1L);
        replyDTO.setKeyword("血压");
        replyDTO.setMatchType(2);
        replyDTO.setReplyContent("正常血压范围：收缩压90-140mmHg");
        replyDTO.setContentType(1);
        replyDTO.setPriority(10);
    }

    @Test
    void testGetReplyByIdSuccess() {
        when(keywordReplyMapper.selectById(1L)).thenReturn(testReply);

        KeywordReplyVO result = keywordReplyService.getReplyById(1L);

        assertNotNull(result);
        assertEquals("血压", result.getKeyword());
        assertEquals(2, result.getMatchType());
    }

    @Test
    void testGetReplyByIdNotFound() {
        when(keywordReplyMapper.selectById(1L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            keywordReplyService.getReplyById(1L);
        });
    }

    @Test
    void testCreateReplySuccess() {
        when(keywordReplyMapper.insert(any(GroupKeywordReply.class))).thenAnswer(invocation -> {
            GroupKeywordReply reply = invocation.getArgument(0);
            reply.setReplyId(1L);
            return 1;
        });

        KeywordReplyVO result = keywordReplyService.createReply(replyDTO);

        assertNotNull(result);
        assertEquals("血压", result.getKeyword());
    }

    @Test
    void testUpdateReplySuccess() {
        when(keywordReplyMapper.selectById(1L)).thenReturn(testReply);

        replyDTO.setReplyContent("更新后的内容");
        KeywordReplyVO result = keywordReplyService.updateReply(1L, replyDTO);

        assertNotNull(result);
        verify(keywordReplyMapper).updateById(any(GroupKeywordReply.class));
    }

    @Test
    void testDeleteReplySuccess() {
        when(keywordReplyMapper.selectById(1L)).thenReturn(testReply);

        keywordReplyService.deleteReply(1L);

        verify(keywordReplyMapper).deleteById(1L);
    }

    @Test
    void testUpdateStatus() {
        List<Long> replyIds = Arrays.asList(1L, 2L);
        when(keywordReplyMapper.selectById(anyLong())).thenReturn(testReply);

        keywordReplyService.updateStatus(replyIds, 0);

        verify(keywordReplyMapper, times(2)).updateById(any(GroupKeywordReply.class));
    }

    @Test
    void testMatchKeywordExactMatch() {
        GroupKeywordReply exactReply = new GroupKeywordReply();
        exactReply.setReplyId(1L);
        exactReply.setGroupId(1L);
        exactReply.setKeyword("血压");
        exactReply.setMatchType(1); // 精确匹配
        exactReply.setReplyContent("精确匹配回复");
        exactReply.setStatus(1);

        when(keywordReplyMapper.selectActiveByGroupId(1L)).thenReturn(Arrays.asList(exactReply));

        KeywordReplyVO result = keywordReplyService.matchKeyword(1L, "血压");

        assertNotNull(result);
        assertEquals("精确匹配回复", result.getReplyContent());
    }

    @Test
    void testMatchKeywordContainsMatch() {
        GroupKeywordReply containsReply = new GroupKeywordReply();
        containsReply.setReplyId(1L);
        containsReply.setGroupId(1L);
        containsReply.setKeyword("血压");
        containsReply.setMatchType(2); // 包含匹配
        containsReply.setReplyContent("包含匹配回复");
        containsReply.setStatus(1);

        when(keywordReplyMapper.selectActiveByGroupId(1L)).thenReturn(Arrays.asList(containsReply));

        KeywordReplyVO result = keywordReplyService.matchKeyword(1L, "我的血压正常吗");

        assertNotNull(result);
        assertEquals("包含匹配回复", result.getReplyContent());
    }

    @Test
    void testMatchKeywordNoMatch() {
        when(keywordReplyMapper.selectActiveByGroupId(1L)).thenReturn(Collections.emptyList());

        KeywordReplyVO result = keywordReplyService.matchKeyword(1L, "测试内容");

        assertNull(result);
    }

    @Test
    void testMatchKeywordRegexMatch() {
        GroupKeywordReply regexReply = new GroupKeywordReply();
        regexReply.setReplyId(1L);
        regexReply.setGroupId(1L);
        regexReply.setKeyword("预约.*医生");
        regexReply.setMatchType(5); // 正则匹配
        regexReply.setReplyContent("正则匹配回复");
        regexReply.setStatus(1);

        when(keywordReplyMapper.selectActiveByGroupId(1L)).thenReturn(Arrays.asList(regexReply));

        KeywordReplyVO result = keywordReplyService.matchKeyword(1L, "预约李医生");

        assertNotNull(result);
        assertEquals("正则匹配回复", result.getReplyContent());
    }
}
