package cn.skstudio.config.system

/**
 * @author: Kairlec
 * @version: 1.0
 * @description: 程序启动的属性文件内配置
 */

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class StartupConfig(
        @Value("\${allowedorigins:}")
        val allowedOrigins: Array<String>,

        @Value("\${allowedheaders:}")
        val allowedHeaders: Array<String>,

        @Value("\${redis.enable:#{false}}")
        val redisEnabled: Boolean
)