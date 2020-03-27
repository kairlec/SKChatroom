package cn.skstudio.controller.user

import cn.skstudio.`interface`.ResponseDataInterface
import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.annotation.RequestLimit
import cn.skstudio.controller.websocket.WebSocketHandler
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.local.utils.LocalConfig.Companion.toJSON
import cn.skstudio.local.utils.LocalConfig.Companion.toJsonNode
import cn.skstudio.local.utils.LocalConfig.Companion.toObjectNode
import cn.skstudio.local.utils.ResourcesUtils
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.local.utils.ResponseDataUtils.responseOK
import cn.skstudio.local.utils.UserChecker
import cn.skstudio.pojo.*
import cn.skstudio.utils.IP
import cn.skstudio.utils.set
import cn.skstudio.utils.StringExtend.similar
import cn.skstudio.utils.asLongOrNull
import org.apache.commons.io.FilenameUtils
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartHttpServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@JsonRequestMapping(value = ["/api/user"])
@RestController
class UserController {

    //region 登录相关方法

    //登录
    @RequestLimit(60, 10)
    @RequestMapping(value = ["/login"])
    fun login(request: HttpServletRequest): ResponseDataInterface {
        val session = request.session
        val user = UserChecker.check(request)
        val updateUser = User.readyToUpdate(user)
        updateUser[User.UpdateUser.LASTSESSIONID_FIELD] = session.id
        updateUser[User.UpdateUser.IP_FIELD] = request.IP
        if (LocalConfig.userService.updateLoginInfo(updateUser) == null) {
            logger.warn("Update user login info failed")
        }
        user.applyUpdate(updateUser)
        session.setAttribute("user", user)
        session.maxInactiveInterval = 60 * 60
        return user.responseOK
    }

    //登出
    @RequestMapping(value = ["/logout"])
    fun logout(request: HttpServletRequest): ResponseDataInterface {
        request.session.invalidate()
        return null.responseOK
    }

    //登录状态
    @RequestMapping(value = ["/relogin"])
    fun relogin(session: HttpSession): ResponseDataInterface {
        return session.getAttribute("user").responseOK
    }

    //endregion

    //在线人数
    @RequestMapping(value = ["/onlineCount"])
    fun onlineCount(): ResponseDataInterface {
        return WebSocketHandler.onlineCount.responseOK
    }

    //是否为管理员
    @RequestMapping(value = ["/isAdmin"])
    fun isAdmin(session: HttpSession): ResponseDataInterface {
        return session.getAttribute("admin").responseOK
    }

    //获取用户信息
    @RequestMapping(value = ["/get/id/{id}"])
    fun getUserInfo(@PathVariable id: Long): ResponseDataInterface {
        val user: User = LocalConfig.userService.getUserByID(id)
                ?: ServiceErrorEnum.USER_ID_NOT_EXIST.throwout()
        return GuestUser.getInstance(user).responseOK
    }

    //消息处理接口
    @RequestMapping(value = ["/message/{type}/{typeInfo}"])
    fun message(@PathVariable type: String, request: HttpServletRequest, @PathVariable typeInfo: String): ResponseDataInterface {
        val user = request.session.getAttribute("user") as User
        return when (type) {
            //获取消息
            "get" -> {
                return when (typeInfo) {
                    "unreadTo" -> {
                        val messages = LocalConfig.actionMessageService.getAllUnreadToActionMessages(user.userID)
                                ?: ArrayList()
                        logger.info(messages)
                        messages.responseOK
                    }
                    //TODO 历史消息
                    "" -> {
                        null.responseOK
                    }
                    else -> {
                        ServiceErrorEnum.UNKNOWN_REQUEST.data(typeInfo).throwout()
                    }
                }
            }
            "read" -> {
                val messageID: Long = typeInfo.toLongOrNull()
                        ?: ServiceErrorEnum.ID_INVALID.data(typeInfo).throwout()
                val message = LocalConfig.actionMessageService[messageID]
                        ?: ServiceErrorEnum.MESSAGE_NOT_EXIST.throwout()
                if (!message.ownerVerify(user.userID)) {
                    ServiceErrorEnum.MESSAGE_NOT_ALLOWED.throwout()
                }
                if (LocalConfig.actionMessageService.read(message) == null) {
                    ServiceErrorEnum.IO_EXCEPTION.throwout()
                }
                message.messageID.responseOK
            }
            else -> {
                ServiceErrorEnum.UNKNOWN_REQUEST.data(type).throwout()
            }
        }
    }


