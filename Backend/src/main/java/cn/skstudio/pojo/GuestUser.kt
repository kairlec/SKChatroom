package cn.skstudio.pojo

import cn.skstudio.controller.WebSocketHandler
import org.apache.logging.log4j.LogManager

data class GuestUser(
        var userID: Long = -1,
        var nickname: String,
        var email: String,
        var sex: String,
        var avatar: String,
        var phoneNumber: String,
        var signature: String,
        var isOnline: Boolean
) {

    companion object {
        private val logger = LogManager.getLogger(GuestUser::javaClass)
        fun getInstance(user: User): GuestUser {
            logger.info("user id = ${user.userID}")
            val guestUser = GuestUser(user.userID,
                    user.nickname,
                    if (user.privateEmail) "@PRIVATE?" else user.email,
                    if (user.privateSex) "@PRIVATE?" else user.sex,
                    user.avatar ?: "@DEFAULT?",
                    if (user.privatePhone) "@PRIVATE?" else user.phone ?: "",
                    user.signature,
                    WebSocketHandler.isOnline(user.userID))
            logger.info("user id = ${guestUser.userID}")
            return guestUser
        }
    }
}