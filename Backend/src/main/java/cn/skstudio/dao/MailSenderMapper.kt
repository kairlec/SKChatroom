package cn.skstudio.dao

import cn.skstudio.pojo.MailSender
import org.apache.ibatis.annotations.Mapper

@Mapper
interface MailSenderMapper {
    fun initializeTable(): Int?

    fun initializeSender(mailSender: MailSender): Int?

    fun getMailSender(): MailSender?

    fun updateMailSender(mailSender: MailSender): Int?
}