package cn.skstudio.controller.user

import cn.skstudio.controller.websocket.WebSocketHandler
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.pojo.Group
import cn.skstudio.pojo.GuestUser
import cn.skstudio.pojo.User
import cn.skstudio.utils.*
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@RequestMapping("/api/user")
@RestController
class UserController {

    //region 登录相关方法

    @RequestMapping(value = ["/login"], produces = ["application/json; charset=utf-8"])
    fun login(request: HttpServletRequest): String {
        val session = request.getSession(true)
        val userChecker = UserChecker(request)
        val error = userChecker.check()
        if (!error.ok()) {
            logger.info("用户验证失败:$error")
            return ResponseDataUtils.Error(error)
        }
        val user: User = LocalConfig.userService.getUserByUsername(userChecker.username!!)!!
        logger.info(user.toString())
        val updateUser = User.readyToUpdate(user)
        updateUser.lastSessionID = session.id
        updateUser.IP = Network.getIpAddress(request)
        val result: Int? = LocalConfig.userService.updateLoginInfo(updateUser)
        if (result == null) {
            logger.warn("Update user login info failed")
        }
        logger.info("用户验证成功")
        session.setAttribute("user", user)
        session.maxInactiveInterval = 60 * 60
        return ResponseDataUtils.successData(user)
    }


    @RequestMapping(value = ["/logout"], produces = ["application/json; charset=utf-8"])
    fun logout(request: HttpServletRequest): String {
        val session = request.getSession(true)
        session.invalidate()
        return ResponseDataUtils.OK()
    }

    @RequestMapping(value = ["/relogin"], produces = ["application/json; charset=utf-8"])
    fun relogin(session: HttpSession): String {
        return ResponseDataUtils.successData(session.getAttribute("user"))
    }

    //endregion

    @RequestMapping(value = ["/onlinecount"])
    fun onlineCount(): String {
        return ResponseDataUtils.successData(WebSocketHandler.onlineCount)
    }

    @RequestMapping(value = ["/isAdmin"])
    fun isAdmin(session: HttpSession): String {
        return ResponseDataUtils.successData(session.getAttribute("admin"))
    }

    @RequestMapping(value = ["/get/{id}"])
    fun get(@PathVariable id: Long, request: HttpServletRequest): String {
        val user: User = LocalConfig.userService.getUserByID(id)
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.USERID_NOT_EXIST)
        return ResponseDataUtils.successData(GuestUser.getInstance(user))
    }

    @RequestMapping(value = ["/getGroup"])
    fun getGroup(request: HttpServletRequest): String {
        val user: User = request.session.getAttribute("user") as User
        val friendGroupList = LocalConfig.friendGroupService.getUserGroup(user.userID)
                ?: object : ArrayList<Group>() {
                    init {
                        Group.newDefaultGroup(user.userID)
                    }
                }
        return ResponseDataUtils.successData(friendGroupList)
    }

    @RequestMapping(value = ["/getFriend"])
    fun getFriend(request: HttpServletRequest): String {
        val user: User = request.session.getAttribute("user") as User
        val friendList = LocalConfig.friendService.getFriendList(user.userID) ?: ArrayList()
        return ResponseDataUtils.successData(friendList)
    }

    @RequestMapping(value = ["/update/{type}"])
    fun update(@PathVariable type: String, request: HttpServletRequest): String {
        when (type) {
            "sex" -> {
                return updateSex(request)
            }
            "nickname" -> {
                return updateNickname(request)
            }
            "password" -> {
                return updatePassword(request)
            }
            "email" -> {
                return updateEmail(request)
            }
            "phone" -> {
                return updatePhone(request)
            }
            "avatar" -> {
                return updateAvatar(request)
            }
            "signature" -> {
                return updateSignature(request)
            }
        }
        return ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST)
    }

    @RequestMapping(value = ["/update"])
    fun update(request: HttpServletRequest): String {
        return ResponseDataUtils.OK()
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

    private fun updateAvatar(request: HttpServletRequest?): String {
        return ResponseDataUtils.OK()
    }

    //endregion

    companion object {
        private val logger = LogManager.getLogger(UserController::class.java)
    }
}