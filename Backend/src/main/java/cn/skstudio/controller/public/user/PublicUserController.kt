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
        return ResponseDataUtils.successData(StartupConfig.publicKey)
    }

    @RequestMapping(value = ["/captcha"])
    fun captcha(session: HttpSession, response: HttpServletResponse): String {
        val captcha: Captcha? = session.getAttribute("captcha") as Captcha?
        if (captcha == null) {
            logger.info("当前无需验证,错误的验证码请求")
            response.status = 403
            return ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST)
        }
        logger.info("请求验证码:" + captcha.captchaString)
        //ResponseDataUtils.writeResponseImage(response,captcha.skImage)
        return ResponseDataUtils.successData(captcha.skImage.toBase64())
    }

    @RequestMapping(value = ["/test/captcha"])
    fun testCaptcha(response: HttpServletResponse): String {
        val captcha: Captcha = Captcha.getInstant(4)
        logger.info("请求测试验证码:" + captcha.captchaString)
        logger.info("输出验证码到流")
        return ResponseDataUtils.successData(captcha.skImage.toBase64())
        //response.outputStream.use { outputStream -> captcha.write(outputStream) }
    }

    @RequestMapping(value = ["/newcaptcha"])
    fun newcaptcha(session: HttpSession, response: HttpServletResponse) {
        val captcha: Captcha? = session.getAttribute("captcha") as Captcha?
        if (captcha == null) {
            response.status = 403
            return
        }
        session.setAttribute("captcha", Captcha.getInstant(StaticConfig.captchaCount))
        logger.info("刷新验证码:" + (session.getAttribute("captcha") as Captcha).captchaString)
    }
}