    @RequestMapping(value = ["/get/resource/{type}/{id}"])
    fun getResource(@PathVariable type: String, @PathVariable id: Long, request: HttpServletRequest, response: HttpServletResponse): ResponseDataInterface {
        return when (type) {
//            "Avatar" -> {
//                if (!ResourcesUtils.resourceExists(ResourcesUtils.ResourceType.Avatar, id.toString())) {
//                    ResponseDataUtils.Error(ServiceErrorEnum.RESOURCE_NOT_FOUND.data(id))
//                } else {
//                    //ResponseDataUtils.writeResponseImage(response, ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, id.toString()))
//                    ResponseDataUtils.successData(ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, id.toString()).toBase64())
//                }
//            }
            else -> {
                ServiceErrorEnum.UNKNOWN_REQUEST.throwout()
            }
        }
    }


    @RequestMapping(value = ["/get/list/{type}"])
    fun getList(@PathVariable type: String, request: HttpServletRequest): ResponseDataInterface {
        val user: User = request.session.getAttribute("user") as User
        return when (type) {
            //获取好友分组列表
            "group" -> {
                val friendGroupList = LocalConfig.friendGroupService.getUserGroup(user.userID)
                        ?: arrayOf(Group.newDefaultGroup(user.userID))
                friendGroupList.responseOK
            }
            //获取好友列表
            "friend" -> {
                val friendList = LocalConfig.friendService.getFriendList(user.userID) ?: ArrayList()
                friendList.responseOK
            }
            //未知的请求
            else -> {
                ServiceErrorEnum.UNKNOWN_REQUEST.throwout()
            }
        }
    }


    @RequestMapping(value = ["/search"])
    fun search(request: HttpServletRequest): ResponseDataInterface {
        val self = request.session.getAttribute("user") as User
        val data = request.getParameter("data")
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val dataList = ArrayList<GuestUser>()
        data.toLongOrNull()?.let {
            LocalConfig.userService.getUserByID(it)?.let { user ->
                if (user.userID != self.userID) {
                    dataList.add(GuestUser.getInstance(user))
                }
            }
        }
        LocalConfig.userService.searchUserByNickname(data)?.forEach { user ->
            if (user.userID != self.userID && data.similar(user.nickname) >= 0.5) {
                dataList.add(GuestUser.getInstance(user))
            }
        }
        logger.info(dataList)
        return dataList.responseOK
    }

