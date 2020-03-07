package cn.skstudio.controller.user

import cn.skstudio.annotation.RequestLimit
import cn.skstudio.controller.websocket.WebSocketHandler
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.local.utils.ResourcesUtils
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.local.utils.UserChecker
import cn.skstudio.pojo.*
import cn.skstudio.utils.Network
import cn.skstudio.utils.PasswordCoder
import cn.skstudio.utils.StringExtend.similar
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.apache.commons.io.FilenameUtils
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartHttpServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@RequestMapping("/api/user")
@RestController
class UserController {

    //region 登录相关方法


    //登录
    @RequestLimit(60, 10)
    @RequestMapping(value = ["/login"])
    fun login(request: HttpServletRequest): String {
        val session = request.session
        val error = UserChecker.check(request)
        if (!error.ok()) {
            logger.info("用户验证失败:$error")
            return ResponseDataUtils.Error(error)
        }
        //验证已经通过表示存在用户,下面的user直接取非null
        val user: User = LocalConfig.userService.getUserByUsername(error.data as String)!!
        logger.info(user.toString())
        val updateUser = User.readyToUpdate(user)
        updateUser.lastSessionID = session.id
        updateUser.IP = Network.getIpAddress(request)
        if (LocalConfig.userService.updateLoginInfo(updateUser) == null) {
            logger.warn("Update user login info failed")
        }
        logger.info("用户验证成功")
        logger.info(user.avatar)
        if (user.avatar != null && user.avatar != "@DEFAULT?") {
            val base64Data = ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, user.avatar!!).toBase64()
            user.avatar = base64Data
            logger.info(user.avatar)
        }
        session.setAttribute("user", user)
        session.maxInactiveInterval = 60 * 60
        return ResponseDataUtils.successData(user)
    }

    //登出
    @RequestMapping(value = ["/logout"])
    fun logout(request: HttpServletRequest): String {
        request.session.invalidate()
        return ResponseDataUtils.OK()
    }

    //登录状态
    @RequestMapping(value = ["/relogin"])
    fun relogin(session: HttpSession): String {
        logger.info((session.getAttribute("user") as User).avatar)
        return ResponseDataUtils.successData(session.getAttribute("user"))
    }

    //endregion

    //在线人数
    @RequestMapping(value = ["/onlineCount"])
    fun onlineCount(): String {
        return ResponseDataUtils.successData(WebSocketHandler.onlineCount)
    }

    //是否为管理员
    @RequestMapping(value = ["/isAdmin"])
    fun isAdmin(session: HttpSession): String {
        return ResponseDataUtils.successData(session.getAttribute("admin"))
    }

    //获取用户信息
    @RequestMapping(value = ["/get/id/{id}"])
    fun getUserInfo(@PathVariable id: Long): String {
        val user: User = LocalConfig.userService.getUserByID(id)
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.USER_ID_NOT_EXIST)
        if (user.avatar != null && user.avatar != "@DEFAULT?") {
            val base64Data = ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, user.avatar!!).toBase64()
            user.avatar = base64Data
        }
        return ResponseDataUtils.successData(GuestUser.getInstance(user))
    }

    //消息处理接口
    @RequestMapping(value = ["/message/{type}/{typeInfo}"])
    fun message(@PathVariable type: String, request: HttpServletRequest, @PathVariable typeInfo: String): String {
        val user = request.session.getAttribute("user") as User
        return when (type) {
            //获取消息
            "get" -> {
                return when (typeInfo) {
                    "unreadTo" -> {
                        val messages = LocalConfig.actionMessageService.getAllUnreadToActionMessages(user.userID)
                                ?: ArrayList()
                        logger.info(messages)
                        logger.info(JSON.toJSONString(messages))
                        ResponseDataUtils.successData(messages)
                    }
                    //TODO 历史消息
                    "" -> {
                        ""
                    }
                    else -> {
                        ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST.data(typeInfo))
                    }
                }
            }
            "read" -> {
                val messageID: Long
                try {
                    messageID = typeInfo.toLong()
                } catch (e: java.lang.NumberFormatException) {
                    e.printStackTrace()
                    return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID.data(typeInfo))
                }
                val message = LocalConfig.actionMessageService[messageID]
                        ?: return ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_NOT_EXIST)
                if (!message.ownerVerify(user.userID)) {
                    return ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_NOT_ALLOWED)
                }
                if (LocalConfig.actionMessageService.read(message) != null) {
                    ResponseDataUtils.successData(message.messageID)
                }
                ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
            }
            else -> {
                ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST.data(type))
            }
        }
    }


    @RequestMapping(value = ["/get/resource/{type}/{id}"])
    fun getResource(@PathVariable type: String, @PathVariable id: Long, request: HttpServletRequest, response: HttpServletResponse): String {
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
                ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST)
            }
        }
    }


    @RequestMapping(value = ["/get/list/{type}"])
    fun getList(@PathVariable type: String, request: HttpServletRequest): String {
        val user: User = request.session.getAttribute("user") as User
        return when (type) {
            //获取好友分组列表
            "group" -> {
                val friendGroupList = LocalConfig.friendGroupService.getUserGroup(user.userID)
                        ?: object : ArrayList<Group>() {
                            init {
                                Group.newDefaultGroup(user.userID)
                            }
                        }
                ResponseDataUtils.successData(friendGroupList)
            }
            //获取好友列表
            "friend" -> {
                val friendList = LocalConfig.friendService.getFriendList(user.userID) ?: ArrayList()
                ResponseDataUtils.successData(friendList)
            }
            //未知的请求
            else -> {
                ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST)
            }
        }
    }


    @RequestMapping(value = ["/search"])
    fun search(request: HttpServletRequest): String {
        val self = request.session.getAttribute("user") as User
        val data = request.getParameter("data")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val dataList = ArrayList<GuestUser>()
        val dataID = data.toLongOrNull()
        if (dataID != null) {
            val user = LocalConfig.userService.getUserByID(dataID)
            if (user != null && user.userID != self.userID) {
                val guest = GuestUser.getInstance(user)
                if (guest.avatar != "@DEFAULT?") {
                    val base64Data = ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, guest.avatar).toBase64()
                    guest.avatar = base64Data
                }
                dataList.add(guest)
            }
        }
        val list = LocalConfig.userService.searchUserByNickname(data)
        if (list != null) {
            for (user in list) {
                if (user.userID != self.userID) {
                    if (data.similar(user.nickname) < 0.5) {
                        continue
                    }
                    val guest = GuestUser.getInstance(user)
                    if (guest.avatar != "@DEFAULT?") {
                        val base64Data = ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, guest.avatar).toBase64()
                        guest.avatar = base64Data
                    }
                    dataList.add(guest)
                }
            }
        }
        logger.info(dataList)
        return ResponseDataUtils.successData(dataList)
    }

    @RequestMapping(value = ["/friend/{type}"])
    fun friend(@PathVariable type: String, request: HttpServletRequest): String {
        val user = request.session.getAttribute("user") as User
        val targetID: Long
        try {
            targetID = request.getParameter("targetID")?.toLong()
                    ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("targetID"))
        } catch (e: NumberFormatException) {
            return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID.data(request.getParameter("targetID")))
        }
        return when (type) {
            //添加好友
            "add" -> {
                var content = request.getParameter("content")
                        ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("content"))
                try {
                    val json = JSON.parseObject(content)
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data(content))
                    json.getObject("groupID", Long::class.java)
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("No groupID"))
                    json["type"] = "REQUEST"
                    content = json.toJSONString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("parse json object"))
                }
                val message = ActionMessage.create(ActionTypeEnum.ADD_FRIEND_REQUEST, user.userID, targetID, null, content)
                LocalConfig.actionMessageService.newActionMessage(message)
                        ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION.data("Create request message"))
                ResponseDataUtils.successData(targetID)
            }
            //删除好友
            "delete" -> {
                LocalConfig.friendService.deleteFriend(user.userID, targetID)
                        ?: ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION.data("deleteFriend"))
                ResponseDataUtils.OK()
            }
            //同意添加好友
            "accept" -> {
                val friendGroupID: Long
                try {
                    //获取添加人在被添加人中的分组
                    friendGroupID = request.getParameter("groupID")?.toLong()
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("groupID"))
                } catch (e: NumberFormatException) {
                    return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID.data(request.getParameter("groupID")))
                }
                val messageID: Long
                try {
                    //获取要处理的消息ID
                    messageID = request.getParameter("messageID")?.toLong()
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("messageID"))
                } catch (e: NumberFormatException) {
                    return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID.data(request.getParameter("messageID")))
                }
                //获取要处理的消息
                val message = LocalConfig.actionMessageService[messageID]
                        ?: return ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_NOT_EXIST.data(messageID))
                //验证消息所有者(防止非法提交来获得他人的私人消息)
                if (!message.ownerVerify(user.userID)) {
                    ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_NOT_ALLOWED.data(messageID))
                } else {
                    //获取被添加人在添加人中的分组
                    val userGroupID = JSON.parseObject(message.context)?.getObject("groupID", Long::class.java)
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_WRONG_FORMAT.data("groupID"))
                    //写入好友数据表列表
                    LocalConfig.friendService.addFriend(message.fromID, message.toID, userGroupID, friendGroupID)
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION.data("addFriend"))
                    //将该消息已读
                    LocalConfig.actionMessageService.read(messageID)
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION.data("readMessage"))
                    //创造回执消息
                    val content = JSONObject()
                    content["type"] = "RESPONSE"
                    content["requestID"] = messageID
                    content["result"] = "ACCEPT"
                    val responseMessage = ActionMessage.create(ActionTypeEnum.ADD_FRIEND_REQUEST, message.toID, message.fromID, null, content.toJSONString())
                    LocalConfig.actionMessageService.newActionMessage(responseMessage)
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION.data("Create response message"))
                    //尝试立即发送回执消息(若在线)
                    WebSocketHandler.trySendMessage(responseMessage)
                    ResponseDataUtils.successData(Friend(message.fromID, friendGroupID))
                }
            }
            //拒绝添加好友
            "refuse" -> {
                val messageID: Long
                try {
                    messageID = request.getParameter("messageID")?.toLong()
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("messageID"))
                } catch (e: NumberFormatException) {
                    return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID.data(request.getParameter("messageID")))
                }
                val message = LocalConfig.actionMessageService[messageID]
                        ?: return ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_NOT_EXIST.data(messageID))
                if (!message.ownerVerify(user.userID)) {
                    ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_NOT_ALLOWED.data(messageID))
                } else {
                    LocalConfig.actionMessageService.read(messageID)
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION.data("readMessage"))
                    val content = JSONObject()
                    content["type"] = "RESPONSE"
                    content["requestID"] = messageID
                    content["result"] = "REFUSE"
                    val responseMessage = ActionMessage.create(ActionTypeEnum.ADD_FRIEND_REQUEST, message.toID, message.fromID, null, content.toJSONString())
                    LocalConfig.actionMessageService.newActionMessage(responseMessage)
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION.data("Create response message"))
                    WebSocketHandler.trySendMessage(responseMessage)
                    ResponseDataUtils.OK()
                }
            }
            //忽略添加请求
            "ignore" -> {
                val messageID: Long
                try {
                    messageID = request.getParameter("messageID")?.toLong()
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS.data("messageID"))
                } catch (e: NumberFormatException) {
                    return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID.data(request.getParameter("messageID")))
                }
                val message = LocalConfig.actionMessageService[messageID]
                        ?: return ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_NOT_EXIST.data(messageID))
                if (!message.ownerVerify(user.userID)) {
                    ResponseDataUtils.Error(ServiceErrorEnum.MESSAGE_NOT_ALLOWED.data(messageID))
                } else {
                    LocalConfig.actionMessageService.read(messageID)
                            ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION.data("readMessage"))
                    ResponseDataUtils.OK()
                }
            }
            //未知请求
            else -> {
                ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST.data(type))
            }
        }
    }

    @RequestMapping(value = ["/update/{type}"])
    fun update(@PathVariable type: String, request: HttpServletRequest): String {
        return when (type) {
            //更新性别
            "sex" -> {
                updateSex(request)
            }
            //更新昵称
            "nickname" -> {
                updateNickname(request)
            }
            //更新密码
            "password" -> {
                updatePassword(request)
            }
            //更新邮箱
            "email" -> {
                updateEmail(request)
            }
            //更新电话号码
            "phone" -> {
                updatePhone(request)
            }
            //更新头像
            "avatar" -> {
                updateAvatar(request)
            }
            //更新签名
            "signature" -> {
                updateSignature(request)
            }
            //未知请求
            else -> {
                ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST)
            }
        }
    }

    @RequestMapping(value = ["/update"])
    fun update(request: HttpServletRequest): String {
        val signature = request.getParameter("signature")
        val nickname = request.getParameter("nickname")
        val email = request.getParameter("email")
        val privateEmail = request.getParameter("private-email")?.toBoolean()
        val phone = request.getParameter("phone")
        val privatePhone = request.getParameter("private-phone")?.toBoolean()
        val sex = request.getParameter("sex")
        val privateSex = request.getParameter("private-sex")?.toBoolean()
        val user: User = request.session.getAttribute("user") as User
        val updateUser = user.readyToUpdate()
        var error: ServiceErrorEnum
        var edited = false

        if (email != null && email.isNotBlank()) {
            edited = true
            error = updateUser.updateEmail(email)
            if (!error.ok()) {
                return ResponseDataUtils.Error(error)
            }
        }
        if (privateEmail != null) {
            edited = true
            updateUser.privateEmail = privateEmail
        }
        if (nickname != null && nickname.isNotBlank()) {
            edited = true
            error = updateUser.updateNickname(nickname)
            if (!error.ok()) {
                return ResponseDataUtils.Error(error)
            }
        }
        if (phone != null) {
            edited = true
            error = updateUser.updatePhone(phone)
            if (!error.ok()) {
                return ResponseDataUtils.Error(error)
            }
        }
        if (privatePhone != null) {
            edited = true
            updateUser.privatePhone = privatePhone
        }
        if (sex != null && sex.isNotBlank()) {
            edited = true
            error = updateUser.updateSex(sex)
            if (!error.ok()) {
                return ResponseDataUtils.Error(error)
            }
        }
        if (privateSex != null) {
            edited = true
            updateUser.privateSex = privateSex
        }
        if (signature != null) {
            edited = true
            error = updateUser.updateSignature(signature)
            if (!error.ok()) {
                return ResponseDataUtils.Error(error)
            }
        }
        if (!edited) {
            return ResponseDataUtils.successData(user)
        }
        LocalConfig.userService.updateUser(updateUser)
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        user.signature = signature ?: user.signature
        user.nickname = nickname ?: user.nickname
        user.email = email ?: user.email
        user.sex = sex ?: user.sex
        user.phone = phone ?: user.phone
        user.privateEmail = privateEmail ?: user.privateEmail
        user.privateSex = privateSex ?: user.privateSex
        user.privatePhone = privatePhone ?: user.privatePhone
        return ResponseDataUtils.successData(user)
    }

    //region 详细信息更新方法


    private fun updateSignature(request: HttpServletRequest): String {
        val signature = request.getParameter("signature")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val user: User = request.session.getAttribute("user") as User
        val updateUser = user.readyToUpdate()
        val error = updateUser.updateSignature(signature)
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        LocalConfig.userService.updateUser(updateUser) ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        user.signature = signature
        return ResponseDataUtils.successData(user.signature)
    }

    private fun updateNickname(request: HttpServletRequest): String {
        val nickname = request.getParameter("nickname")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val user: User = request.session.getAttribute("user") as User
        val updateUser = user.readyToUpdate()
        val error = updateUser.updateNickname(nickname)
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        LocalConfig.userService.updateUser(updateUser) ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        user.nickname = nickname.trim { it <= ' ' }
        return ResponseDataUtils.successData(user.nickname)
    }

    private fun updatePassword(request: HttpServletRequest): String {
        val password = request.getParameter("password")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val user: User = request.session.getAttribute("user") as User
        val updateUser = User.readyToUpdate(user)
        val error = updateUser.updatePassword(PasswordCoder.fromRequest(password))
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        LocalConfig.userService.updatePassword(updateUser)
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        user.password = password.trim { it <= ' ' }
        return ResponseDataUtils.OK()
    }

    private fun updateEmail(request: HttpServletRequest): String {
        val email = request.getParameter("email")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val user: User = request.session.getAttribute("user") as User
        val updateUser = User.readyToUpdate(user)
        val error = updateUser.updateEmail(email)
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        LocalConfig.userService.updateUser(updateUser) ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        user.email = email
        return ResponseDataUtils.successData(email)
    }

    private fun updateSex(request: HttpServletRequest): String {
        val sex = request.getParameter("sex")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val user: User = request.session.getAttribute("user") as User
        val updateUser = User.readyToUpdate(user)
        val error = updateUser.updateSex(sex)
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        LocalConfig.userService.updateUser(updateUser) ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        user.sex = sex

        return ResponseDataUtils.successData(sex)
    }

    private fun updatePhone(request: HttpServletRequest): String {
        val phone = request.getParameter("phone") ?: ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val user: User = request.session.getAttribute("user") as User
        val updateUser = User.readyToUpdate(user)
        val error = updateUser.updatePhone(phone)
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        LocalConfig.userService.updateUser(updateUser) ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        user.phone = phone
        return ResponseDataUtils.successData(phone)
    }

    private fun updateAvatar(request: HttpServletRequest): String {
        val user: User = request.session.getAttribute("user") as User
        val multiRequest = request as MultipartHttpServletRequest
        logger.info("收到上传命令")
        val fileNames = multiRequest.fileNames
        if (!fileNames.hasNext()) {
            return ResponseDataUtils.Error(ServiceErrorEnum.FILE_EMPTY)
        }
        val file = multiRequest.getFile(fileNames.next()) ?: return ResponseDataUtils.Error(ServiceErrorEnum.FILE_EMPTY)
        if (file.size > 2048 * 1024) {
            return ResponseDataUtils.Error(ServiceErrorEnum.RESOURCE_TOO_BIG)
        }
        if (file.isEmpty) {
            return ResponseDataUtils.Error(ServiceErrorEnum.FILE_EMPTY)
        }
        if (file.contentType != "image/png" && file.contentType != "image/jpeg") {
            logger.info("contentType匹配失败:" + file.contentType)
            return ResponseDataUtils.Error(ServiceErrorEnum.RESOURCE_TYPE_MISMATCH)
        }
        val fileType = FilenameUtils.getExtension(file.originalFilename).toLowerCase()
        val fileName = if (fileType.isNotEmpty()) {
            user.userID.toString() + "." + fileType
        } else {
            user.userID.toString()
        }
        if (!SKImage.isImage(file)) {
            logger.info("图像流识别失败")
            return ResponseDataUtils.Error(ServiceErrorEnum.RESOURCE_TYPE_MISMATCH)
        }
        ResourcesUtils.saveResource(ResourcesUtils.ResourceType.Avatar, fileName, file, true)
        val updateUser = User.readyToUpdate(user)
        updateUser.avatar = fileName
        LocalConfig.userService.updateUser(updateUser) ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        val base64Data = ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, fileName).toBase64()
        user.avatar = base64Data
        return ResponseDataUtils.OK(base64Data)
    }

    //endregion

    companion object {
        private val logger = LogManager.getLogger(UserController::class.java)
    }
}