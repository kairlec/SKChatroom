package cn.skstudio.service.impl

import cn.skstudio.config.database.EditableConfig
import cn.skstudio.dao.MailSenderMapper
import cn.skstudio.pojo.MailSender
import cn.skstudio.service.MailSenderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class MailSenderServiceImpl : MailSenderService {
    @Autowired
    private lateinit var mailSenderMapper: MailSenderMapper

    override fun initialize(): Int? {
        return try {
            mailSenderMapper.initializeTable()
            mailSenderMapper.initializeSender(MailSender())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getMailSender(): MailSender? {
        return try {
            mailSenderMapper.getMailSender()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun updateMailSender(mailSender: MailSender): Int? {
        return try {
            val result: Int? = mailSenderMapper.updateMailSender(mailSender)
            if (result != null) {
                EditableConfig.mailSender = mailSender
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
