package cn.skstudio.controller.admin


import cn.skstudio.`interface`.ResponseDataInterface
import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.pojo.MailSender
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@JsonRequestMapping("/api/admin")
@RestController
class AdminController {

    /**
     * @description: 获取相应的配置项内容
     * @return: 相应配置项的json字符串
     */
    @RequestMapping(value = ["/get/{type}"])
    fun get(@PathVariable type: String): ResponseDataInterface {
        return when (type) {
            "mailSender" -> {
                val mailSender = LocalConfig.mailSenderService.getMailSender()
                        ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
                ResponseDataUtils.ok(mailSender)
            }
            else -> {
                ServiceErrorEnum.UNKNOWN_REQUEST.throwout()
            }
        }
    }

    /**
     * @description: 更新相应配置项内容
     * @return: 更新状态(成功更新或失败)
     */
    @RequestMapping(value = ["/update/{type}"])
    fun update(@PathVariable type: String, request: HttpServletRequest): ResponseDataInterface {
        return when (type) {
            "mailSender" -> updateMailSenderConfig(request)
            else -> {
                ServiceErrorEnum.UNKNOWN_REQUEST.throwout()
            }
        }
    }

    /**
     * @description: 更新邮件发送配置
     * @return:更新成功与否的字符串
     */
    private fun updateMailSenderConfig(request: HttpServletRequest): ResponseDataInterface {
        val port = request.getParameter("port")?.toIntOrNull()
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val host = request.getParameter("host")
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val protocol = request.getParameter("protocol")
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val encoding = request.getParameter("encoding")
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val username = request.getParameter("username")
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val password = request.getParameter("password")
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val enable = request.getParameter("enable")?.toBoolean()
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val mailSender = MailSender(port, host, protocol, encoding, username, password, enable)
        LocalConfig.mailSenderService.updateMailSender(mailSender)
                ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
        return ResponseDataUtils.ok(mailSender)
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(AdminController::class.java)
    }
}