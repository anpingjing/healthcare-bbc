package com.healthcare.bbc.dto.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ExternalContactInfo {
    private Integer errcode;
    private String errmsg;
    
    @JsonProperty("external_contact")
    private ExternalContact externalContact;
    
    @JsonProperty("follow_user")
    private List<FollowUser> followUser;
    
    @Data
    public static class ExternalContact {
        @JsonProperty("external_userid")
        private String externalUserid;
        
        private String name;
        
        private Integer position;
        
        private String avatar;
        
        @JsonProperty("corp_name")
        private String corpName;
        
        private Integer type;
        
        private Integer gender;
        
        private String unionid;
        
        @JsonProperty("provincial")
        private String provincial;
        
        private String city;
    }
    
    @Data
    public static class FollowUser {
        private String userid;
        
        private String remark;
        
        private String description;
        
        @JsonProperty("createtime")
        private Long createTime;
        
        private String state;
        
        @JsonProperty("remark_corp_name")
        private String remarkCorpName;
        
        @JsonProperty("remark_mobiles")
        private List<String> remarkMobiles;
        
        private List<Tag> tags;
        
        @Data
        public static class Tag {
            @JsonProperty("group_name")
            private String groupName;
            
            private String name;
            
            private Integer type;
        }
    }
}
