package cn.skstudio.exception

import cn.skstudio.`interface`.ResponseDataInterface
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONType
import com.alibaba.fastjson.serializer.SerializerFeature
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

@JSONType(serializeEnumAsJavaBean = true)
enum class ServiceErrorEnum(override val code: Int, override val msg: String, override var data: Any?) : ResponseDataInterface {

    //无异常
    NO_ERROR(0, "OK", null),
    //未指名的异常
    UNSPECIFIED(90001, "未指名的错误", null),
    UNKNOWN(90002, "未知错误", null),
    AN_EXCEPTION_OCCURRED(90003, "发生了一个异常", null),
    //请求异常
    UNKNOWN_REQUEST(400, "未知的请求", null),
    FILE_NOT_EXISTS(404, "文件不存在", null),
    NO_CONTENT(204, "无内容可显示", null),
    INVALID_DIR(10001, "无效的文件夹", null),
    INVALID_FILE(10002, "无效的文件", null),
    NOT_DIR(10003, "不是文件夹", null),
    NOT_FILE(10004, "不是文件", null),
    NOT_MULTIPART_FROM_DATA(10005, "不是multipart/form-data请求", null),
    INSUFFICIENT_PARAMETERS(10006, "需要的参数不足", null),
    USER_ID_NOT_EXIST(10007, "用户ID不存在", null),
    ID_INVALID(10008, "无效的ID", null),

    /*用户信息异常*/

    //用户名
    USERNAME_EXIST(61000, "用户名已存在", null),
    USERNAME_TOO_LONG(61001, "用户名太长", null),
    USERNAME_EMPTY(61002, "用户名为空", null),
    USERNAME_CONTAINS_SP_CHAR(61003, "用户名含有非法字符", null),
    USERNAME_ALL_DIGITAL(61004, "用户名不能为纯数字", null),
    //昵称
    NICKNAME_TOO_LONG(61100, "昵称太长", null),
    NICKNAME_EMPTY(61101, "昵称为空", null),
    //密码
    WEAK_PASSWORD(62000, "密码强度太弱", null),
    PASSWORD_TOO_LONG(62001, "密码太长", null),
    //邮箱
    WRONG_EMAIL(63000, "错误的邮箱", null),
    EMAIL_USED(63001, "邮箱已被使用", null),
    //性别
    UNKNOWN_SEX(64000, "未知性别", null),
    GROUP_NOT_EXISTS(65000, "组不存在", null),
    GROUP_NAME_TOO_LONG(65001, "组名太长", null),
    GROUP_NOT_ALLOW(65002, "组操作不被允许", null),

    //签名异常
    SIGNATURE_TOO_LONG(65001, "签名太长", null),

    /* 激活异常 */
    ACTIVATE_TOKEN_EXPIRE(65001, "验证已过期", null),
    ACTIVATE_TOKEN_INVALID(65002, "验证无效", null),
    ACTIVATE_UNKNOWN_EXCEPTION(65003, "系统内部错误", null),

    //登录异常
    USERNAME_NOT_EXISTS(30001, "用户名不存在", null),
    WRONG_PASSWORD(30002, "密码错误", null),
    EXPIRED_LOGIN(30003, "登录状态已过期", null),
    NULL_USERNAME(30004, "用户名为空", null),
    NULL_PASSWORD(30005, "密码为空", null),
    UNTRUSTED_IP(30006, "不受信任的IP", null),
    NOT_LOGGED_IN(30007, "未登录", null),
    HAD_LOGGED_IN(30008, "已有登录用户", null),
    NEED_VERIFY(30009, "请求的权限需要验证", null),
    WRONG_CAPTCHA(30010, "验证码错误", null),
    NULL_CAPTCHA(30011, "验证码为空", null),
    UNKNOWN_PASSWORD(30012, "未知的密码串", null),
    ACCOUNT_NOT_ACTIVATED(30013, "账户尚未激活", null),


    //资源异常
    RESOURCE_NOT_FOUND(80001, "资源不存在", null),
    RESOURCE_NOT_ALLOWED(80002, "资源不可访问", null),
    RESOURCE_TOO_BIG(80003, "资源太大", null),
    FILE_EMPTY(80004, "文件为空", null),
    RESOURCE_TYPE_MISMATCH(80005, "资源类型不匹配", null),

    //消息体异常
    MESSAGE_NOT_EXIST(70001, "消息不存在", null),
    MESSAGE_NOT_ALLOWED(70002, "消息不被允许", null),
    MESSAGE_WRONG_FORMAT(70003, "消息格式错误", null),


    //服务器异常
    IO_EXCEPTION(50001, "IO出现错误", null),
    INITIALIZE_FAILED(50003, "初始化系统出现错误", null);


    fun data(data: Any?): ServiceErrorEnum {
        this.data = data
        return this
    }

    fun ok(): Boolean {
        return code == 0
    }

    override fun toString(): String {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue)
    }

    companion object {
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