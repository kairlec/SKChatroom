package cn.skstudio.controller.user

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.ArrayList

@Configuration
open class UserInterceptorRegister : WebMvcConfigurer {

    @Bean
    open fun userInterceptorMaker(): UserInterceptor {
        return UserInterceptor()
    }

    private val pathPatterns: List<String> = ArrayList(listOf(
            "/api/user",
            "/api/user/*"
    ))

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(UserInterceptor()).addPathPatterns(pathPatterns)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/*").allowCredentials(true)
    }
}