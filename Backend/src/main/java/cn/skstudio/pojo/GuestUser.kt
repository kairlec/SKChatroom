package cn.skstudio.pojo

import cn.skstudio.controller.websocket.WebSocketHandler
import cn.skstudio.fastjson.LongToStringSerializer
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import com.alibaba.fastjson.serializer.SerializerFeature
import org.apache.logging.log4j.LogManager

data class GuestUser(
        @JSONField(serializeUsing = LongToStringSerializer::class)
        var userID: Long = -1,
        var nickname: String,
        var email: String,
        var sex: String,
        var avatar: String,
        var phoneNumber: String,
        var signature: String,
        var isOnline: Boolean
) {
    override fun toString(): String {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue)
    }

    companion object {
        private val logger = LogManager.getLogger(GuestUser::javaClass)
        fun getInstance(user: User): GuestUser {
            logger.info("user id = ${user.userID}")
            val guestUser = GuestUser(user.userID,
                    user.nickname ?: "未设置",
                    if (user.privateEmail!!) "@PRIVATE?" else user.email!!,
                    if (user.privateSex!!) "@PRIVATE?" else user.sex ?: "未知",
                    user.avatar ?: "@DEFAULT?",
                    if (user.privatePhone!!) "@PRIVATE?" else user.phone ?: "",
                    user.signature ?: "",
                    WebSocketHandler.isOnline(user.userID))
            logger.info("user id = ${guestUser.userID}")
            return guestUser
        }
    }
}