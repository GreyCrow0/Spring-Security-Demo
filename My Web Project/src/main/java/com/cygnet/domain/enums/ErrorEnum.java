package com.cygnet.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorEnum {

    // ========== 成功状态码 ==========
    SUCCESS(200, "操作成功"),

    // ========== 客户端错误 4xx ==========
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "请先登录"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    AUTHORITY_LIMIT(406, "没有访问权限"),

    // 业务相关错误

    USER_DISABLED(40002, "用户已被禁用"),
    USERNAME_OR_PASSWORD_ERROR(40003, "用户名或密码错误"),
    USERNAME_EXISTS(40004, "用户名已存在"),
    EMAIL_EXISTS(40005, "邮箱已存在"),
    VERIFICATION_CODE_ERROR(40006, "验证码错误"),
    OLD_PASSWORD_ERROR(40007, "原密码错误"),

    // ========== 服务端错误 5xx ==========
    SYSTEM_ERROR(500, "系统异常，请稍后重试"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 第三方服务错误
    THIRD_PARTY_SERVICE_ERROR(50001, "第三方服务异常"),
    FILE_UPLOAD_ERROR(50002, "文件上传失败"),

    // 数据库相关错误
    DATA_ACCESS_ERROR(50010, "数据访问异常"),
    DUPLICATE_KEY_ERROR(50011, "数据重复");

    private final Integer code;
    private final String desc;

    /**
     * 根据code获取枚举
     */
    public static ErrorEnum fromCode(Integer code) {
        for (ErrorEnum errorEnum : values()) {
            if (errorEnum.getCode().equals(code)) {
                return errorEnum;
            }
        }
        return SYSTEM_ERROR;
    }
}