package cn.skstudio.config.web

import cn.skstudio.annotation.RequestLimit
import cn.skstudio.config.system.StartupConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.utils.Network
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *@program: Backend
 *@description: API接口请求限制拦截器
 *@author: Kairlec
 *@create: 2020-02-27 13:10
 */
@Component
class RequestLimitInterceptor : HandlerInterceptor {
    companion object {
        private val logger = LogManager.getLogger(RequestLimitInterceptor::class.java)
        val pathPatterns: List<String> = ArrayList(listOf(
                "/**"
        ))
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (!StartupConfig.redisEnabled) {
            return true
        }
        if (handler is HandlerMethod) {
            val accessLimit = handler.getMethodAnnotation(RequestLimit::class.java) ?: return true
            val seconds = accessLimit.seconds
            val maxCount = accessLimit.maxCount
            val ip = Network.getIpAddress(request)
            val uri = request.requestURI
            val key = "$ip@$uri"
            logger.info("seconds=$seconds")
            logger.info("maxCount=$maxCount")
            logger.info("ip=$ip")
            logger.info("uri=$uri")
            logger.info("key=$key")
            val count = LocalConfig.redisService[key] as? Int
            logger.info("请求次数:$count")
            when {
                count == null -> {
                    LocalConfig.redisService[key, seconds, TimeUnit.SECONDS] = 1
                }
                count < maxCount -> {
                    logger.info("过期时间:" + LocalConfig.redisService.getExpire(key, TimeUnit.SECONDS) + "秒")
                    LocalConfig.redisService.update(key, count + 1)
                }
                else -> {
                    logger.warn(""""拦截到"$ip"对"$uri"的异常连续访问""")
                    response.writer.write(ResponseDataUtils.error(ServiceErrorEnum.REQUEST_FORBIDDEN).toString())
                    logger.info("过期时间:" + LocalConfig.redisService.getExpire(key, TimeUnit.SECONDS) + "秒")
                    return false
                }
            }
        }
        return true
    }

}