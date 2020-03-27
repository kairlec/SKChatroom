package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg
import cn.skstudio.config.static.StaticConfig
import cn.skstudio.exception.SKException
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.utils.SendEmail
import cn.skstudio.utils.SnowFlake
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.util.DigestUtils

@NoArg
data class User(
        var userID: Long,
        var username: String,
        @JsonIgnore
        var password: String,
        var nickname: String,
        var email: String,
        var sex: String,
        var avatar: String?,
        var phone: String?,
        var lastSessionID: String?,
        var IP: String?,
        var signature: String,
        var privateSex: Boolean,
        var privatePhone: Boolean,
        var privateEmail: Boolean,
        var admin: Boolean
) {

    constructor() : this(-1, "", "", "", "", "未知", null, null, null, null, "", true, true, true, false)

    fun equalPassword(password: String): Boolean {
        return this.password == DigestUtils.md5DigestAsHex((password + userID).toByteArray())
    }

    fun readyToUpdate(): UpdateUser {
        return UpdateUser(this)
    }

    fun applyUpdate(updateUser: UpdateUser) {
        this.username = updateUser.username ?: this.username
        this.password = updateUser.password ?: this.password
        this.nickname = updateUser.nickname ?: this.nickname
        this.email = updateUser.email ?: this.email
        this.sex = updateUser.sex ?: this.sex
        this.avatar = updateUser.avatar ?: this.avatar
        this.phone = updateUser.phone ?: this.phone
        this.lastSessionID = updateUser.lastSessionID ?: this.lastSessionID
        this.IP = updateUser.IP ?: this.IP
        this.signature = updateUser.signature ?: this.signature
        this.privateSex = updateUser.privateSex ?: this.privateSex
        this.privatePhone = updateUser.privatePhone ?: this.privatePhone
        this.privateEmail = updateUser.privateEmail ?: this.privateEmail
        this.admin = updateUser.admin ?: this.admin
    }

    data class UpdateUser(
            val userID: Long,
            var username: String?,
            var password: String?,
            var nickname: String?,
            var email: String?,
            var sex: String?,
            var avatar: String?,
            var phone: String?,
            var lastSessionID: String?,
            var IP: String?,
            var signature: String?,
            var privateSex: Boolean?,
            var privatePhone: Boolean?,
            var privateEmail: Boolean?,
            var admin: Boolean?
    ) {
        constructor(user: User) : this(user.userID, null, null, null, null, null, null, null, null, null, null, null, null, null, null)

        operator fun set(type: Int, encryptPassword: Boolean = true, data: Any) {
            when (type) {
                USERNAME_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    val error = LocalConfig.userService.checkUsername(data)
                    if (error.ok) {
                        this.username = data
                    } else {
                        error.throwout()
                    }
                }
                PASSWORD_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    when {
                        data.length > StaticConfig.maxPasswordLength -> {
                            ServiceErrorEnum.PASSWORD_TOO_LONG.throwout()
                        }
                        data.length < StaticConfig.minPasswordLength -> {
                            ServiceErrorEnum.WEAK_PASSWORD.throwout()
                        }
                        else -> {
                            if (encryptPassword) {
                                this.password = DigestUtils.md5DigestAsHex((data + userID).toByteArray())
                            } else {
                                this.password = data
                            }
                        }
                    }
                }
                NICKNAME_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    val realNickname = data.trim()
                    when {
                        realNickname.isEmpty() -> {
                            ServiceErrorEnum.NICKNAME_EMPTY.throwout()
                        }
                        realNickname.length > StaticConfig.maxNickNameLength -> {
                            ServiceErrorEnum.NICKNAME_TOO_LONG.throwout()
                        }
                        else -> {
                            this.nickname = realNickname
                        }
                    }

                }
                EMAIL_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    if (!SendEmail.checkEmail(data)) {
                        ServiceErrorEnum.WRONG_EMAIL.throwout()
                    } else {
                        this.email = data
                    }

                }
                SEX_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    if (!StaticConfig.allowSexString.contains(data)) {
                        ServiceErrorEnum.UNKNOWN_SEX.throwout()
                    } else {
                        this.sex = data
                    }
                }
                AVATAR_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    this.avatar = data
                }
                PHONE_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    this.phone = data
                }
                LASTSESSIONID_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    this.lastSessionID = data
                }
                IP_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    this.IP = data
                }
                SIGNATURE_FIELD -> {
                    if (data !is String) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    if (data.length > StaticConfig.maxSignatureLength) {
                        ServiceErrorEnum.SIGNATURE_TOO_LONG.throwout()
                    } else {
                        this.signature = data
                    }
                }
                PRIVATESEX_FIELD -> {
                    if (data !is Boolean) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    this.privateSex = data
                }
                PRIVATEPHONE_FIELD -> {
                    if (data !is Boolean) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    this.privatePhone = data
                }
                PRIVATEEMAIL_FIELD -> {
                    if (data !is Boolean) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    this.privateEmail = data
                }
                ADMIN_FIELD -> {
                    if (data !is Boolean) {
                        ServiceErrorEnum.INVALID_TYPE.data(data).throwout()
                    }
                    this.admin = data
                }
                else -> {
                    ServiceErrorEnum.TYPE_MISMATCH.data(type).throwout()
                }
            }
        }

        companion object {
            const val USERNAME_FIELD = 1
            const val PASSWORD_FIELD = 2
            const val NICKNAME_FIELD = 3
            const val EMAIL_FIELD = 4
            const val SEX_FIELD = 5
            const val AVATAR_FIELD = 6
            const val PHONE_FIELD = 7
            const val LASTSESSIONID_FIELD = 8
            const val IP_FIELD = 9
            const val SIGNATURE_FIELD = 10
            const val PRIVATESEX_FIELD = 11
            const val PRIVATEPHONE_FIELD = 12
            const val PRIVATEEMAIL_FIELD = 13
            const val ADMIN_FIELD = 14
        }

    }

    companion object {
        private val snowFlake = SnowFlake(StaticConfig.snowFlakeWorkerId, StaticConfig.snowFlakeDataCenterId)

        fun getNewID() = snowFlake.nextId()

        fun readyToUpdate(user: User) = UpdateUser(user)


        val allUserID
            get() = StaticConfig.signIDToAllUser


    }
}
