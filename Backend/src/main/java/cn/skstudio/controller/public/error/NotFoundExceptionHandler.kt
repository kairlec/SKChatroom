package cn.skstudio.controller.public.error

import cn.skstudio.`interface`.ResponseDataInterface
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.ResponseDataUtils
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

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
    fun error(request: HttpServletRequest): ResponseDataInterface {
        return ResponseDataUtils.error(ServiceErrorEnum.UNKNOWN_REQUEST.data(request.requestURI))
    }
}