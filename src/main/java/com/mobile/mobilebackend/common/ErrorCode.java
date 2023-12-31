package com.mobile.mobilebackend.common;

public enum ErrorCode {
    SUCCESS(0,"ok",""),
    PARAM_ERROR(40000,"请求参数错误",""),
    NO_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"没权限", ""),
    ACCOUNT_SAME(40102,"重复性错误", ""),
    PARAM_NULL(40001,"请求得到的数据不存在",""),
    VERIFY_ERROR(40103,"验证不成功",""),
    SYSTEM_ERROR(50000, "系统内部异常", ""),
    NOT_FOUND_ERROR(40400, "请求数据不存在", "");
    private int code;
    private String message;
    private String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
