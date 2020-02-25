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
    fun check(request: HttpServletRequest): ServiceErrorEnum {
        val username = request.getParameter("username")
        var password = request.getParameter("password")
        val ip = getIpAddress(request)

        val session = request.getSession(true)
        if (session.getAttribute("user") != null) {
            return ServiceErrorEnum.HAD_LOGGED_IN.data((session.getAttribute("user") as User).userID) //已经登录过
        }
        username ?: return ServiceErrorEnum.NULL_USERNAME //空的用户名
        password ?: return ServiceErrorEnum.NULL_PASSWORD //空的密码
        val user: User = LocalConfig.userService.getUserByUsername(username)
                ?: return ServiceErrorEnum.USERNAME_NOT_EXISTS.data(username) //错误的用户名
        val captcha = session.getAttribute("captcha") as Captcha?
        if (captcha != null) {
            val captchaString = request.getParameter("captcha")
                    ?: return ServiceErrorEnum.NULL_CAPTCHA //需要验证但是验证码为空
            if (!captcha.check(captchaString)) {
                return ServiceErrorEnum.WRONG_CAPTCHA.data(captchaString) //错误的验证码
            }
            session.removeAttribute("captcha")
            logger.info("验证码验证成功,移除验证码请求")
        } else {
            if (user.IP != null && ip != user.IP) {
                logger.info("不受信任的IP,需要验证登录")
                session.setAttribute("captcha", Captcha.getInstant(StaticConfig.captchaCount))
                return ServiceErrorEnum.NEED_VERIFY.data(ip) //不受信任的IP,需要验证
            }
        }
        password = password.replace(' ', '+')
        try {
            password = fromRequest(password)
        } catch (e: Exception) {
            return ServiceErrorEnum.UNKNOWN_PASSWORD //未知的密码串
        }
        logger.debug("解密的密码:$password")
        logger.debug("数据库得到的密码:" + user.password)
        if (!user.equalPassword(password)) {
            session.setAttribute("captcha", Captcha.getInstant(StaticConfig.captchaCount))
            return ServiceErrorEnum.WRONG_PASSWORD //错误的密码
        }
        return ServiceErrorEnum.NO_ERROR.data(username)
    }

}