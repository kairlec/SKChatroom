package cn.skstudio.controller.public.activated

/**
 * @author: Kairlec
 * @version: 2.2
 * @description: 注册和激活接口
 */
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

@RequestMapping("/api/public/register")
@RestController
class ActivatedController {
    companion object {
        val logger: Logger = LogManager.getLogger(ActivatedController::class.java)
    }

    /**
     * @description: 激活接口
     * @return: 激活状态
     */
    @RequestLimit(60,3)
    @RequestMapping(value = ["/activate"])
    fun activated(request: HttpServletRequest, response: HttpServletResponse): String {
        val activateCode = request.getParameter("activateCode")
        if (activateCode == null) {
            response.status = 403
            logger.info("无激活码")
            return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_INVALID)//无效激活码
        } else if (activateCode.isEmpty()) {
            response.status = 403
            logger.info("激活码为空")
            return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_INVALID)//无效激活码
        }
        logger.info(activateCode)
        val activatedInfo: SendEmail.ActivatedInfo?
        try {
            //将激活码用私钥解密并反序列化
            activatedInfo = JSON.parseObject(RSACoder.decryptByPrivateKeyToString(activateCode, StartupConfig.privateKey), SendEmail.ActivatedInfo::class.java)
            if (activatedInfo == null) {
                return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_INVALID)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_INVALID)
        }
        val nowTime = Date().time
        if (nowTime >= activatedInfo.eT) {
            //验证码已超过有效时间
            return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_EXPIRE)
        }
        logger.info(activatedInfo)
        val existsUser = LocalConfig.userService.getUserByUsername(activatedInfo.uN)
        if (existsUser != null) {
            //在注册的时候验证过用户名不存在,这里已经存在表示该链接已经成功激活过了,所以应该写为注册码过期
            return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_EXPIRE)
        }
        val user = User()
        user.email = activatedInfo.eM
        user.username = activatedInfo.uN
        user.userID = User.getNewID()
        user.updatePassword(activatedInfo.pW)
        user.nickname = "默认用户"
        user.privateEmail = true
        user.privatePhone = true
        user.privateSex = false
        LocalConfig.userService.insertUser(user)
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_UNKNOWN_EXCEPTION)
        return ResponseDataUtils.OK()
    }

    @RequestLimit(60,3)
    @RequestMapping(value = [""])
    fun register(request: HttpServletRequest): String {
        val username = request.getParameter("username")
        var password = request.getParameter("password")
        val email = request.getParameter("email")
        val domain = request.getParameter("domain")//前端需要提交一个domain域名,以指示激活成功的页面是什么,密钥将以QueryString拼接到域名后
        if (username == null || password == null || email == null || domain == null) {
            return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        }
        logger.info(domain)
        val user = User()
        var error = user.setUsername(username)
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        //前端提交的所有密码都必须要经过一次RSA公钥加密
        password = PasswordCoder.fromRequest(URLDecoder.decode(password, StandardCharsets.UTF_8)).trim()
        logger.debug("由Request解密的密码:$password")
        error = user.updatePassword(password, false)//因为还未生成ID,所以不能进行加密
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        error = user.updateEmail(email)
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        var existUser: User? = LocalConfig.userService.getUserByUsername(username)
        if (existUser != null) {
            return ResponseDataUtils.Error(ServiceErrorEnum.USERNAME_EXIST)
        }
        existUser = LocalConfig.userService.getUserByEmail(email)
        if (existUser != null) {
            return ResponseDataUtils.Error(ServiceErrorEnum.EMAIL_USED)
        }
        return if (EditableConfig.mailSender.enable) {
            //启用了激活邮件发送
            logger.info("尝试发送激活邮件")
            val thread = Thread(Runnable {
                try {
                    SendEmail.sendActivitedEmail(domain, user)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            thread.start()
            logger.info("邮箱发送线程已启动")
            //返回数据为需要验证
            ResponseDataUtils.OK("VERIFICATION_REQUIRED")
        } else {
            user.userID = User.getNewID()
            user.updatePassword(user.password)//生成了新的ID,需要对密码进行一次加密
            user.nickname = "默认用户"
            LocalConfig.userService.insertUser(user) ?: return ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
            //返回数据为无需验证
            ResponseDataUtils.OK("NO_VERIFICATION_REQUIRED")
        }
    }
}