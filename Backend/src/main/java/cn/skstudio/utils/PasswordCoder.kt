package cn.skstudio.utils

import cn.skstudio.config.system.DatabaseConfig
import java.util.*

object PasswordCoder {
    fun fromRequest(password: String): String {
        return try {
            String(RSACoder.decryptByPrivateKey(Base64.getDecoder().decode(password.replace(' ', '+')), DatabaseConfig.PrivateKey))
        } catch (e: Exception) {
            e.printStackTrace()
            password
        }
    }
}