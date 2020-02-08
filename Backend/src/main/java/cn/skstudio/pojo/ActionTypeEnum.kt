package cn.skstudio.pojo

import com.alibaba.fastjson.annotation.JSONType

@JSONType(serializeEnumAsJavaBean = true)
enum class ActionTypeEnum(val typeCode:Int) {
    ADD_FRIEND_REQUEST(0),//添加好友请求
    GROUP_CHAT_MESSAGE(1),//群聊消息
    PRIVATE_CHAT_MESSAGE(2),//私聊消息
    DELETE_FRIEND_REQUEST(3),//删除好友
    SYSTEM_NOTIFICATION(4),//系统通知
}