    @RequestMapping(value = ["/friend/{type}"])
    fun friend(@PathVariable type: String, request: HttpServletRequest): ResponseDataInterface {
        val user = request.session.getAttribute("user") as User
        val targetID = request.getParameter("targetID")?.toLongOrNull()
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("targetID").throwout()
        return when (type) {
            //添加好友
            "add" -> {
                var content = request.getParameter("content")
                        ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("content").throwout()
                try {
                    val json = content.toObjectNode()
                            ?: ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data(content).throwout()
                    json["groupID"].asLongOrNull()
                            ?: ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("No groupID or error format").throwout()
                    json["type"] = "REQUEST"
                    content = String.toJSON(json)
                } catch (e: Exception) {
                    ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("parse json object").throwout()
                }
                val message = ActionMessage.create(ActionTypeEnum.ADD_FRIEND_REQUEST, user.userID, targetID, null, content)
                LocalConfig.actionMessageService.newActionMessage(message)
                        ?: ServiceErrorEnum.IO_EXCEPTION.data("Create request message").throwout()
                targetID.responseOK
            }
            //删除好友
            "delete" -> {
                LocalConfig.friendService.deleteFriend(user.userID, targetID)
                        ?: ServiceErrorEnum.IO_EXCEPTION.data("deleteFriend").throwout()
                null.responseOK
            }
            //同意添加好友
            "accept" -> {
                val friendGroupID = request.getParameter("groupID")?.toLongOrNull()
                        ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("groupID").throwout()
                val messageID = request.getParameter("messageID")?.toLongOrNull()
                        ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("messageID").throwout()
                //获取要处理的消息
                val message = LocalConfig.actionMessageService[messageID]
                        ?: ServiceErrorEnum.MESSAGE_NOT_EXIST.data(messageID).throwout()
                //验证消息所有者(防止非法提交来获得他人的私人消息)
                if (!message.ownerVerify(user.userID)) {
                    ServiceErrorEnum.MESSAGE_NOT_ALLOWED.data(messageID).throwout()
                } else {
                    //获取被添加人在添加人中的分组
                    val userGroupID = message.context.toJsonNode()?.get("groupID")?.asLong()
                            ?: ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("groupID").throwout()
                    //写入好友数据表列表
                    LocalConfig.friendService.addFriend(message.fromID, message.toID, userGroupID, friendGroupID)
                            ?: ServiceErrorEnum.IO_EXCEPTION.data("addFriend").throwout()
                    //将该消息已读
                    LocalConfig.actionMessageService.read(messageID)
                            ?: ServiceErrorEnum.IO_EXCEPTION.data("readMessage").throwout()
                    //创造回执消息
                    val content = String.toJSON(object {
                        val type = "RESPONSE"
                        val requestID = messageID
                        val result = "ACCEPT"
                    })
                    val responseMessage = ActionMessage.create(ActionTypeEnum.ADD_FRIEND_REQUEST, message.toID, message.fromID, null, content)
                    LocalConfig.actionMessageService.newActionMessage(responseMessage)
                            ?: ServiceErrorEnum.IO_EXCEPTION.data("Create response message").throwout()
                    //尝试立即发送回执消息(若在线)
                    WebSocketHandler.trySendMessage(responseMessage)
                    Friend(message.fromID, friendGroupID).responseOK
                }
            }
            //拒绝添加好友
            "refuse" -> {
                val messageID = request.getParameter("messageID")?.toLongOrNull()
                        ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("messageID").throwout()
                val message = LocalConfig.actionMessageService[messageID]
                        ?: ServiceErrorEnum.MESSAGE_NOT_EXIST.data(messageID).throwout()
                if (!message.ownerVerify(user.userID)) {
                    ServiceErrorEnum.MESSAGE_NOT_ALLOWED.data(messageID).throwout()
                } else {
                    LocalConfig.actionMessageService.read(messageID)
                            ?: ServiceErrorEnum.IO_EXCEPTION.data("readMessage").throwout()
                    val content = String.toJSON(object {
                        val type = "RESPONSE"
                        val requestID = messageID
                        val result = "REFUSE"
                    })
                    val responseMessage = ActionMessage.create(ActionTypeEnum.ADD_FRIEND_REQUEST, message.toID, message.fromID, null, content)
                    LocalConfig.actionMessageService.newActionMessage(responseMessage)
                            ?: ServiceErrorEnum.IO_EXCEPTION.data("Create response message").throwout()
                    WebSocketHandler.trySendMessage(responseMessage)
                    null.responseOK
                }
            }
            //忽略添加请求
            "ignore" -> {
                val messageID = request.getParameter("messageID")?.toLongOrNull()
                        ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("messageID").throwout()
                val message = LocalConfig.actionMessageService[messageID]
                        ?: ServiceErrorEnum.MESSAGE_NOT_EXIST.data(messageID).throwout()
                if (!message.ownerVerify(user.userID)) {
                    ServiceErrorEnum.MESSAGE_NOT_ALLOWED.data(messageID).throwout()
                } else {
                    LocalConfig.actionMessageService.read(messageID)
                            ?: ServiceErrorEnum.IO_EXCEPTION.data("readMessage").throwout()
                    null.responseOK
                }
            }
            //未知请求
            else -> {
                ServiceErrorEnum.UNKNOWN_REQUEST.data(type).throwout()
            }
        }
    }


