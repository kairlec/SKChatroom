package cn.skstudio.controller.admin

/**
 * @author: Kairlec
 * @description: 管理员接口拦截注册器
 */
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*

@Configuration
open class AdminInterceptorRegister : WebMvcConfigurer {

    @Bean
    open fun adminInterceptorMaker(): AdminInterceptor {
        return AdminInterceptor()
    }

    private val pathPatterns: List<String> = ArrayList(listOf(
            "/api/admin",
            "/api/admin/*"
    ))

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AdminInterceptor()).addPathPatterns(pathPatterns)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/*").allowCredentials(true)
    }
}