package cn.skstudio.controller.websocket

import cn.skstudio.pojo.ActionMessage
import cn.skstudio.pojo.ActionTypeEnum
import cn.skstudio.pojo.User
import cn.skstudio.utils.LocalConfig
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
    //新增socket
    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        logger.info("成功建立连接")
        webSocketMap[(session.attributes["user"] as User).userID] = session
        session.sendMessage(TextMessage("成功建立socket连接,ID=" + session.id))
    }

    @Throws(Exception::class)
    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        logger.error("连接出错")
        webSocketMap.remove((session.attributes["user"] as User).userID)
        if (session.isOpen) {
            session.close()
        }
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        logger.info("连接已关闭：$status")
        webSocketMap.remove((session.attributes["user"] as User).userID)
    }

    override fun supportsPartialMessages(): Boolean {
        return false
    }

    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val msg = message.payload
        try {
            for (webSocketSession in webSocketMap) {
                //TODO收到消息
            }
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


        fun sendMessageToID(fromID: Long, toID: Long, message: String): Boolean {
            val actionMessage = ActionMessage.create(ActionTypeEnum.NORMAL_MESSAGE, fromID, toID, null, message)
            LocalConfig.actionMessageService.newActionMessage(actionMessage) ?: return false
            if (isOnline(toID)) {
                try {
                    webSocketMap[toID]!!.sendMessage(TextMessage(message))
                    LocalConfig.actionMessageService.read(actionMessage)
                } catch (e: IOException) {
                    return false
                }
            }
            return true
        }

        @Throws(IOException::class)
        fun sendMessageToAll(message: String) {
            for (webSocketSessionTuple in webSocketMap) {
                webSocketSessionTuple.value.sendMessage(TextMessage(message))
            }
        }
    }
}