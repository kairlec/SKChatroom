package cn.skstudio.controller


import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.intf.ResponseDataInterface
import cn.skstudio.local.utils.ResponseDataUtils.responseOK
import cn.skstudio.pojo.MailSender
import cn.skstudio.service.impl.MailSenderServiceImpl
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@JsonRequestMapping("/api/admin")
@RestController
class AdminController {

    @Autowired
    private lateinit var mailSenderService: MailSenderServiceImpl


    /**
     * @description: 获取相应的配置项内容
     * @return: 相应配置项的json字符串
     */
    @RequestMapping(value = ["/get/{type}"])
    fun get(@PathVariable type: String): ResponseDataInterface {
        return when (type) {
            "mailSender" -> {
                val mailSender = mailSenderService.getMailSender()
                        ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
                mailSender.responseOK
            }
            else -> {
                ServiceErrorEnum.UNKNOWN_REQUEST.throwout()
            }
        }
    }

    /**
     * @description: 更新邮件发送配置
     * @return:更新成功与否的字符串
     */
    @RequestMapping(value = ["/update/mailSender"])
    fun updateMailSenderConfig(@RequestParam(name = "port") port: Int,
                               @RequestParam(name = "host") host: String,
                               @RequestParam(name = "protocol") protocol: String,
                               @RequestParam(name = "encoding") encoding: String,
                               @RequestParam(name = "username") username: String,
                               @RequestParam(name = "password") password: String,
                               @RequestParam(name = "enable") enable: Boolean): ResponseDataInterface {
        val mailSender = MailSender(port, host, protocol, encoding, username, password, enable)
        mailSenderService.updateMailSender(mailSender)
                ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
        return mailSender.responseOK
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(AdminController::class.java)
    }
}