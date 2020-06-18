package cn.skstudio.controller

import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.intf.ResponseDataInterface
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 *@program: Backend
 *@description: 重写Springboot对错误请求的处理
 *@author: Kairlec
 *@create: 2020-02-28 16:53
 */

@Controller
class NotFoundExceptionHandler : ErrorController {
    override fun getErrorPath(): String {
        return "/error"
    }

    @RequestMapping(value = ["/error"])
    @ResponseBody
    fun error(): ResponseDataInterface {
        return ServiceErrorEnum.UNKNOWN_REQUEST
    }
}