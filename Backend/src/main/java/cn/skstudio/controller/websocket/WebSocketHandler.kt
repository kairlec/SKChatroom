package cn.skstudio.controller.websocket

import cn.skstudio.config.static.StaticConfig
import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.pojo.ActionMessage
import cn.skstudio.pojo.ActionTypeEnum
import cn.skstudio.pojo.User
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
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
        //session.sendMessage(warpData("成功建立socket连接,ID=" + session.id, "Message"))
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
            val result = parseReceiveMessage(msg, session)
            logger.debug(result)
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
                return true
            }
            return if (isOnline(message.toID)) {
                logger.info("${message.toID} is online")
                return try {
                    webSocketMap[message.toID]!!.sendMessage(warpData(JSON.toJSONString(message),"Message"))
                    true
                } catch (e: IOException) {
                    e.printStackTrace()
                    false
                }
            } else {
                logger.info("${message.toID} is offline")
                true
            }
        }

        private fun warpData(data: Any?, type: String): TextMessage {
            val webSocketMessageObject = JSONObject()
            webSocketMessageObject["type"] = type
            webSocketMessageObject["data"] = data
            logger.debug("""a success"$type" message""")
            return TextMessage(JSON.toJSONString(webSocketMessageObject))
        }

        private fun badMessage(reason: String, timestamp: String): TextMessage {
            val webSocketMessageObject = JSONObject()
            webSocketMessageObject["type"] = "Response"
            webSocketMessageObject["status"] = true
            webSocketMessageObject["timestamp"] = timestamp
            webSocketMessageObject["data"] = reason
            logger.debug("a bad message:${reason}")
            return TextMessage(JSON.toJSONString(webSocketMessageObject))
        }

        fun sendMessageToAll(message: ActionMessage) {
            val textMessage = warpData(message, "Message")
            for (webSocketSessionTuple in webSocketMap) {
                webSocketSessionTuple.value.sendMessage(textMessage)
            }
        }

        private fun parseReceiveMessage(textMessage: String, senderSession: WebSocketSession):String {
            logger.info("get a new message${textMessage}")
            val rawJson = JSON.parseObject(textMessage)
            when (rawJson["type"]) {
                "HeartBeat" -> {
                    senderSession.sendMessage(warpData(null, "HeartBeat"))
                    return "心跳包"
                }
                "Message" -> {
                    val json = rawJson["data"] as? JSONObject ?: return "无数据"
                    logger.info(json)
                    val timestamp = json["timestamp"] as? String ?: return "无时间戳"
                    val type = json["typeCode"] as? Int
                    if (type == null) {
                        senderSession.sendMessage(badMessage("类型为空", timestamp))
                        return "类型为空"
                    }
                    val actionTypeEnum = ActionTypeEnum.parse(type)
                    if (actionTypeEnum == null) {
                        senderSession.sendMessage(badMessage("未知类型", timestamp))
                        return "未知类型"
                    }
                    val toID: Long?
                    try {
                        toID = (json["toID"] as? String)?.toLong()
                        if (toID == null) {
                            senderSession.sendMessage(badMessage("无对象ID", timestamp))
                            return "无对象ID"
                        }
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        senderSession.sendMessage(badMessage("对象ID错误", timestamp))
                        return "对象ID错误"
                    }
                    val fromID = (senderSession.attributes["user"] as User).userID
                    val content = json["content"]?.toString()
                    if (content == null) {
                        senderSession.sendMessage(badMessage("无消息体", timestamp))
                        return "无消息体"
                    }
                    val msg = ActionMessage.create(actionTypeEnum, fromID, toID, null, content)
                    if (!trySendMessage(msg)) {
                        senderSession.sendMessage(badMessage("服务器错误", timestamp))
                    }
                    return "成功"
                }
                else->{
                    return "其他消息"
                }
            }
        }
    }
}