package cn.skstudio.pojo.vo

import cn.skstudio.annotation.NoArg
import cn.skstudio.utils.RSACoder

@NoArg
data class Config(
        var privateKey: String,
        var publicKey: String
) {
    companion object {
        val Default: Config
            get() {
                val key = RSACoder.initKey()
                val publicKey = RSACoder.getPublicKey(key)!!
                val privateKey = RSACoder.getPrivateKey(key)!!
                return Config(privateKey, publicKey)
            }
    }
}
