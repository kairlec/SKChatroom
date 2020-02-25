package cn.skstudio.config.web

/**
 *@program: Backend
 *@description: 全局接受CORS
 *@author: Kairlec
 *@create: 2020-02-21 16:48
 */
import cn.skstudio.config.system.StartupConfig
import cn.skstudio.controller.admin.AdminInterceptor
import cn.skstudio.controller.group.GroupInterceptor
import cn.skstudio.controller.user.UserInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
open class InterceptorRegister : WebMvcConfigurer {
    @Bean
    open fun adminInterceptorMaker(): AdminInterceptor {
        return AdminInterceptor()
    }

    @Bean
    open fun userInterceptorMaker(): UserInterceptor {
        return UserInterceptor()
    }

    @Bean
    open fun groupInterceptorMaker(): GroupInterceptor {
        return GroupInterceptor()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(adminInterceptorMaker()).addPathPatterns(AdminInterceptor.pathPatterns)
        registry.addInterceptor(userInterceptorMaker()).addPathPatterns(UserInterceptor.pathPatterns)
        registry.addInterceptor(groupInterceptorMaker()).addPathPatterns(GroupInterceptor.pathPatterns)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedOrigins(*StartupConfig.allowedOrigins)
                .allowedHeaders("x-requested-with","content-type", *StartupConfig.allowedHeaders)
                .allowCredentials(true)
                .maxAge(86400)
    }
}
