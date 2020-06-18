package cn.skstudio.controller

import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.exception.SKException
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.intf.ResponseDataInterface
import cn.skstudio.local.utils.ResponseDataUtils.responseOK
import cn.skstudio.pojo.ActionMessage
import cn.skstudio.pojo.ActionTypeEnum
import cn.skstudio.pojo.Friend
import cn.skstudio.pojo.User
import cn.skstudio.service.impl.ActionMessageServiceImpl
import cn.skstudio.service.impl.FriendServiceImpl
import cn.skstudio.utils.asLongOrNull
import cn.skstudio.utils.set
import cn.skstudio.utils.toJSON
import cn.skstudio.utils.toObjectNode
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute
import java.time.LocalDateTime


@JsonRequestMapping(value = ["/api/message"])
@RestController
class MessageController {
    companion object {
        private val logger = LogManager.getLogger(MessageController::class.java)
    }

    @Autowired
    private lateinit var actionMessageService: ActionMessageServiceImpl

    @Autowired
    private lateinit var friendService: FriendServiceImpl

    @Autowired
    private lateinit var webSocketHandler: WebSocketHandler

    //获取未读消息
    @RequestMapping(value = ["/get/unreadTo"])
    fun getUnreadToMessage(@SessionAttribute(name = "user") user: User): ResponseDataInterface {
        val messages = actionMessageService.getAllUnreadToActionMessages(user.userID)
                ?: emptyList()
        logger.info(messages)
        return messages.responseOK
    }

    //获取历史消息
    @RequestMapping(value = ["/get/history"])
    fun getHistoryMessage(@SessionAttribute(name = "user") user: User): ResponseDataInterface {
        val time = LocalDateTime.now().minusDays(7)
        val messages = actionMessageService.getAllRelatedActionMessage(user.userID, time) ?: emptyList()
        logger.debug(messages)
        return messages.responseOK
    }

    //标记已读
    @RequestMapping(value = ["/read"])
    fun read(@SessionAttribute(name = "user") user: User,
             @RequestParam(name = "ids", required = false, defaultValue = "") ids: String): ResponseDataInterface {
        val messageIDs = ids.split(';').map { it.toLongOrNull() ?: ServiceErrorEnum.ID_INVALID.data(it).throwout() }
        if (actionMessageService.read(user.userID, messageIDs) == null) {
            ServiceErrorEnum.IO_EXCEPTION.throwout()
        }
        return null.responseOK
    }

    @RequestMapping(value = ["/friend/add"])
    fun addFriend(@SessionAttribute(name = "user") user: User,
                  @RequestParam(name = "content") content: String,
                  @RequestParam(name = "targetID") targetID: Long): ResponseDataInterface {
        try {
            val json = content.toObjectNode()
                    ?: ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data(content).throwout()
            logger.info(json)
            json["groupID"].asLongOrNull()
                    ?: ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("No groupID or error format:${json["groupID"]}").throwout()
            json["type"] = "REQUEST"
            json["result"] = "WAIT"
            val message = ActionMessage.create(ActionTypeEnum.ADD_FRIEND_REQUEST, user.userID, targetID, null, String.toJSON(json))
            actionMessageService.newActionMessage(message)
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("Create request message").throwout()
        } catch (e: SKException) {
            throw e
        } catch (e: Exception) {
            ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("parse json object").throwout(e)
        }
        return targetID.responseOK
    }

    @RequestMapping(value = ["/friend/delete"])
    fun deleteFriend(@SessionAttribute(name = "user") user: User,
                     @RequestParam(name = "targetID") targetID: Long): ResponseDataInterface {
        friendService.deleteFriend(user.userID, targetID)
                ?: ServiceErrorEnum.IO_EXCEPTION.data("deleteFriend").throwout()
        return null.responseOK
    }

