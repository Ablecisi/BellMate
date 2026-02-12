package com.bellmate.constant;

/**
 * ailianlian
 * com.ailianlian.ablecisi.constant
 * LoginStatusCodeConstant <br>
 * 登录状态码常量类
 *
 * @author Ablecisi
 * @version 1.0
 * 2025/6/15
 * 星期日
 * 18:07
 */
public enum StatusCodeConstant {
    SUCCESS(200, "成功"),
    FAILURE(500, "失败"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    CONFLICT(409, "资源冲突"),
    UNPROCESSABLE_ENTITY(422, "参数校验失败"),
    TOO_MANY_REQUESTS(429, "请求过多"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    NETWORK_ERROR(501, "网络错误"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    INVALID_CREDENTIALS(1001, "凭证无效"),
    TOKEN_EXPIRED(1002, "Token过期"),
    USER_NOT_EXIST(1003, "用户不存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    ACCOUNT_LOCKED(1005, "账户被锁定"),
    PHONE_ALREADY_EXISTS(1007, "手机号已存在"),
    VERIFICATION_CODE_ERROR(1008, "验证码错误"),
    OPERATION_NOT_ALLOWED(1009, "操作不被允许"),
    SQL_ERROR(600, "SQL异常"),
    FILE_UPLOAD_ERROR(700, "文件上传异常"),
    FILE_DOWNLOAD_ERROR(701, "文件下载异常"),
    THIRD_PARTY_SERVICE_ERROR(800, "第三方服务异常"),
    BUSINESS_ERROR(900, "其他业务错误");

    private final int code;
    private final String msg;

    StatusCodeConstant(int i, String msg) {
        this.code = i;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
