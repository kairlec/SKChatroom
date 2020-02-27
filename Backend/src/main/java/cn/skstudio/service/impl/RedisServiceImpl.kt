package cn.skstudio.service.impl

/**
 *@program: Backend
 *@description: Redis缓存服务
 *@author: Kairlec
 *@create: 2020-02-27 13:43
 */

import org.apache.logging.log4j.LogManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource


@Service
class RedisServiceImpl {
    companion object {
        private val logger = LogManager.getLogger(RedisServiceImpl::class.java)
    }

    @Resource
    private lateinit var redisTemplate: RedisTemplate<Serializable, Any>

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