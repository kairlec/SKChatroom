package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg
import cn.skstudio.config.static.StaticConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.utils.LocalConfig
import cn.skstudio.utils.SendEmail
import cn.skstudio.utils.SnowFlake
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import com.alibaba.fastjson.serializer.SerializerFeature
import org.springframework.util.DigestUtils

@NoArg
data class User(var userID: Long,
                var username: String,
                @JSONField(serialize = false) var password: String,
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

    constructor() : this(-1, "@", "@", null, null, null, null, null, null, null, null, true, false, false, false)

    fun equalPassword(password: String): Boolean {
        return this.password == DigestUtils.md5DigestAsHex((password + userID).toByteArray())
    }

    fun updatePassword(password: String): ServiceErrorEnum {
        return when {
            password.length > StaticConfig.maxPasswordLength -> {
                ServiceErrorEnum.PASSWORD_TOO_LONG
            }
            password.length < StaticConfig.minPasswordLength -> {
                ServiceErrorEnum.WEAK_PASSWORD
            }
            else -> {
                this.password = DigestUtils.md5DigestAsHex((password + userID).toByteArray())
                ServiceErrorEnum.NO_ERROR
            }
        }
    }

    fun setUsername(username: String): ServiceErrorEnum {
        val error = LocalConfig.userService.checkUsername(username)
        return if (error.ok()) {
            this.username = username
            ServiceErrorEnum.NO_ERROR
        } else {
            error
        }
    }

    fun updateSignature(signature: String): ServiceErrorEnum {
        return if (signature.length > StaticConfig.maxSignatureLength) {
            ServiceErrorEnum.SIGNATURE_TOO_LONG
        } else {
            this.signature = signature
            ServiceErrorEnum.NO_ERROR
        }
    }

    fun updateNickname(nickname: String): ServiceErrorEnum {
        val realNickname = nickname.trim { it <= ' ' }
        return when {
            realNickname.isEmpty() -> {
                ServiceErrorEnum.NICKNAME_EMPTY
            }
            realNickname.length > StaticConfig.maxNickNameLength -> {
                ServiceErrorEnum.NICKNAME_TOO_LONG
            }
            else -> {
                this.nickname = realNickname
                return ServiceErrorEnum.NO_ERROR
            }
        }
    }

    fun updateEmail(email: String): ServiceErrorEnum {
        return if (!SendEmail.checkEmail(email)) {
            ServiceErrorEnum.WRONG_EMAIL
        } else {
            this.email = email
            ServiceErrorEnum.NO_ERROR
        }
    }

    fun updateSex(sex: String): ServiceErrorEnum {
        return if (!StaticConfig.allowSexString.contains(sex)) {
            ServiceErrorEnum.UNKNOWN_SEX
        } else {
            this.sex = sex
            ServiceErrorEnum.NO_ERROR
        }
    }

    fun updatePhone(phone: String): ServiceErrorEnum {
        this.phone = phone
        return ServiceErrorEnum.NO_ERROR
    }

    override fun toString(): String {
        return JSON.toJSONString(this, SerializerFeature.WRITE_MAP_NULL_FEATURES)
    }

    fun readyToUpdate(): User {
        val newUser = User()
        newUser.userID = this.userID
        return newUser
    }

    companion object {
        private val snowFlake = SnowFlake(StaticConfig.snowFlakeWorkerId, StaticConfig.snowFlakeDataCenterId)
        fun getNewID(): Long {
            return snowFlake.nextId()
        }

        fun readyToUpdate(user: User): User {
            val newUser = User()
            newUser.userID = user.userID
            return newUser
        }

        val allUserID
            get() = StaticConfig.signIDToAllUser


    }
}