    @RequestMapping(value = ["/friend/accept"])
    fun acceptFriend(@SessionAttribute(name = "user") user: User,
                     @RequestParam(name = "groupID") friendGroupID: Long,
                     @RequestParam(name = "messageID") messageID: Long): ResponseDataInterface {
        //获取要处理的消息
        val message = actionMessageService[messageID]
                ?: ServiceErrorEnum.MESSAGE_NOT_EXIST.data(messageID).throwout()
        //验证消息所有者(防止非法提交来获得他人的私人消息)
        if (!message.ownerVerify(user.userID)) {
            ServiceErrorEnum.MESSAGE_NOT_ALLOWED.data(messageID).throwout()
        } else {
            val oldContent = message.content.toObjectNode()
                    ?: ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("message.content").throwout()
            //获取被添加人在添加人中的分组
            val userGroupID = oldContent["groupID"]?.asLong()
                    ?: ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("groupID").throwout()
            //写入好友数据表列表
            friendService.addFriend(message.fromID, message.toID, userGroupID, friendGroupID)
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("addFriend").throwout()
            //将该消息已读
            actionMessageService.read(user.userID, listOf(messageID))
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("readMessage").throwout()
            //创造回执消息
            val content = String.toJSON(object {
                val type = "RESPONSE"
                val requestID = messageID
                val result = "ACCEPT"
            })
            oldContent["result"] = "ACCEPT"
            actionMessageService.updateContent(messageID, String.toJSON(oldContent))
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("Unable to update content").throwout()
            val responseMessage = ActionMessage.create(ActionTypeEnum.ADD_FRIEND_REQUEST, message.toID, message.fromID, null, content)
            actionMessageService.newActionMessage(responseMessage)
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("Create response message").throwout()
            //尝试立即发送回执消息(若在线)
            webSocketHandler.trySendMessage(responseMessage)
            return Friend(message.fromID, friendGroupID).responseOK
        }
    }

    @RequestMapping(value = ["/friend/refuse"])
    fun refuseFriend(@SessionAttribute(name = "user") user: User,
                     @RequestParam(name = "messageID") messageID: Long): ResponseDataInterface {
        val message = actionMessageService[messageID]
                ?: ServiceErrorEnum.MESSAGE_NOT_EXIST.data(messageID).throwout()
        if (!message.ownerVerify(user.userID)) {
            ServiceErrorEnum.MESSAGE_NOT_ALLOWED.data(messageID).throwout()
        } else {
            val oldContent = message.content.toObjectNode()
                    ?: ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("message.content").throwout()
            actionMessageService.read(user.userID, listOf(messageID))
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("readMessage").throwout()
            val content = String.toJSON(object {
                val type = "RESPONSE"
                val requestID = messageID
                val result = "REFUSE"
            })
            oldContent["result"] = "REFUSE"
            actionMessageService.updateContent(messageID, String.toJSON(oldContent))
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("Unable to update content").throwout()
            val responseMessage = ActionMessage.create(ActionTypeEnum.ADD_FRIEND_REQUEST, message.toID, message.fromID, null, content)
            actionMessageService.newActionMessage(responseMessage)
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("Create response message").throwout()
            webSocketHandler.trySendMessage(responseMessage)
            return null.responseOK
        }
    }

    @RequestMapping(value = ["/friend/ignore"])
    fun ignoreFriend(@SessionAttribute(name = "user") user: User,
                     @RequestParam(name = "messageID") messageID: Long): ResponseDataInterface {
        val message = actionMessageService[messageID]
                ?: ServiceErrorEnum.MESSAGE_NOT_EXIST.data(messageID).throwout()
        if (!message.ownerVerify(user.userID)) {
            ServiceErrorEnum.MESSAGE_NOT_ALLOWED.data(messageID).throwout()
        } else {
            val oldContent = message.content.toObjectNode()
                    ?: ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("message.content").throwout()
            actionMessageService.read(user.userID, listOf(messageID))
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("readMessage").throwout()
            oldContent["result"] = "IGNORE"
            actionMessageService.updateContent(messageID, String.toJSON(oldContent))
                    ?: ServiceErrorEnum.IO_EXCEPTION.data("Unable to update content").throwout()
            return null.responseOK
        }
    }


}