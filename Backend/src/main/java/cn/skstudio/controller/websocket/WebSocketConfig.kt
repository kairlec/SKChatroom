package cn.skstudio.controller.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
open class WebSocketConfig : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(webSocketController(), "api/msg").addInterceptors(webSocketInterceptor()).setAllowedOrigins("*")
    }

    private fun webSocketInterceptor(): WebSocketInterceptor {
        return WebSocketInterceptor()
    }

    private fun webSocketController(): WebSocketHandler {
        return WebSocketHandler()
    }
}