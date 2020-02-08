package cn.skstudio.controller.public.user

import cn.skstudio.config.static.StaticConfig
import cn.skstudio.config.system.StartupConfig
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.pojo.Captcha
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
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
    fun captcha(session: HttpSession, response: HttpServletResponse) {
        val captcha: Captcha? = session.getAttribute("captcha") as Captcha?
        if (captcha == null) {
            logger.info("当前无需验证,错误的验证码请求")
            response.status = 403
            return
        }
        logger.info("请求验证码:" + captcha.captchaString)
        response.setDateHeader("Expires", 0)
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate")
        response.addHeader("Cache-Control", "post-check=0, pre-check=0")
        response.setHeader("Pragma", "no-cache")
        response.contentType = "image/jpeg"
        logger.info("输出验证码到流")
        try {
            response.outputStream.use { outputStream -> captcha.write(outputStream) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @RequestMapping(value = ["/test/captcha"])
    fun testCaptcha(response: HttpServletResponse) {
        val captcha: Captcha = Captcha.getInstant(4)
        logger.info("请求测试验证码:" + captcha.captchaString)
        response.setDateHeader("Expires", 0)
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate")
        response.addHeader("Cache-Control", "post-check=0, pre-check=0")
        response.setHeader("Pragma", "no-cache")
        response.contentType = "image/jpeg"
        logger.info("输出验证码到流")
        try {
            response.outputStream.use { outputStream -> captcha.write(outputStream) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
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