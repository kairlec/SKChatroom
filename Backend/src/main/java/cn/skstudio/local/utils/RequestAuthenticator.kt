package cn.skstudio.local.utils

import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.User
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

class RequestAuthenticator {
    companion object {
        private val logger: Logger = LogManager.getLogger(RequestAuthenticator::class.java)
        fun authHttpSession(session: HttpSession): ServiceErrorEnum {
            logger.info("获取到session,ID=" + session.id)
            val user: User? = session.getAttribute("user") as? User
            return if (user == null) {
                logger.info("新打开session或未找到已有用户")
                logger.info("无法处理的未登录请求")
                ServiceErrorEnum.NOT_LOGGED_IN
            } else {
                logger.info("""在Session取到了"${user.username}"的用户名""")
                val targetUser: User? = LocalConfig.userService.getUserByUsername(user.username)
                if (targetUser == null) {
                    logger.info("新打开session或未找到已有用户")
                    logger.info("无法处理的未登录请求")
                    ServiceErrorEnum.NOT_LOGGED_IN
                }
                if (targetUser!!.lastSessionID != session.id) {
                    session.invalidate()
                    ServiceErrorEnum.EXPIRED_LOGIN //已登录状态与上一次的登录SessionID不一致,表示在其他地方登录,老的被挤下线
                }
                session.setAttribute("user", targetUser)
                session.maxInactiveInterval = 60 * 60
                if (targetUser.admin!!) {
                    ServiceErrorEnum.NO_ERROR.data("UserAdmin")
                } else {
                    ServiceErrorEnum.NO_ERROR.data("NormalUser")
                }
            }
        }

        fun authHttpServletRequest(request: HttpServletRequest, blackAPIList: List<String>): ServiceErrorEnum {
            if (!request.method.equals("POST", ignoreCase = true)) {
                return ServiceErrorEnum.UNKNOWN_REQUEST
            }
            val requestUrl = URLDecoder.decode(request.requestURI, StandardCharsets.UTF_8)
            val session = request.getSession(true)
            if (blackAPIList.contains(requestUrl)) {
                return ServiceErrorEnum.NO_ERROR.data("BlackList")
            }
            return authHttpSession(session)
        }
    }
}