    @RequestMapping(value = ["/update"])
    fun update(request: HttpServletRequest): ResponseDataInterface {
        val user: User = request.session.getAttribute("user") as User
        val updateUser = user.readyToUpdate()
        var edited = false
        request.getParameter("signature")?.let {
            edited = true
            updateUser[User.UpdateUser.SIGNATURE_FIELD] = it
        }
        request.getParameter("nickname")?.let {
            if (it.isNotBlank()) {
                edited = true
                updateUser[User.UpdateUser.NICKNAME_FIELD] = it
            }
        }
        request.getParameter("email")?.let {
            if (it.isNotBlank()) {
                edited = true
                updateUser[User.UpdateUser.EMAIL_FIELD] = it
            }
        }
        request.getParameter("private-email")?.toBoolean()?.let {
            edited = true
            updateUser[User.UpdateUser.PRIVATEEMAIL_FIELD] = it
        }
        request.getParameter("phone")?.let {
            edited = true
            updateUser[User.UpdateUser.PHONE_FIELD] = it
        }
        request.getParameter("private-phone")?.toBoolean()?.let {
            edited = true
            updateUser[User.UpdateUser.PRIVATEPHONE_FIELD] = it
        }
        request.getParameter("sex")?.let {
            if (it.isNotBlank()) {
                edited = true
                updateUser[User.UpdateUser.SEX_FIELD] = it
            }
        }
        request.getParameter("private-sex")?.toBoolean()?.let {
            edited = true
            updateUser[User.UpdateUser.PRIVATESEX_FIELD] = it
        }
        if (!edited) {
            return user.responseOK
        }
        LocalConfig.userService.updateUser(updateUser)
                ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
        user.applyUpdate(updateUser)
        return user.responseOK
    }

    @RequestMapping(value = ["/update/avatar"])
    fun updateAvatar(request: HttpServletRequest): ResponseDataInterface {
        val user: User = request.session.getAttribute("user") as User
        if (request !is MultipartHttpServletRequest) {
            ServiceErrorEnum.UNKNOWN_REQUEST.throwout()
        }
        logger.info("收到上传命令")
        val fileNames = request.fileNames
        if (!fileNames.hasNext()) {
            ServiceErrorEnum.FILE_EMPTY.throwout()
        }
        val file = request.getFile(fileNames.next())
                ?: ServiceErrorEnum.FILE_EMPTY.throwout()
        if (file.size > 2048 * 1024) {
            ServiceErrorEnum.RESOURCE_TOO_BIG.throwout()
        }
        if (file.isEmpty) {
            ServiceErrorEnum.FILE_EMPTY.throwout()
        }
        if (file.contentType != "image/png" && file.contentType != "image/jpeg") {
            logger.info("contentType匹配失败:" + file.contentType)
            ServiceErrorEnum.RESOURCE_TYPE_MISMATCH.throwout()
        }
        val fileType = FilenameUtils.getExtension(file.originalFilename).toLowerCase()
        val fileName = if (fileType.isNotEmpty()) {
            user.userID.toString() + "." + fileType
        } else {
            user.userID.toString()
        }
        if (!SKImage.isImage(file)) {
            ServiceErrorEnum.RESOURCE_TYPE_MISMATCH.throwout()
        }
        ResourcesUtils.saveResource(ResourcesUtils.ResourceType.Avatar, fileName, file, true)
        val updateUser = User.readyToUpdate(user)
        updateUser.avatar = fileName
        LocalConfig.userService.updateUser(updateUser)
                ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
        val base64Data = ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, fileName).toBase64()
        user.avatar = base64Data
        return base64Data.responseOK
    }


    companion object {
        private val logger = LogManager.getLogger(UserController::class.java)
    }
}