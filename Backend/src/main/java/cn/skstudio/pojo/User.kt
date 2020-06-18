package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg
import cn.skstudio.config.system.StaticConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.service.impl.SendEmailService
import cn.skstudio.service.impl.UserServiceImpl
import cn.skstudio.utils.SnowFlake
import com.fasterxml.jackson.annotation.JsonIgnore
import org.apache.logging.log4j.LogManager
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

    class UpdateUser(user: User) {
        val userID: Long = user.userID
        var username: String? = null
            set(value) {
                if (value == null) {
                    field = null
                    return
                }
                val error = UserServiceImpl.checkUsername(value)
                if (error.ok) {
                    field = value
                } else {
                    error.throwout()
                }
            }

        var password: String? = null
            private set

        var nickname: String? = null
            set(value) {
                if (value == null) {
                    field = null
                    return
                }
                val realNickname = value.trim()
                when {
                    realNickname.isEmpty() -> {
                        ServiceErrorEnum.NICKNAME_EMPTY.throwout()
                    }
                    realNickname.length > StaticConfig.maxNickNameLength -> {
                        ServiceErrorEnum.NICKNAME_TOO_LONG.throwout()
                    }
                    else -> {
                        field = realNickname
                    }
                }
            }

        var email: String? = null
            set(value) {
                if (value == null) {
                    field = null
                    return
                }
                if (!SendEmailService.checkEmail(value)) {
                    ServiceErrorEnum.WRONG_EMAIL.throwout()
                } else {
                    field = value
                }
            }

        var sex: String? = null
            set(value) {
                if (value == null) {
                    field = null
                    return
                }
                if (!StaticConfig.AllowSexString.allow(value)) {
                    ServiceErrorEnum.UNKNOWN_SEX.throwout()
                } else {
                    field = value
                }
            }

        var avatar: String? = null
        var phone: String? = null
        var lastSessionID: String? = null
        var IP: String? = null
        var signature: String? = null
            set(value) {
                if (value == null) {
                    field = null
                    return
                }
                if (value.length > StaticConfig.maxSignatureLength) {
                    ServiceErrorEnum.SIGNATURE_TOO_LONG.throwout()
                } else {
                    field = value
                }
            }

        var privateSex: Boolean? = null
        var privatePhone: Boolean? = null
        var privateEmail: Boolean? = null
        var admin: Boolean? = null

        fun setPassword(password: String, needEncrypted: Boolean) {
            if (!needEncrypted) {
                this.password = password
                return
            }
            when {
                password.length > StaticConfig.maxPasswordLength -> {
                    ServiceErrorEnum.PASSWORD_TOO_LONG.throwout()
                }
                password.length < StaticConfig.minPasswordLength -> {
                    ServiceErrorEnum.WEAK_PASSWORD.throwout()
                }
                else -> {
                    this.password = DigestUtils.md5DigestAsHex((password + userID).toByteArray())
                }
            }
        }

        companion object {
            private val logger = LogManager.getLogger(UpdateUser::class.java)
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
