package cn.skstudio.service

import cn.skstudio.pojo.MailSender
import org.springframework.stereotype.Service

@Service
interface MailSenderService {
    fun initialize(): Int?

    fun getMailSender(): MailSender?

    fun updateMailSender(mailSender: MailSender): Int?
}