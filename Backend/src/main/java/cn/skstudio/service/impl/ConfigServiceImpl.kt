package cn.skstudio.service.impl

import cn.skstudio.dao.ConfigMapper
import cn.skstudio.service.ConfigService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import java.lang.Exception
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

@Service
class ConfigServiceImpl : ConfigService {

    @Autowired
    private lateinit var configMapper: ConfigMapper

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @PostConstruct
    fun init() {
        if (initialize() == null) {
            logger.fatal("Init database table [Config] failed")
            exitProcess(SpringApplication.exit(applicationContext))
        }else {
            logger.info("Init database table [Config] success")
        }
    }

    override fun containsConfig(name: String): Boolean? {
        return try {
            configMapper.containsConfig(name)?.let { it == 1 }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun initialize(): Int? {
        return try {
            configMapper.initialize()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun setConfig(name: String, value: String): Int? {
        return try {
            containsConfig(name)?.let {
                if (it) {
                    configMapper.setConfig(name, value)
                } else {
                    configMapper.insertConfig(name, value)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getConfig(name: String): String? {
        return try {
            configMapper.getConfig(name)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getPrivateKey(): String? {
        return try {
            getConfig(privateKeyName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getPublicKey(): String? {
        return try {
            getConfig(publicKeyName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun setPrivateKey(privateKey: String): Int? {
        return try {
            setConfig(privateKeyName, privateKey)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun setPublicKey(publicKey: String): Int? {
        return try {
            setConfig(publicKeyName, publicKey)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val privateKeyName = "PrivateKey"
        private const val publicKeyName = "PublicKey"
        private val logger = LogManager.getLogger(ConfigServiceImpl::class.java)
    }
}