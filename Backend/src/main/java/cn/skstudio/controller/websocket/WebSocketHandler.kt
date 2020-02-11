package cn.skstudio.controller.websocket

import cn.skstudio.config.static.StaticConfig
import cn.skstudio.pojo.ActionMessage
import cn.skstudio.pojo.User
import cn.skstudio.local.utils.LocalConfig
import com.alibaba.fastjson.JSON
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Controller
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@Controller
class WebSocketHandler : TextWebSocketHandler() {

    //新增WebSocket连接
    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("成功建立连接")
        webSocketMap[(session.attributes["user"] as User).userID] = session
        session.sendMessage(TextMessage("成功建立socket连接,ID=" + session.id))
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("连接出错")
        webSocketMap.remove((session.attributes["user"] as User).userID)
        if (session.isOpen) {
            session.close()
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info("连接已关闭：$status")
        webSocketMap.remove((session.attributes["user"] as User).userID)
    }

    override fun supportsPartialMessages(): Boolean {
        return false
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val msg = message.payload
        try {
            parseReceiveMessage(msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val logger = LogManager.getLogger(WebSocketHandler::class.java)
        private val webSocketMap = ConcurrentHashMap<Long, WebSocketSession>()
        val onlineCount: Int
            get() = webSocketMap.size

        fun isOnline(id: Long) = webSocketMap.containsKey(id)

        fun trySendMessage(message: ActionMessage): Boolean {
            LocalConfig.actionMessageService.newActionMessage(message) ?: return false
            if (message.toID == StaticConfig.signIDToAllUser) {
                sendMessageToAll(message)
            }
            return if (isOnline(message.toID)) {
                return try {
                    webSocketMap[message.toID]!!.sendMessage(TextMessage(JSON.toJSONString(message)))
                    true
                } catch (e: IOException) {
                    e.printStackTrace()
                    false
                }
            } else {
                true
            }
        }

        fun sendMessageToAll(message: ActionMessage) {
            val textMessage = TextMessage(JSON.toJSONString(message))
            for (webSocketSessionTuple in webSocketMap) {
                webSocketSessionTuple.value.sendMessage(textMessage)
            }
        }

        private fun parseReceiveMessage(textMessage:String){
            //TODO 收到的消息串处理
        }
    }
}