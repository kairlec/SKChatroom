package cn.skstudio.service

import cn.skstudio.pojo.ActionMessage
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
interface ActionMessageService {
    fun initialize(): Int?

    operator fun get(messageID: Long): ActionMessage?

    fun getAllFromActionMessage(userID: Long, after: LocalDateTime): List<ActionMessage>?

    fun getAllToActionMessage(userID: Long, after: LocalDateTime): List<ActionMessage>?

    fun getAllUnreadFromActionMessages(userID: Long): List<ActionMessage>?

    fun getAllUnreadToActionMessages(userID: Long): List<ActionMessage>?

    fun getFromToActionMessage(fromUserID: Long, toUserID: Long, after: LocalDateTime): List<ActionMessage>?

    fun newActionMessage(message: ActionMessage): Int?

    fun updateContent(messageID: Long, content: String): Int?

    fun read(messageID: Long): Int?

    fun read(message: ActionMessage): Int?
}