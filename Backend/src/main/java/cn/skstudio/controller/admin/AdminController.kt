package cn.skstudio.controller.admin


import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.pojo.MailSender
import cn.skstudio.local.utils.LocalConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RequestMapping("/api/admin")
@RestController
class AdminController {
    @RequestMapping(value = ["/get/{type}"])
    fun get(@PathVariable type: String, request: HttpServletRequest): String {
        when (type) {
            "mailSender" -> {
                val mailSender = LocalConfig.mailSenderService.getMailSender()
                        ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
                return ResponseDataUtils.successData(mailSender)
            }
        }
        return ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST)
    }

    @RequestMapping(value = ["/update/{type}"])
    fun update(@PathVariable type: String, request: HttpServletRequest): String {
        when (type) {
            "mailSender" -> return updateMailSenderConfig(request)
        }
        return ResponseDataUtils.Error(ServiceErrorEnum.UNKNOWN_REQUEST)
    }

    private fun updateMailSenderConfig(request: HttpServletRequest): String {
        val port = request.getParameter("port")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val host = request.getParameter("host")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val protocol = request.getParameter("protocol")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val encoding = request.getParameter("encoding")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val username = request.getParameter("username")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val password = request.getParameter("password")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val mailSender = MailSender(port.toInt(), host, protocol, encoding, username, password)
        LocalConfig.mailSenderService.updateMailSender(mailSender)
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        return ResponseDataUtils.successData(mailSender)
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(AdminController::class.java)
    }
}