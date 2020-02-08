package cn.skstudio

import cn.skstudio.utils.RSACoder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.system.ApplicationHome
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.system.exitProcess


@RestController
@EnableConfigurationProperties
@SpringBootApplication
open class SKChatroomApplication {
    companion object {
        private val logger: Logger = LogManager.getLogger(SKChatroomApplication::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            logger.info("Init ...")
            val path = ApplicationHome(SKChatroomApplication::class.java).source.parentFile.toString()
            val aPath="$path${File.separator}application.properties";
            logger.info("Path is $aPath")
            val resource = FileSystemResource(aPath)
            logger.info("Load file system resource completed")
            val properties: Properties = PropertiesLoaderUtils.loadProperties(resource)
            logger.info("Load properties completed")
            val publickey = properties.getProperty("publickey", "")
            val privatekey = properties.getProperty("privatekey", "")
            if (publickey.isEmpty() || privatekey.isEmpty()) {
                logger.info("Publickey or privatekey is not exist , create new publickey and privatekey")
                val key = RSACoder.initKey()
                properties.setProperty("publickey", RSACoder.getPublicKey(key))
                properties.setProperty("privatekey", RSACoder.getPrivateKey(key))
            }
            try {
                FileOutputStream(resource.file).use {
                    //properties.store 方法会转义:和=,所以这里使用直接保存
                    //properties.store(it, "new key")
                    val e = properties.propertyNames()
                    while (e.hasMoreElements()) {
                        val key = e.nextElement() as String
                        val value = properties.getProperty(key)
                        val s = "$key=$value${System.lineSeparator()}"
                        it.write(s.toByteArray())
                    }
                    it.flush()
                }
            } catch (e: IOException) {
                logger.fatal("Initialize RSA code failed")
                e.printStackTrace()
                exitProcess(-1)
            }

            val databaseUrl = properties.getProperty("spring.datasource.url", "")
            val databaseUsername = properties.getProperty("spring.datasource.username", "")
            val databasePassword = properties.getProperty("spring.datasource.password", "")
            if (databaseUrl.isEmpty() || databaseUsername.isEmpty() || databasePassword.isEmpty()) {
                logger.fatal("Database login info is not exists , please set \"spring.datasource.url\",\"spring.datasource.username\",\"spring.datasource.password\"")
                exitProcess(-1)
            }
            runApplication<SKChatroomApplication>(*args)
        }
    }

}

