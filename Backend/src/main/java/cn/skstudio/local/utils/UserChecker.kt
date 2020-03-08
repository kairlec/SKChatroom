package cn.skstudio.local.utils

import cn.skstudio.config.static.StaticConfig
import cn.skstudio.controller.public.activated.ActivatedController.Companion.logger
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.Captcha
import cn.skstudio.pojo.User
import cn.skstudio.utils.Network.getIpAddress
import cn.skstudio.utils.PasswordCoder.fromRequest
import javax.servlet.http.HttpServletRequest

object UserChecker {
    fun check(request: HttpServletRequest): User {
        val username = request.getParameter("username")
        var password = request.getParameter("password")
        val ip = getIpAddress(request)

        val session = request.getSession(true)
        if (session.getAttribute("user") != null) {
            ServiceErrorEnum.HAD_LOGGED_IN.data((session.getAttribute("user") as User).userID).throwout()
        }
        username ?: ServiceErrorEnum.NULL_USERNAME.throwout()//空的用户名
        password ?: ServiceErrorEnum.NULL_PASSWORD.throwout()//空的密码
        val user: User = LocalConfig.userService.getUserByUsername(username)
                ?: ServiceErrorEnum.USERNAME_NOT_EXISTS.data(username).throwout() //错误的用户名
        val captcha = session.getAttribute("captcha") as Captcha?
        if (captcha != null) {
            val captchaString = request.getParameter("captcha")
                    ?: ServiceErrorEnum.NULL_CAPTCHA.throwout() //需要验证但是验证码为空
            if (!captcha.check(captchaString)) {
                ServiceErrorEnum.WRONG_CAPTCHA.data(captchaString).throwout()//错误的验证码
            }
            session.removeAttribute("captcha")
        } else {
            if (user.IP != null && ip != user.IP) {
                logger.info("不受信任的IP,需要验证登录")
                session.setAttribute("captcha", Captcha.getInstant(StaticConfig.captchaCount))
                ServiceErrorEnum.NEED_VERIFY.data(ip).throwout() //不受信任的IP,需要验证
            }
        }
        password = password.replace(' ', '+')
        try {
            password = fromRequest(password)
        } catch (e: Exception) {
            ServiceErrorEnum.UNKNOWN_PASSWORD.throwout() //未知的密码串
        }
        if (!user.equalPassword(password)) {
            session.setAttribute("captcha", Captcha.getInstant(StaticConfig.captchaCount))
            ServiceErrorEnum.WRONG_PASSWORD.throwout() //错误的密码
        }
        return user
    }

}