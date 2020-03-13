package cn.skstudio.controller.public.activated

/**
 * @author: Kairlec
 * @version: 2.2
 * @description: 注册和激活接口
 */
import cn.skstudio.`interface`.ResponseDataInterface
import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.annotation.RequestLimit
import cn.skstudio.config.database.EditableConfig
import cn.skstudio.config.system.StartupConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.pojo.User
import cn.skstudio.utils.PasswordCoder
import cn.skstudio.utils.RSACoder
import cn.skstudio.utils.SendEmail
import com.alibaba.fastjson.JSON
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@JsonRequestMapping("/api/public/register")
@RestController
class ActivatedController {
    companion object {
        val logger: Logger = LogManager.getLogger(ActivatedController::class.java)
    }

    /**
     * @description: 激活接口
     * @return: 激活状态
     */
    @RequestLimit(60, 3)
    @RequestMapping(value = ["/activate"])
    fun activated(request: HttpServletRequest, response: HttpServletResponse): ResponseDataInterface {
        val activateCode = request.getParameter("activateCode")
        if (activateCode == null) {
            response.status = 403
            logger.info("无激活码")
            ServiceErrorEnum.ACTIVATE_TOKEN_INVALID.throwout()//无效激活码
        } else if (activateCode.isEmpty()) {
            response.status = 403
            logger.info("激活码为空")
            ServiceErrorEnum.ACTIVATE_TOKEN_INVALID.throwout()//无效激活码
        }
        logger.info(activateCode)
        val activatedInfo: SendEmail.ActivatedInfo?
        try {
            //将激活码用私钥解密并反序列化
            activatedInfo = JSON.parseObject(RSACoder.decryptByPrivateKeyToString(activateCode, StartupConfig.privateKey), SendEmail.ActivatedInfo::class.java)
            if (activatedInfo == null) {
                ServiceErrorEnum.ACTIVATE_TOKEN_INVALID.throwout()
            }
        } catch (e: Exception) {
            ServiceErrorEnum.ACTIVATE_TOKEN_INVALID.throwout()
        }
        val nowTime = Date().time
        if (nowTime >= activatedInfo.eT) {
            //验证码已超过有效时间
            ServiceErrorEnum.ACTIVATE_TOKEN_EXPIRE.throwout()
        }
        logger.info(activatedInfo)
        val existsUser = LocalConfig.userService.getUserByUsername(activatedInfo.uN)
        if (existsUser != null) {
            //在注册的时候验证过用户名不存在,这里已经存在表示该链接已经成功激活过了,所以应该写为注册码过期
            ServiceErrorEnum.ACTIVATE_TOKEN_EXPIRE.throwout()
        }
        val user = User()
        user.userID = User.getNewID()
        val updateUser = user.readyToUpdate()
        updateUser[User.UpdateUser.EMAIL_FIELD] = activatedInfo.eM
        updateUser[User.UpdateUser.USERNAME_FIELD] = activatedInfo.uN
        updateUser[User.UpdateUser.PASSWORD_FIELD] = activatedInfo.pW
        updateUser[User.UpdateUser.NICKNAME_FIELD] = "默认用户"
        user.applyUpdate(updateUser)
        LocalConfig.userService.insertUser(user)
                ?: ServiceErrorEnum.ACTIVATE_UNKNOWN_EXCEPTION.throwout()
        return ResponseDataUtils.ok()
    }

    @RequestLimit(60, 3)
    @RequestMapping(value = [""])
    fun register(request: HttpServletRequest): ResponseDataInterface {
        val username = request.getParameter("username")
        val password = request.getParameter("password")
        val email = request.getParameter("email")
        val domain = request.getParameter("domain")//前端需要提交一个domain域名,以指示激活成功的页面是什么,密钥将以QueryString拼接到域名后
        if (username == null || password == null || email == null || domain == null) {
            ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        }
        logger.info(domain)
        val user = User()
        val updateUser = user.readyToUpdate()
        updateUser[User.UpdateUser.USERNAME_FIELD] = username
        //因为还未生成ID,所以不能进行加密
        updateUser[User.UpdateUser.PASSWORD_FIELD, false] = PasswordCoder.fromRequest(URLDecoder.decode(password, StandardCharsets.UTF_8)).trim()
        updateUser[User.UpdateUser.EMAIL_FIELD] = email
        user.applyUpdate(updateUser)
        LocalConfig.userService.getUserByUsername(username)?.let {
            ServiceErrorEnum.USERNAME_EXIST.throwout()
        }
        LocalConfig.userService.getUserByEmail(email)?.let {
            ServiceErrorEnum.EMAIL_USED.throwout()
        }
        return if (EditableConfig.mailSender.enable) {
            //启用了激活邮件发送
            logger.info("尝试发送激活邮件")
            val thread = Thread(Runnable {
                try {
                    SendEmail.sendActivatedEmail(domain, user)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            thread.start()
            logger.info("邮箱发送线程已启动")
            //返回数据为需要验证
            ResponseDataUtils.ok("VERIFICATION_REQUIRED")
        } else {
            user.userID = User.getNewID()
            val finalUpdateUser = user.readyToUpdate()
            finalUpdateUser[User.UpdateUser.PASSWORD_FIELD] = user.password//生成了新的ID,需要对密码进行一次加密
            finalUpdateUser[User.UpdateUser.NICKNAME_FIELD] = "默认用户"
            user.applyUpdate(finalUpdateUser)
            LocalConfig.userService.insertUser(user) ?: ServiceErrorEnum.IO_EXCEPTION.throwout()
            //返回数据为无需验证
            ResponseDataUtils.ok("NO_VERIFICATION_REQUIRED")
        }
    }
}