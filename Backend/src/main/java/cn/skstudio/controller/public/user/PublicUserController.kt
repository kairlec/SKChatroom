package cn.skstudio.controller.public.user

/**
 * @author: Kairlec
 * @description: 公开的用户接口类
 */

import cn.skstudio.config.static.StaticConfig
import cn.skstudio.config.system.StartupConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.pojo.Captcha
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@RequestMapping("/api/public/user")
@RestController
class PublicUserController {
    companion object {
        private val logger: Logger = LogManager.getLogger(PublicUserController::class.java)
    }

    @RequestMapping(value = ["/pk"])
    fun getPublicKey(): String {
        return ResponseDataUtils.OK(StartupConfig.publicKey)
    }

    @RequestMapping(value = ["/captcha"])
    fun captcha(session: HttpSession, response: HttpServletResponse): String {
        val captcha = session.getAttribute("captcha")
        if (captcha is Captcha) {
            return ResponseDataUtils.OK(captcha.skImage.toBase64())
        }
        response.status = 403
        ServiceErrorEnum.UNKNOWN_REQUEST.throwout()
    }

    @RequestMapping(value = ["/test/captcha"])
    fun testCaptcha(): String {
        val captcha: Captcha = Captcha.getInstant(StaticConfig.captchaCount)
        return ResponseDataUtils.successData(captcha.skImage.toBase64())
    }

    @RequestMapping(value = ["/newcaptcha"])
    fun newcaptcha(session: HttpSession, response: HttpServletResponse):String {
        val captcha = session.getAttribute("captcha")
        if (captcha is Captcha) {
            val newCaptcha = Captcha.getInstant(StaticConfig.captchaCount)
            session.setAttribute("captcha", newCaptcha)
            return ResponseDataUtils.successData(newCaptcha.skImage.toBase64())
        }
        response.status = 403
        ServiceErrorEnum.UNKNOWN_REQUEST.throwout()
    }
}