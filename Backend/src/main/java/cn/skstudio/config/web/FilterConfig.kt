package cn.skstudio.config.web

import cn.skstudio.pojo.json.HTTPInfo
import org.apache.catalina.filters.RemoteIpFilter
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import java.io.IOException
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
        // 过滤应用程序中所有资源,当前应用程序根下的所有文件包括多级子目录下的所有文件，注意这里*前有“/”
        registration.addUrlPatterns("/*")
        // 过滤器顺序
        registration.order = 1
        return registration
    }

    // 定义过滤器
    @Order(0)
    inner class AllDomainFilter : Filter {

        @Throws(IOException::class, ServletException::class)
        override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
            servletRequest.characterEncoding = "UTF-8"
            servletResponse.characterEncoding = "UTF-8"
            val response = servletResponse as HttpServletResponse
            val originHeader = (servletRequest as HttpServletRequest).getHeader("Origin")
            response.setHeader("Access-Control-Allow-Origin", originHeader)
            response.setHeader("Access-Control-Allow-Credentials", "true")
            response.setHeader("Cache-Control", "no-cache")
            filterChain.doFilter(servletRequest, servletResponse)
            logger.log(Level.getLevel("REQUEST"), HTTPInfo(servletRequest, response).toString())
        }

    }

    companion object {
        private val logger = LogManager.getLogger(FilterConfig::class.java)
    }
}