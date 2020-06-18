package cn.skstudio.interceptor


import cn.skstudio.local.utils.RequestAuthenticator
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
        if (request is ServletServerHttpRequest) {
            val session = request.servletRequest.session
            val error = RequestAuthenticator.authHttpSession(session)
            if (error.bad) {
                if (response is ServletServerHttpResponse) {
                    response.servletResponse.contentType = "application/json;charset=UTF-8"
                    response.servletResponse.writer.write(error.json)
                }
                return false
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes)
    }

}
