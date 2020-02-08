package cn.skstudio.pojo

import cn.skstudio.controller.websocket.WebSocketHandler

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
        fun getInstance(user: User): GuestUser {
            return GuestUser(user.userID,
                    user.nickname ?: "未设置",
                    if (user.privateEmail!!) "@PRIVATE?" else user.email!!,
                    if (user.privateSex!!) "@PRIVATE?" else user.sex ?: "未知",
                    user.avatar ?: "@DEFAULT?",
                    if (user.privatePhone!!) "@PRIVATE?" else user.phone ?: "",
                    user.signature ?: "",
                    WebSocketHandler.isOnline(user.userID))
        }
    }
}