package cn.skstudio.config.system

import cn.skstudio.pojo.vo.Config
import cn.skstudio.service.impl.ConfigServiceImpl
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

@Component
class DatabaseConfig {
    @Autowired
    lateinit var configServiceImpl: ConfigServiceImpl

    @PostConstruct
    fun init() {
        if (configServiceImpl.initialize() == null) {
            logger.fatal("Init database table [config] failed")
            exitProcess(-1)
        }
        val public = configServiceImpl.getPublicKey()
        val private = configServiceImpl.getPrivateKey()
        if (public == null || private == null) {
            generate()
        } else {
            PublicKey = public
            PrivateKey = private
        }
    }

    fun setPublicKey(publicKey: String): Int? {
        return configServiceImpl.setPublicKey(publicKey)
    }

    fun setPrivateKey(privateKey: String): Int? {
        return configServiceImpl.setPrivateKey(privateKey)
    }

    private fun generate() {
        val config = Config.Default
        PublicKey = config.publicKey
        PrivateKey = config.privateKey
    }

    companion object {
        private val logger = LogManager.getLogger(DatabaseConfig::class.java)
        lateinit var PublicKey: String
            private set
        lateinit var PrivateKey: String
            private set
    }
}