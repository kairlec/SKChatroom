package cn.skstudio.controller.public.error

import cn.skstudio.`interface`.ResponseDataInterface
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.local.utils.ResponseDataUtils.responseError
import org.apache.logging.log4j.LogManager
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

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
    fun exception(e: Exception): ResponseDataInterface {
        logger.error(e.message)
        return e.responseError
    }

    companion object {
        private val logger = LogManager.getLogger(SKExceptionHandler::class.java)
    }
}
