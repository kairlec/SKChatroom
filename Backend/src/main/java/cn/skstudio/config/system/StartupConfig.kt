package cn.skstudio.config.system

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

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

    companion object {
        lateinit var privateKey: String
        lateinit var publicKey: String
    }

}