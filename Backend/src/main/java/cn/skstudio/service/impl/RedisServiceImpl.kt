package cn.skstudio.service.impl

/**
 *@program: Backend
 *@description: Redis缓存服务
 *@author: Kairlec
 *@create: 2020-02-27 13:43
 */

import cn.skstudio.config.system.StartupConfig
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import redis.clients.jedis.JedisPoolConfig
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.Resource
import kotlin.system.exitProcess

@Service
open class RedisServiceImpl {
    companion object {
        private val logger = LogManager.getLogger(RedisServiceImpl::class.java)
    }

    @Bean(name = ["RedisProperties"])
    @ConditionalOnMissingBean
    @ConfigurationProperties("spring.redis")
    open fun redisProperties(): RedisProperties {
        return RedisProperties()
    }

    @Autowired
    @Qualifier("RedisProperties")
    private lateinit var properties: RedisProperties

    open fun jedisConnectionFactory(): RedisConnectionFactory {
        val poolConfig = JedisPoolConfig()
        poolConfig.maxIdle = properties.jedis.pool.maxIdle
        poolConfig.minIdle = properties.jedis.pool.minIdle
        poolConfig.maxWaitMillis = properties.jedis.pool.maxWait.toMillis()
        poolConfig.testOnBorrow = true
        poolConfig.testOnCreate = true
        poolConfig.testWhileIdle = true
        val redisConnectionFactory = JedisConnectionFactory(poolConfig)
        val standaloneConfiguration = redisConnectionFactory.standaloneConfiguration!!
        standaloneConfiguration.hostName = properties.host
        standaloneConfiguration.password = RedisPassword.of(properties.password)
        standaloneConfiguration.port = properties.port
        standaloneConfiguration.database = properties.database
        return redisConnectionFactory
    }

    @Lazy
    @Bean(name = ["RedisTemplate"])
    open fun redisTemplate(): RedisTemplate<*, *> {
        val redisTemplate: RedisTemplate<*, *> = RedisTemplate<Any?, Any?>()
        redisTemplate.connectionFactory = jedisConnectionFactory()
        return redisTemplate
    }

    @Lazy
    @Resource
    @Qualifier("RedisTemplate")
    private lateinit var redisTemplate: RedisTemplate<Serializable, Any>


    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var startupConfig: StartupConfig

    @PostConstruct
    fun init() {
        if (startupConfig.redisEnabled) {
            try {
                set("skchatroom_init", true)
                logger.info("Redis init:${get("skchatroom_init")}")
                remove("skchatroom_init")
            } catch (e: RedisConnectionFailureException) {
                e.printStackTrace()
                logger.fatal("Init connect failed")
                exitProcess(SpringApplication.exit(applicationContext))
            } catch (e: Exception) {
                e.printStackTrace()
                logger.fatal("Init init failed")
                exitProcess(SpringApplication.exit(applicationContext))
            }
            logger.info("Init redis success")
        }
    }

    fun setExpire(key: String, expireTime: Long, timeUnit: TimeUnit = TimeUnit.SECONDS): Boolean {
        return try {
            redisTemplate.expire(key, expireTime, timeUnit)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setExpire(key: String, date: Date): Boolean {
        return try {
            redisTemplate.expireAt(key, date)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getExpire(key: String, timeUnit: TimeUnit? = null): Long {
        timeUnit ?: return redisTemplate.getExpire(key)
        return redisTemplate.getExpire(key, timeUnit)
    }

    fun update(key: String, value: Any, expireTime: Long? = null, timeUnit: TimeUnit? = null): Boolean {
        if (!exists(key)) {
            return false
        }
        val timeUnitT = timeUnit ?: TimeUnit.SECONDS
        val expireTimeT = expireTime ?: getExpire(key, timeUnitT)
        return set(key, expireTimeT, timeUnitT, value)
    }

    operator fun set(key: String, value: Any): Boolean {
        return try {
            redisTemplate.opsForValue()[key] = value
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun increment(key: String, number: Long) {
        redisTemplate.boundValueOps(key).increment(number)
    }

    fun increment(key: String, number: Double) {
        redisTemplate.boundValueOps(key).increment(number)
    }

    operator fun set(key: String, expireTime: Long = -1, timeUnit: TimeUnit = TimeUnit.SECONDS, value: Any): Boolean {
        logger.info("key=$key")
        logger.info("expireTime=$expireTime")
        logger.info("timeUnit=$timeUnit")
        logger.info("value=$value")
        return try {
            if (expireTime > 0) {
                redisTemplate.opsForValue().set(key, value, expireTime, timeUnit)
            } else {
                redisTemplate.opsForValue()[key] = value
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun remove(vararg keys: String) {
        redisTemplate.delete(keys)
    }

    fun removePattern(pattern: String) {
        val keys: Set<Serializable> = redisTemplate.keys(pattern)
        if (keys.isNotEmpty()) {
            redisTemplate.delete(keys)
        }
    }

    fun remove(key: String) {
        if (exists(key)) {
            redisTemplate.delete(key)
        }
    }

    fun exists(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    operator fun get(key: String): Any? {
        return redisTemplate.opsForValue()[key]
    }

}