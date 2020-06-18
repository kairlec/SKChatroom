package cn.skstudio.pojo.vo

import cn.skstudio.utils.RSACoder
import cn.skstudio.utils.toJSON
import com.fasterxml.jackson.annotation.JsonIgnore

data class StaticStartupConfig(
        var privateKey: String,
        var publicKey: String
) {
    val json
        @JsonIgnore
        get() = run {
            String.toJSON(this)
        }

    companion object {
        val Default: StaticStartupConfig
            get() {
                val key = RSACoder.initKey()
                val publicKey = RSACoder.getPublicKey(key)!!
                val privateKey = RSACoder.getPrivateKey(key)!!
                return StaticStartupConfig(privateKey, publicKey)
            }
    }
}
