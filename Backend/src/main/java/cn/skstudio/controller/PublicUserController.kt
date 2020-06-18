package cn.skstudio.controller

/**
 * @author: Kairlec
 * @description: 公开的用户接口类
 */

import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.config.system.DatabaseConfig
import cn.skstudio.config.system.StaticConfig
import cn.skstudio.intf.ResponseDataInterface
import cn.skstudio.local.utils.ResponseDataUtils.responseOK
import cn.skstudio.pojo.Captcha
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute
import javax.servlet.http.HttpSession

@JsonRequestMapping(value = ["/api/public/user"])
@RestController
class PublicUserController {
    companion object {
        private val logger: Logger = LogManager.getLogger(PublicUserController::class.java)
    }

    @RequestMapping(value = ["/pk"])
    fun getPublicKey(): ResponseDataInterface {
        return DatabaseConfig.PublicKey.responseOK
    }

    @RequestMapping(value = ["/captcha"])
    fun captcha(@SessionAttribute(name = "captcha") captcha: Captcha): ResponseDataInterface {
        return captcha.skImage.toBase64().responseOK
    }

    @RequestMapping(value = ["/test/captcha"])
    fun testCaptcha(): ResponseDataInterface {
        return Captcha.getInstant(StaticConfig.captchaCount).skImage.toBase64().responseOK
    }

    @RequestMapping(value = ["/captcha/fresh"])
    fun freshCaptcha(@SessionAttribute(name = "captcha") captcha: Captcha,
                     session: HttpSession): ResponseDataInterface {
        val newCaptcha = Captcha.getInstant(StaticConfig.captchaCount)
        session.setAttribute("captcha", newCaptcha)
        return newCaptcha.skImage.toBase64().responseOK
    }
}