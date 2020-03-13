package cn.skstudio.controller.websocket


import cn.skstudio.local.utils.RequestAuthenticator
import cn.skstudio.local.utils.ResponseDataUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor

class WebSocketInterceptor : HttpSessionHandshakeInterceptor() {
    companion object {
        private val logger: Logger = LogManager.getLogger(WebSocketInterceptor::class.java)
    }

    override fun beforeHandshake(request: ServerHttpRequest, response: ServerHttpResponse, wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>): Boolean {
        logger.debug("WebSocket拦截器启动")
        if (request is ServletServerHttpRequest) {
            logger.debug("WebSocket拦截器请求类型匹配成功")
            val session = request.servletRequest.session
            val error = RequestAuthenticator.authHttpSession(session)
            if (!error.ok()) {
                if (response is ServletServerHttpResponse) {
                    response.servletResponse.writer.write(ResponseDataUtils.error(error).toString())
                }
                return false
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes)
    }

}
