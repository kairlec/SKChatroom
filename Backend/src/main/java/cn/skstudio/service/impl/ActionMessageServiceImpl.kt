package cn.skstudio.service.impl

import cn.skstudio.dao.ActionMessageMapper
import cn.skstudio.pojo.ActionMessage
import cn.skstudio.service.ActionMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception
import java.sql.Timestamp
import java.time.LocalDateTime

@Service
class ActionMessageServiceImpl : ActionMessageService {
    @Autowired
    private lateinit var actionMessageMapper: ActionMessageMapper

    override fun initialize(): Int? {
        return try {
            actionMessageMapper.initialize()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun get(messageID: Long): ActionMessage? {
        return try {
            actionMessageMapper[messageID]
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllFromActionMessage(userID: Long, after: LocalDateTime): List<ActionMessage>? {
        return try {
            actionMessageMapper.getAllFromActionMessages(userID, Timestamp.valueOf(after))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllToActionMessage(userID: Long, after: LocalDateTime): List<ActionMessage>? {
        return try {
            actionMessageMapper.getAllToActionMessages(userID, Timestamp.valueOf(after))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllUnreadFromActionMessages(userID: Long): List<ActionMessage>? {
        return try {
            actionMessageMapper.getAllUnreadFromActionMessages(userID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllUnreadToActionMessages(userID: Long): List<ActionMessage>? {
        return try {
            actionMessageMapper.getAllUnreadToActionMessages(userID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getFromToActionMessage(fromUserID: Long, toUserID: Long, after: LocalDateTime): List<ActionMessage>? {
        return try {
            actionMessageMapper.getFromToActionMessages(fromUserID, toUserID, Timestamp.valueOf(after))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun newActionMessage(message: ActionMessage): Int? {
        return try {
            actionMessageMapper.addActionMessage(message)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun updateContent(messageID: Long, content: String): Int? {
        return try {
            actionMessageMapper.updateContent(messageID, content)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun read(messageID: Long): Int? {
        return try {
            actionMessageMapper.read(messageID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun read(message: ActionMessage): Int? {
        return read(message.messageID)
    }

}