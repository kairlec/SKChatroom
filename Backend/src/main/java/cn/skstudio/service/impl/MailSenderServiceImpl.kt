package cn.skstudio.service.impl

import cn.skstudio.dao.MailSenderMapper
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.MailSender
import cn.skstudio.service.MailSenderService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import java.lang.Exception
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

@Service
class MailSenderServiceImpl : MailSenderService {
    companion object {
        private val logger = LogManager.getLogger(MailSenderServiceImpl::class.java)
    }

    @Autowired
    private lateinit var mailSenderMapper: MailSenderMapper

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    lateinit var mailSenderInstant: MailSender
        private set

    @PostConstruct
    fun init() {
        if (initialize() == null) {
            logger.fatal("Init database table [MailSender] failed")
            exitProcess(SpringApplication.exit(applicationContext))
        }else {
            mailSenderInstant = getMailSender() ?: run {
                ServiceErrorEnum.IO_EXCEPTION.throwout()
            }
            logger.info("Init database table [MailSender] success")
        }
    }

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
            val result = mailSenderMapper.updateMailSender(mailSender)
            if (result != null) {
                mailSenderInstant = mailSender
            }
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
