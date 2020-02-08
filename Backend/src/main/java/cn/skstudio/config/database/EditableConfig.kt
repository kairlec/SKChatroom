package cn.skstudio.config.database

import cn.skstudio.pojo.MailSender
import cn.skstudio.utils.LocalConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class EditableConfig {


    companion object {
        private val logger: Logger = LogManager.getLogger(EditableConfig::class.java)
        private var _mailSender: MailSender? = null
        var mailSender: MailSender
            get() {
                if (_mailSender == null) {
                    _mailSender = LocalConfig.mailSenderService.getMailSender()!!
                }
                return _mailSender!!
            }
            set(value) {
                _mailSender = value
            }

    }
}