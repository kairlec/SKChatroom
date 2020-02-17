package cn.skstudio.config.database

/**
 * @author: Kairlec
 * @version: 1.0
 * @description: 在程序中可更改动态加载的配置项目
 */

import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.pojo.MailSender
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Component

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