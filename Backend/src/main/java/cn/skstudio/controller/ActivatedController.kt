package cn.skstudio.controller

/**
 * @author: Kairlec
 * @version: 2.2
 * @description: 注册和激活接口
 */
import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.annotation.RequestLimit
import cn.skstudio.config.system.DatabaseConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.intf.ResponseDataInterface
import cn.skstudio.local.utils.ResponseDataUtils.responseOK
import cn.skstudio.pojo.User
import cn.skstudio.service.impl.MailSenderServiceImpl
import cn.skstudio.service.impl.SendEmailService
import cn.skstudio.service.impl.UserServiceImpl
import cn.skstudio.utils.PasswordCoder
import cn.skstudio.utils.RSACoder
import cn.skstudio.utils.json2Object
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*
import javax.servlet.http.HttpServletResponse

@JsonRequestMapping(value = ["/api/public/register"])
@RestController
class ActivatedController {
    companion object {
        val logger: Logger = LogManager.getLogger(ActivatedController::class.java)
    }

    @Autowired
    private lateinit var userService: UserServiceImpl

    @Autowired
    private lateinit var mailSenderService: MailSenderServiceImpl

    @Autowired
    private lateinit var sendEmailService: SendEmailService

    /**
     * @description: 激活接口
     * @return: 激活状态
     */
    @RequestLimit(60, 3)
    @RequestMapping(value = ["/activate"])
    fun activated(@RequestParam(name = "activateCode") activateCode: String, response: HttpServletResponse): ResponseDataInterface {
        if (activateCode.isEmpty()) {
            response.status = 403
            logger.info("激活码为空")
            ServiceErrorEnum.ACTIVATE_TOKEN_INVALID.throwout()//无效激活码
        }
        logger.info(activateCode)
        val activatedInfo: SendEmailService.ActivatedInfo?
        try {
            //将激活码用私钥解密并反序列化
            activatedInfo = RSACoder.decryptByPrivateKeyToString(activateCode, DatabaseConfig.PrivateKey).json2Object<SendEmailService.ActivatedInfo>()
                    ?: ServiceErrorEnum.ACTIVATE_TOKEN_INVALID.throwout()
        } catch (e: Exception) {
            ServiceErrorEnum.ACTIVATE_TOKEN_INVALID.throwout(e)
        }
        val nowTime = Date().time
        if (nowTime >= activatedInfo.expiredTime) {
            //验证码已超过有效时间
            ServiceErrorEnum.ACTIVATE_TOKEN_EXPIRE.throwout()
        }
        logger.info(activatedInfo)
        val existsUser = userService.getUserByUsername(activatedInfo.username)
        if (existsUser != null) {
            //在注册的时候验证过用户名不存在,这里已经存在表示该链接已经成功激活过了,所以应该写为注册码过期
            ServiceErrorEnum.ACTIVATE_TOKEN_EXPIRE.throwout()
        }
        val user = User()
        user.userID = User.getNewID()
        val updateUser = user.readyToUpdate()
        updateUser.email = activatedInfo.email
        updateUser.username = activatedInfo.username
        updateUser.setPassword(activatedInfo.password, true)
        updateUser.nickname = "默认用户"
        user.applyUpdate(updateUser)
        userService.insertUser(user)
                ?: ServiceErrorEnum.ACTIVATE_UNKNOWN_EXCEPTION.throwout()
        return null.responseOK
    }

    @RequestLimit(60, 3)
    @RequestMapping(value = [""])
    fun register(@RequestParam(name = "username") username: String,
                 @RequestParam(name = "password") password: String,
                 @RequestParam(name = "email") email: String,
                 @RequestParam(name = "domain") domain: String): ResponseDataInterface {
        //前端需要提交一个domain域名,以指示激活成功的页面是什么,密钥将以QueryString拼接到域名后
        val user = User()
        val updateUser = user.readyToUpdate()
        updateUser.username = username
        //因为还未生成ID,所以不能进行加密
        updateUser.setPassword(PasswordCoder.fromRequest(URLDecoder.decode(password, StandardCharsets.UTF_8)).trim(), false)
        updateUser.email = email
        user.applyUpdate(updateUser)
        userService.getUserByUsername(username)?.let {
            ServiceErrorEnum.USERNAME_EXIST.throwout()
        }
        userService.getUserByEmail(email)?.let {
            ServiceErrorEnum.EMAIL_USED.throwout()
        }
        return if (mailSenderService.mailSenderInstant.enable) {
            //启用了激活邮件发送
            logger.info("尝试发送激活邮件")
            val thread = Thread(Runnable {
                try {
                    sendEmailService.sendActivatedEmail(domain, user)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            thread.start()
            logger.info("邮箱发送线程已启动")
            //返回数据为需要验证
            "VERIFICATION_REQUIRED".responseOK
        } else {
            user.userID = User.getNewID()
            val finalUpdateUser = user.readyToUpdate()
            finalUpdateUser.setPassword(user.password, true)//生成了新的ID,需要对密码进行一次加密
            finalUpdateUser.nickname = "默认用户"
            user.applyUpdate(finalUpdateUser)
            userService.insertUser(user) ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
            //返回数据为无需验证
            "NO_VERIFICATION_REQUIRED".responseOK
        }
    }
}