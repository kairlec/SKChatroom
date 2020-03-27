package cn.skstudio.local.utils

import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.User
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

object RequestAuthenticator {
    private val logger: Logger = LogManager.getLogger(RequestAuthenticator::class.java)
    fun authHttpSession(session: HttpSession): ServiceErrorEnum {
        (session.getAttribute("user") as? User)?.let { user ->
            logger.info("""在Session取到了"${user.username}"的用户名""")
            return when{
                //ID不一样,在其他地方登录,挤下线
                user.lastSessionID != session.id->{
                    session.invalidate()
                    ServiceErrorEnum.EXPIRED_LOGIN
                }
                user.admin->{
                    ServiceErrorEnum.NO_ERROR.data("UserAdmin")
                }
                else->{
                    ServiceErrorEnum.NO_ERROR.data("NormalUser")
                }
            }
        }
        return ServiceErrorEnum.NOT_LOGGED_IN
    }

    fun authHttpServletRequest(request: HttpServletRequest, blackAPIList: Array<String>): ServiceErrorEnum {
        if (!request.method.equals("POST", ignoreCase = true)) {
            return ServiceErrorEnum.UNKNOWN_REQUEST
        }
        val requestUrl = URLDecoder.decode(request.requestURI, StandardCharsets.UTF_8)
        val session = request.getSession(true)
        if (requestUrl in blackAPIList) {
            return ServiceErrorEnum.NO_ERROR.data("BlackList")
        }
        return authHttpSession(session)
    }
}