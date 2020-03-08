package cn.skstudio.utils

import cn.skstudio.config.database.EditableConfig
import cn.skstudio.config.system.StartupConfig
import cn.skstudio.pojo.User
import com.alibaba.fastjson.JSON
import org.apache.logging.log4j.LogManager
import org.springframework.mail.javamail.MimeMessageHelper
import java.io.UnsupportedEncodingException
import java.util.*
import java.util.regex.Pattern
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeUtility

object SendEmail {
    private val logger = LogManager.getLogger(SendEmail::class.java)
    private fun emails(): MimeMessage {
        val properties = Properties()
        properties.setProperty("mail.host", EditableConfig.mailSender.host)
        properties.setProperty("mail.transport.protocol", EditableConfig.mailSender.protocol)
        properties.setProperty("mail.smtp.auth", "true")
        properties.setProperty("mail.default-encoding", EditableConfig.mailSender.encoding)
        properties.setProperty("mail.smtp.port", EditableConfig.mailSender.port.toString())
        properties.setProperty("mail.smtp.socketFactory.port", EditableConfig.mailSender.port.toString())
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        val session = Session.getInstance(properties, object : Authenticator() {
            // 设置认证账户信息
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(EditableConfig.mailSender.username, EditableConfig.mailSender.password)
            }
        })
        session.debug = true
        val message = MimeMessage(session)
        logger.info("邮件初始化完成")
        return message
    }

    private const val HtmlFormatBefore = "<!DOCTYPE html><html><head> <style type=\"text/css\">*{margin:0;padding: 0;box-sizing: border-box } a { color: inherit; text-decoration: none; background-color: transparent } li { list-style: none } .clearfix:after { content: ''; display: table; clear: both } body { font-size: 14px; color: #494949; overflow: auto } .wrap { position: relative; min-height: 580px } .wrap-bg circle, .wrap-bg rect { stroke-width: 0; -ms-transform: rotate(30deg) scale(1.1); transform: rotate(30deg) scale(1.1); -ms-transform-origin: center; transform-origin: center } .main { position: absolute; top: 50%; left: 50%; z-index: 2; width: 970px; background: #effbff; -ms-transform: translate(-50%, -50%); transform: translate(-50%, -50%); box-shadow: 0 0 50px rgba(0, 0, 0, .1) } .header-tabs { width: 100%; height: 55px; padding: 0 30px; background: linear-gradient(45deg, #4c75e9, #2c7ce3); overflow: hidden } .header-tabs .tab { float: left; height: 40px; width: 200px; padding: 0 18px; margin-top: 15px; border-radius: 10px 10px 0 0; background: #fbfdff; line-height: 40px; color: #113c73 } .header-tabs .tab:after { content: ''; display: table; clear: both } .header-tabs .tab span { float: left } .header-tabs .tab i { position: relative; float: right; width: 11px; height: 11px; margin-top: 14px; cursor: pointer; -ms-transform: rotate(45deg); transform: rotate(45deg) } .header-tabs .tab i:before { content: ''; position: absolute; top: 5px; left: 0; display: block; width: 100%; height: 1px; background: #113c73 } .header-tabs .tab i:after { content: ''; position: absolute; left: 5px; top: 0; display: block; width: 1px; height: 100%; background: #113c73 } .header-tabs .tab-add { position: relative; float: left; width: 28px; height: 28px; margin: 20px 0 0 15px; border-radius: 50%; background: #a9cdf7 } .header-tabs .tab-add:before { content: ''; position: absolute; top: 13px; left: 9px; display: block; width: 10px; height: 2px; background: #113c73 } .header-tabs .tab-add:after { content: ''; position: absolute; left: 13px; top: 9px; display: block; width: 2px; height: 10px; background: #113c73 } .header-tabs .tabs-tool { float: right; height: 100% } .header-tabs .tabs-tool a { position: relative; float: left; width: 14px; height: 100%; margin: 0 15px } .header-tabs .tabs-tool .btn-min:before { content: ''; position: absolute; top: 27px; left: 0; width: 100%; height: 1px; background: #113c73 } .header-tabs .tabs-tool .btn-max:before { content: ''; position: absolute; top: 20px; left: 0; width: 14px; height: 14px; border: 1px solid #113c73; box-sizing: border-box } .header-tabs .tabs-tool .btn-close { width: 15px; -ms-transform: rotate(45deg); transform: rotate(45deg) } .header-tabs .tabs-tool .btn-close:before { content: ''; position: absolute; top: 27px; left: 0; display: block; width: 15px; height: 1px; background: #113c73 } .header-tabs .tabs-tool .btn-close:after { content: ''; position: absolute; left: 7px; top: 20px; display: block; width: 1px; height: 15px; background: #113c73 } .header-url { width: 100%; height: 54px; padding: 10px 20px 10px 15px; background: #f2f2f2 } .header-url a { float: left; height: 100%; margin: 0 9px; overflow: hidden } .header-url a svg { margin-top: 9px } .header-url a .arrow { fill: none; stroke: #494949; stroke-width: 2px; stroke-linecap: round; stroke-linejoin: round } .header-url .btn-next .arrow { -ms-transform-origin: center; transform-origin: center; -ms-transform: rotate(180deg); transform: rotate(180deg); stroke: #d4d5d6 } .header-url .btn-refresh path { fill: none; stroke: #494949; stroke-width: 2px; stroke-linecap: round; stroke-linejoin: round; -ms-transform-origin: center; transform-origin: center; -ms-transform: rotate(40deg); transform: rotate(40deg) } .header-url .btn-refresh polyline { fill: #494949; -ms-transform-origin: center; transform-origin: center; -ms-transform: rotate(40deg); transform: rotate(40deg) } .header-url input[type=text] { height: 100%; width: 820px; margin-left: 6px; padding: 0 1em; border: 1px solid #a9a9a9; border-radius: 4px; background: #fff } .main-content { height: 470px; border: 1px solid #c4dce5; background: #f4fcff } .main-content h5 { padding: 110px 0; font-weight: 400; text-align: center } .main-content h5 span { position: relative; display: inline-block; font-size: 60px; color: #2c7ce3 } .main-content h5 span:before { content: ''; position: absolute; top: 48px; left: -36px; display: block; width: 14px; height: 14px; border-radius: 50%; background: linear-gradient(45deg, #ded9ff, #2c7ce3); opacity: .2 } .main-content h5 span:after { content: ''; position: absolute; top: 32px; left: -80px; display: block; width: 20px; height: 20px; border-radius: 50%; background: linear-gradient(45deg, #ded9ff, #2c7ce3); opacity: .2 } .main-content h5 span i:before { content: ''; position: absolute; top: 32px; right: -72px; display: block; width: 14px; height: 14px; border-radius: 50%; background: linear-gradient(45deg, #ded9ff, #2c7ce3); opacity: .2 } .main-content h5 span i:after { content: ''; position: absolute; top: 48px; right: -40px; display: block; width: 20px; height: 20px; border-radius: 50%; background: linear-gradient(45deg, #ded9ff, #2c7ce3); opacity: .2 } .main-content p { font-size: 26px; text-align: center } </style></head><body> <div class=\"wrap\"> <div class=\"main\"> <header> <div class=\"header-tabs clearfix\"> <a href=\"javascript:void(0);\" class=\"tab\"><span>账号激活</span> <i></i> </a> <a href=\"javascript:void(0);\" class=\"tab-add\"></a> <div class=\"tabs-tool\"> <a href=\"javascript:void(0);\" class=\"btn-min\"></a> <a href=\"javascript:void(0);\" class=\"btn-max\"></a> <a href=\"javascript:void(0);\" class=\"btn-close\"></a> </div> </div> </header> <div class=\"main-content\"> <h5><span>您正在注册SKChatroom<i></i></span></h5> <p>点击 <a href='"
    private const val HtmlFormatAfter = "'><u>&nbsp;激活链接&nbsp;</u></a> 进行账号激活</p> </div> </div> </div></body></html>"
    fun checkEmail(string: String?): Boolean {
        if (string == null) return false
        val regEx1 = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
        val p = Pattern.compile(regEx1, Pattern.CASE_INSENSITIVE)
        val m = p.matcher(string)
        return m.matches()
    }

    private fun sendEmail(subject: String, nickname: String?, content: String, html: Boolean, vararg toEmail: String?) {
        val mimeMessage = emails()
        val mimeMessageHelper = MimeMessageHelper(mimeMessage, "UTF-8")
        mimeMessageHelper.setTo(toEmail)
        var nick = ""
        try {
            nick = MimeUtility.encodeText(nickname)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        mimeMessageHelper.setFrom(InternetAddress(nick + " <" + EditableConfig.mailSender.username + ">"))
        logger.info(nick + " <" + EditableConfig.mailSender.username + ">")
        mimeMessageHelper.setSubject(subject)
        mimeMessageHelper.setText(content, html)
        Transport.send(mimeMessage)
        logger.info("邮件已发送")
    }

    fun sendTextEmail(subject: String, nickname: String?, text: String, vararg toEmail: String?) {
        sendEmail(subject, nickname, text, false, *toEmail)
    }

    fun sendHtmlEmail(subject: String, nickname: String?, html: String, vararg toEmail: String?) {
        sendEmail(subject, nickname, html, true, *toEmail)
    }

    fun sendActivatedEmail(domain: String, user: User) {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.MINUTE, 30)
        calendar.timeInMillis
        val activatedInfo = ActivatedInfo(user.username, user.password, user.email, calendar.timeInMillis)
        logger.info(activatedInfo.toString())
        val url: String = domain + "?" + RSACoder.encryptByPublicKeyToString(activatedInfo.toString(), StartupConfig.publicKey)
        sendHtmlEmail("[SKChatroom]帐号注册激活", "SKChatroom", HtmlFormatBefore + url + HtmlFormatAfter, user.email)
    }

    class ActivatedInfo {
        constructor()
        constructor(uN: String, pW: String, eM: String, eT: Long) : this() {
            //username
            this.uN = uN
            //password
            this.pW = pW
            //email
            this.eM = eM
            //expiredTime
            this.eT = eT
        }

        lateinit var uN: String
        lateinit var pW: String
        lateinit var eM: String
        var eT: Long = 0
        override fun toString(): String {
            return JSON.toJSONString(this)
        }
    }
}