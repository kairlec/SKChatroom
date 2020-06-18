package cn.skstudio.config.web

import cn.skstudio.controller.WebSocketHandler
import cn.skstudio.interceptor.WebSocketInterceptor
import cn.skstudio.service.impl.ActionMessageServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
open class WebSocketConfig : WebSocketConfigurer {
    @Autowired
    private lateinit var actionMessageService: ActionMessageServiceImpl

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketController(), "api/msg").addInterceptors(webSocketInterceptor()).setAllowedOrigins("*")
    }

    private fun webSocketInterceptor() = WebSocketInterceptor()

    private fun webSocketController() = WebSocketHandler(actionMessageService,objectMapper)
}