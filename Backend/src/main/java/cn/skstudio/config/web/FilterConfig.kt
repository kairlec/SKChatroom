package cn.skstudio.config.web

/**
 * @author: Kairlec
 * @version: 1.0
 * @description: 过滤器配置
 */

import cn.skstudio.pojo.json.HTTPInfo
import org.apache.catalina.filters.RemoteIpFilter
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
open class FilterConfig {
    @Bean
    open fun remoteIpFilter(): RemoteIpFilter {
        return RemoteIpFilter()
    }


    @Bean
    open fun testFilterRegistration(): FilterRegistrationBean<AllDomainFilter> {
        val registration = FilterRegistrationBean<AllDomainFilter>()
        registration.filter = AllDomainFilter()
        // 过滤应用程序中所有资源
        registration.addUrlPatterns("/*")
        registration.order = 1
        return registration
    }

    @Order(0)
    inner class AllDomainFilter : Filter {
        override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
            servletRequest.characterEncoding = "UTF-8"
            servletResponse.characterEncoding = "UTF-8"
            val response = servletResponse as HttpServletResponse
            val originHeader = (servletRequest as HttpServletRequest).getHeader("Origin")
            response.setHeader("Access-Control-Allow-Origin", originHeader)
            response.setHeader("Access-Control-Max-Age", "86400")
            response.setHeader("Access-Control-Allow-Credentials", "true")
            response.setHeader("Access-Control-Allow-Methods","GET,POST,OPTIONS")
            response.setHeader("Access-Control-Allow-Headers","Access-Control,x-ijt")
            response.setHeader("Cache-Control", "no-cache")
            filterChain.doFilter(servletRequest, servletResponse)
            //最后记录一下这次请求内容和结果
            logger.log(Level.getLevel("REQUEST"), HTTPInfo(servletRequest, response).json)
        }

    }

    companion object {
        private val logger = LogManager.getLogger(FilterConfig::class.java)
    }
}