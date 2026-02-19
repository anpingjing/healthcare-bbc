package com.healthcare.bbc.dto.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class GroupChatListResult {
    private Integer errcode;
    private String errmsg;
    
    @JsonProperty("group_chat_list")
    private List<GroupChatItem> groupChatList;
    
    @JsonProperty("next_cursor")
    private String nextCursor;
    
    @Data
    public static class GroupChatItem {
        @JsonProperty("chat_id")
        private String chatId;
        
        private Integer status;
        
        private String name;
    }
}
