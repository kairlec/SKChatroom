package cn.skstudio.config.system

/**
 * @author: Kairlec
 * @version: 1.0
 * @description: 程序启动的属性文件内配置
 */

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.properties.Delegates

@Component
class StartupConfig {

    @Value("\${privatekey:#{null}}")
    fun setPrivateKey(privateKey: String) {
        Companion.privateKey = privateKey
    }

    @Value("\${publickey:#{null}}")
    fun setPublicKey(publicKey: String) {
        Companion.publicKey = publicKey
    }

    @Value("\${allowedorigins:}")
    fun setAllowedOrigins(allowedOrigins: Array<String>) {
        Companion.allowedOrigins = allowedOrigins
    }

    @Value("\${allowedheaders:}")
    fun setAllowedHeaders(allowedHeaders: Array<String>) {
        Companion.allowedHeaders = allowedHeaders
    }

    @Value("\${redis.enable:#{false}}")
    fun setRedisEnabled(enabled: Boolean) {
        redisEnabled = enabled
    }

    companion object {
        lateinit var privateKey: String
        lateinit var publicKey: String
        lateinit var allowedOrigins: Array<String>
        lateinit var allowedHeaders: Array<String>
        var redisEnabled by Delegates.notNull<Boolean>()
    }

}