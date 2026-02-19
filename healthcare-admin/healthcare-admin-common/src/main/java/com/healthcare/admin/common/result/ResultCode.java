package com.healthcare.admin.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "success"),
    
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    
    PARAM_ERROR(1001, "参数错误"),
    PARAM_EMPTY(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_FORMAT_ERROR(1004, "参数格式错误"),
    
    USER_NOT_EXIST(2001, "用户不存在"),
    USER_ALREADY_EXIST(2002, "用户已存在"),
    USER_PASSWORD_ERROR(2003, "密码错误"),
    USER_DISABLED(2004, "用户已禁用"),
    USER_LOCKED(2005, "用户已锁定"),
    USER_EXPIRED(2006, "用户已过期"),
    
    ROLE_NOT_EXIST(2101, "角色不存在"),
    ROLE_ALREADY_EXIST(2102, "角色已存在"),
    ROLE_IN_USE(2103, "角色正在使用中"),
    
    PERMISSION_DENIED(2201, "权限不足"),
    PERMISSION_NOT_EXIST(2202, "权限不存在"),
    
    GROUP_NOT_EXIST(3001, "社群不存在"),
    GROUP_ALREADY_EXIST(3002, "社群已存在"),
    GROUP_MEMBER_FULL(3003, "社群成员已满"),
    GROUP_MEMBER_NOT_EXIST(3004, "成员不存在"),
    
    API_NOT_EXIST(4001, "API不存在"),
    API_ALREADY_EXIST(4002, "API已存在"),
    API_TEST_FAILED(4003, "API测试失败"),
    
    TOKEN_EXPIRED(5001, "Token已过期"),
    TOKEN_INVALID(5002, "Token无效"),
    TOKEN_REFRESH_FAILED(5003, "Token刷新失败"),
    
    FILE_UPLOAD_FAILED(6001, "文件上传失败"),
    FILE_DOWNLOAD_FAILED(6002, "文件下载失败"),
    FILE_NOT_EXIST(6003, "文件不存在"),
    FILE_SIZE_EXCEEDED(6004, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(6005, "文件类型不允许"),
    
    SYSTEM_ERROR(5000, "系统错误"),
    DATABASE_ERROR(5001, "数据库错误"),
    CACHE_ERROR(5002, "缓存错误"),
    NETWORK_ERROR(5003, "网络错误"),
    EXTERNAL_SERVICE_ERROR(5004, "外部服务错误"),
    RATE_LIMIT_EXCEEDED(5005, "请求过于频繁");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
