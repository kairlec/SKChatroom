package cn.skstudio.service

import cn.skstudio.pojo.ActionMessage
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
interface ActionMessageService {
    fun initialize(): Int?

    fun getAllFromActionMessage(userID: Long, after: LocalDateTime): List<ActionMessage>?

    fun getAllToActionMessage(userID: Long, after: LocalDateTime): List<ActionMessage>?

    fun getFromToActionMessage(fromUserID: Long, toUserID: Long, after: LocalDateTime): List<ActionMessage>?

    fun newActionMessage(message: ActionMessage): Int?

    fun read(messageID: Long): Int?

    fun read(message: ActionMessage): Int?
}