package cn.skstudio.controller

import cn.skstudio.intf.ResponseDataInterface
import cn.skstudio.local.utils.ResponseDataUtils.responseError
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

/**
 *@program: Backend
 *@description: 重写Springboot对未处理异常的处理
 *@author: Kairlec
 *@create: 2020-02-28 16:55
 */

@ControllerAdvice
class SKExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    fun exception(e: Exception, response: HttpServletResponse): ResponseDataInterface {
        val serviceError = e.responseError
        if (serviceError.code == 90003 || (serviceError.code in 50000..59999)) {
            response.status = 500
        }
        return serviceError
    }

    companion object {
        private val logger = LogManager.getLogger(SKExceptionHandler::class.java)
    }
}
