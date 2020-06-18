package cn.skstudio.controller

import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.annotation.RequestLimit
import cn.skstudio.config.system.StaticConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.intf.ResponseDataInterface
import cn.skstudio.local.utils.ResourcesUtils
import cn.skstudio.local.utils.ResponseDataUtils.responseOK
import cn.skstudio.pojo.Group
import cn.skstudio.pojo.GuestUser
import cn.skstudio.pojo.SKImage
import cn.skstudio.pojo.User
import cn.skstudio.service.impl.FriendGroupServiceImpl
import cn.skstudio.service.impl.FriendServiceImpl
import cn.skstudio.service.impl.UserCheckerService
import cn.skstudio.service.impl.UserServiceImpl
import cn.skstudio.utils.IP
import cn.skstudio.utils.StringExtend.similar
import org.apache.commons.io.FilenameUtils
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartHttpServletRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@JsonRequestMapping(value = ["/api/user"])
@RestController
class UserController {

    @Autowired
    private lateinit var userCheckerService: UserCheckerService

    @Autowired
    private lateinit var userService: UserServiceImpl

    @Autowired
    private lateinit var friendGroupService: FriendGroupServiceImpl

    @Autowired
    private lateinit var friendService: FriendServiceImpl

    //region 登录相关方法

    //登录
    @RequestLimit(60, 10)
    @RequestMapping(value = ["/login"])
    fun login(request: HttpServletRequest, session: HttpSession): ResponseDataInterface {
        val user = userCheckerService.checkLogin(request, session)
        val updateUser = User.readyToUpdate(user)
        updateUser.lastSessionID = session.id
        updateUser.IP = request.IP
        if (userService.updateLoginInfo(updateUser) == null) {
            logger.warn("Update user login info failed")
        }
        user.applyUpdate(updateUser)
        session.setAttribute("user", user)
        session.maxInactiveInterval = 60 * 60
        return user.responseOK
    }

    //登出
    @RequestMapping(value = ["/logout"])
    fun logout(session: HttpSession): ResponseDataInterface {
        session.invalidate()
        return null.responseOK
    }

    //登录状态
    @RequestMapping(value = ["/status"])
    fun status(@SessionAttribute(name = "user") user: User): ResponseDataInterface {
        return user.responseOK
    }

    //endregion

    //在线人数
    @RequestMapping(value = ["/onlineCount"])
    fun onlineCount(): ResponseDataInterface {
        return WebSocketHandler.onlineCount.responseOK
    }

    //是否为管理员
    @RequestMapping(value = ["/isAdmin"])
    fun isAdmin(@SessionAttribute(name = "user") user: User): ResponseDataInterface {
        return user.admin.responseOK
    }

    //获取用户信息
    @RequestMapping(value = ["/get/id/{id}"])
    fun getUserInfo(@PathVariable id: Long): ResponseDataInterface {
        val user: User = userService.getUserByID(id)
                ?: ServiceErrorEnum.USER_ID_NOT_EXIST.throwout()
        return GuestUser.getInstance(user).responseOK
    }

    @RequestMapping(value = ["/get/resource/{type}/{id}"])
    fun getResource(@PathVariable type: String, @PathVariable id: Long, request: HttpServletRequest, response: HttpServletResponse): ResponseDataInterface {
        return when (type) {
            "Avatar" -> {
                if (!ResourcesUtils.resourceExists(ResourcesUtils.ResourceType.Avatar, id.toString())) {
                    ServiceErrorEnum.RESOURCE_NOT_FOUND.data(id).throwout()
                } else {
                    ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, id.toString()).toBase64().responseOK
                }
            }
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
                val friendGroupList = friendGroupService.getUserGroup(user.userID)
                        ?: arrayOf(Group.newDefaultGroup(user.userID))
                friendGroupList.responseOK
            }
            //获取好友列表
            "friend" -> {
                val friendList = friendService.getFriendList(user.userID) ?: ArrayList()
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
            userService.getUserByID(it)?.let { user ->
                if (user.userID != self.userID) {
                    dataList.add(GuestUser.getInstance(user))
                }
            }
        }
        userService.searchUserByNickname(data)?.forEach { user ->
            if (user.userID != self.userID && data.similar(user.nickname) >= 0.5) {
                dataList.add(GuestUser.getInstance(user))
            }
        }
        logger.info(dataList)
        return dataList.responseOK
    }

    @RequestMapping(value = ["/update"])
    fun update(@SessionAttribute(name = "user") user: User,
               @RequestParam(name = "signature", required = false) signature: String?,
               @RequestParam(name = "nickname", required = false) nickname: String?,
               @RequestParam(name = "email", required = false) email: String?,
               @RequestParam(name = "private-email", required = false) privateEmail: Boolean?,
               @RequestParam(name = "phone", required = false) phone: String?,
               @RequestParam(name = "private-phone", required = false) privatePhone: Boolean?,
               @RequestParam(name = "sex", required = false) sex: String?,
               @RequestParam(name = "private-sex", required = false) privateSex: Boolean?
    ): ResponseDataInterface {
        val updateUser = user.readyToUpdate()
        var edited = false
        signature?.let {
            edited = true
            updateUser.signature = it
        }
        nickname?.let {
            if (it.isNotBlank()) {
                edited = true
                updateUser.nickname = it
            }
        }
        email?.let {
            if (it.isNotBlank()) {
                edited = true
                updateUser.email = it
            }
        }
        privateEmail?.let {
            edited = true
            updateUser.privateEmail = it
        }
        phone?.let {
            edited = true
            updateUser.phone = it
        }
        privatePhone?.let {
            edited = true
            updateUser.privatePhone = it
        }
        sex?.let {
            if (it.isNotBlank()) {
                edited = true
                updateUser.sex = it
            }
        }
        privateSex?.let {
            edited = true
            updateUser.privateSex = it
        }
        if (!edited) {
            return user.responseOK
        }
        userService.updateUser(updateUser)
                ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
        user.applyUpdate(updateUser)
        return user.responseOK
    }

    @RequestMapping(value = ["/update/avatar"])
    fun updateAvatar(@SessionAttribute(name = "user") user: User,
                     request: HttpServletRequest): ResponseDataInterface {
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
        if (file.size > StaticConfig.maxAvatarSize) {
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
        userService.updateUser(updateUser)
                ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
        val base64Data = ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, fileName).toBase64()
        user.avatar = base64Data
        return base64Data.responseOK
    }


    companion object {
        private val logger = LogManager.getLogger(UserController::class.java)
    }
}