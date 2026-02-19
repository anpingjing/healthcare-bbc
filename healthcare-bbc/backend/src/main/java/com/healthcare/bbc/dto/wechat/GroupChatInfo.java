package com.healthcare.bbc.dto.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class GroupChatInfo {
    @JsonProperty("chat_id")
    private String chatId;
    
    private String name;
    
    private String owner;
    
    @JsonProperty("member_list")
    private List<GroupMember> memberList;
    
    @JsonProperty("create_time")
    private Long createTime;
    
    private String notice;
    
    @JsonProperty("admin_list")
    private List<String> adminList;
    
    @Data
    public static class GroupMember {
        @JsonProperty("userid")
        private String userId;
        
        private Integer type;
        
        @JsonProperty("join_time")
        private Long joinTime;
        
        @JsonProperty("join_scene")
        private Integer joinScene;
        
        @JsonProperty("invitor")
        private Invitor invitor;
        
        @Data
        public static class Invitor {
            @JsonProperty("userid")
            private String userId;
        }
    }
}
