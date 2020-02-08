package cn.skstudio.utils

import cn.skstudio.config.system.StartupConfig
import java.util.*

object PasswordCoder {
    fun fromRequest(password: String): String {
        return try {
            String(RSACoder.decryptByPrivateKey(Base64.getDecoder().decode(password.replace(' ', '+')), StartupConfig.privateKey))
        } catch (e: Exception) {
            e.printStackTrace()
            password
        }
    }
}