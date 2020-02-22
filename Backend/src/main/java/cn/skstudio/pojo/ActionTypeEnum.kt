package cn.skstudio.pojo


enum class ActionTypeEnum(private val typeCode: Int) {
    ADD_FRIEND_REQUEST(0),//添加好友请求
    GROUP_CHAT_MESSAGE(1),//群聊消息
    PRIVATE_CHAT_MESSAGE(2),//私聊消息
    DELETE_FRIEND_REQUEST(3),//删除好友
    SYSTEM_NOTIFICATION(4);//系统通知

    companion object {
        fun parse(typeCode: Int): ActionTypeEnum? {
            for (action in ActionTypeEnum.values()) {
                if (action.typeCode == typeCode) {
                    return action
                }
            }
            return null
        }
    }
}