package cn.skstudio.exception

import cn.skstudio.`interface`.ResponseDataInterface
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
enum class ServiceErrorEnum(override val code: Int, override val msg: String, override var data: Any?=null) : ResponseDataInterface {

    //无异常
    NO_ERROR(0, "OK"),

    //未指名的异常
    UNSPECIFIED(90001, "未指名的错误"),
    UNKNOWN(90002, "未知错误"),
    AN_EXCEPTION_OCCURRED(90003, "发生了一个异常"),

    //请求异常
    UNKNOWN_REQUEST(400, "未知的请求"),
    FILE_NOT_EXISTS(404, "文件不存在"),
    NO_CONTENT(204, "无内容可显示"),
    INVALID_DIR(10001, "无效的文件夹"),
    INVALID_FILE(10002, "无效的文件"),
    NOT_DIR(10003, "不是文件夹"),
    NOT_FILE(10004, "不是文件"),
    NOT_MULTIPART_FROM_DATA(10005, "不是multipart/form-data请求"),
    INSUFFICIENT_PARAMETERS(10006, "需要的参数不足"),
    USER_ID_NOT_EXIST(10007, "用户ID不存在"),
    ID_INVALID(10008, "无效的ID"),
    REQUEST_FORBIDDEN(10009, "请求被禁止"),

    /*用户信息异常*/

    //用户名
    USERNAME_EXIST(61000, "用户名已存在"),
    USERNAME_TOO_LONG(61001, "用户名太长"),
    USERNAME_EMPTY(61002, "用户名为空"),
    USERNAME_CONTAINS_SP_CHAR(61003, "用户名含有非法字符"),
    USERNAME_ALL_DIGITAL(61004, "用户名不能为纯数字"),

    //昵称
    NICKNAME_TOO_LONG(61100, "昵称太长"),
    NICKNAME_EMPTY(61101, "昵称为空"),

    //密码
    WEAK_PASSWORD(62000, "密码强度太弱"),
    PASSWORD_TOO_LONG(62001, "密码太长"),

    //邮箱
    WRONG_EMAIL(63000, "错误的邮箱"),
    EMAIL_USED(63001, "邮箱已被使用"),

    //性别
    UNKNOWN_SEX(64000, "未知性别"),
    GROUP_NOT_EXISTS(65000, "组不存在"),
    GROUP_NAME_TOO_LONG(65001, "组名太长"),
    GROUP_NOT_ALLOW(65002, "组操作不被允许"),

    //签名异常
    SIGNATURE_TOO_LONG(65001, "签名太长"),

    /* 激活异常 */
    ACTIVATE_TOKEN_EXPIRE(65001, "验证已过期"),
    ACTIVATE_TOKEN_INVALID(65002, "验证无效"),
    ACTIVATE_UNKNOWN_EXCEPTION(65003, "系统内部错误"),

    //登录异常
    USERNAME_NOT_EXISTS(30001, "用户名不存在"),
    WRONG_PASSWORD(30002, "密码错误"),
    EXPIRED_LOGIN(30003, "登录状态已过期"),
    NULL_USERNAME(30004, "用户名为空"),
    NULL_PASSWORD(30005, "密码为空"),
    UNTRUSTED_IP(30006, "不受信任的IP"),
    NOT_LOGGED_IN(30007, "未登录"),
    HAD_LOGGED_IN(30008, "已有登录用户"),
    NEED_VERIFY(30009, "请求的权限需要验证"),
    WRONG_CAPTCHA(30010, "验证码错误"),
    NULL_CAPTCHA(30011, "验证码为空"),
    UNKNOWN_PASSWORD(30012, "未知的密码串"),
    ACCOUNT_NOT_ACTIVATED(30013, "账户尚未激活"),


    //资源异常
    RESOURCE_NOT_FOUND(80001, "资源不存在"),
    RESOURCE_NOT_ALLOWED(80002, "资源不可访问"),
    RESOURCE_TOO_BIG(80003, "资源太大"),
    FILE_EMPTY(80004, "文件为空"),
    RESOURCE_TYPE_MISMATCH(80005, "资源类型不匹配"),

    //消息体异常
    MESSAGE_NOT_EXIST(70001, "消息不存在"),
    MESSAGE_NOT_ALLOWED(70002, "消息不被允许"),
    MESSAGE_WRONG_FORMAT(70003, "消息格式错误"),


    //服务器异常
    IO_EXCEPTION(50001, "IO出现错误"),
    INITIALIZE_FAILED(50003, "初始化系统出现错误"),
    TYPE_MISMATCH(50004, "类型不匹配"),
    INVALID_TYPE(50005, "无效类型"),

    ;

    fun data(data: Any?): ServiceErrorEnum {
        this.data = data
        return this
    }

    @JsonIgnore
    val ok = code == 0

    @JsonIgnore
    val bad = code != 0

    fun throwout(): Nothing = throwout(this)

    companion object {
        fun throwout(error: ServiceErrorEnum):Nothing{
            throw SKException(error)
        }

        fun fromException(e: Exception): ServiceErrorEnum {
            lateinit var expMessage: String
            ByteArrayOutputStream().use {
                e.printStackTrace(PrintWriter(it, true))
                expMessage = it.toString()
            }
            return AN_EXCEPTION_OCCURRED.data(expMessage)
        }
    }
}