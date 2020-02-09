package cn.skstudio.controller.public.activated

import cn.skstudio.config.system.StartupConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.ResponseDataUtils
import com.alibaba.fastjson.JSON
import cn.skstudio.pojo.User
import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.utils.PasswordCoder
import cn.skstudio.utils.RSACoder
import cn.skstudio.utils.SendEmail
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

    @RequestMapping(value = ["/activate"])
    fun activated(request: HttpServletRequest, response: HttpServletResponse): String {
        val activateCode = request.getParameter("activateCode")
        if (activateCode == null) {
            response.status = 403
            logger.info("无激活码")
            return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_INVALID)
        } else if (activateCode.isEmpty()) {
            response.status = 403
            logger.info("激活码为空")
            return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_INVALID)
        }
        logger.info(activateCode)
        val activatedInfo: SendEmail.ActivatedInfo?
        try {
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
            return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_EXPIRE)
        }
        logger.info(activatedInfo)
        val existsUser = LocalConfig.userService.getUserByUsername(activatedInfo.uN)
        if (existsUser != null) {
            return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_TOKEN_EXPIRE)
        }
        val user = User()
        user.email = activatedInfo.eM
        user.username = activatedInfo.uN
        user.password = activatedInfo.pW
        user.nickname = activatedInfo.uN
        user.userID = User.getNewID()
        user.privateEmail = true
        user.privatePhone = true
        user.privateSex = false
        LocalConfig.userService.insertUser(user)
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.ACTIVATE_UNKNOWN_EXCEPTION)
        return ResponseDataUtils.OK()
    }

    @RequestMapping(value = [""])
    fun register(request: HttpServletRequest): String {
        val username = request.getParameter("username")
        var password = request.getParameter("password")
        val email = request.getParameter("email")
        val domain = request.getParameter("domain")
        if (username == null || password == null || email == null || domain == null) {
            return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        }
        val user = User()
        var error = user.setUsername(username)
        if (!error.ok()) {
            return ResponseDataUtils.Error(error)
        }
        password = PasswordCoder.fromRequest(URLDecoder.decode(password, StandardCharsets.UTF_8)).trim()
        logger.info("由Request解密的密码:$password")
        error = user.updatePassword(password)
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
        return ResponseDataUtils.OK()
    }
}