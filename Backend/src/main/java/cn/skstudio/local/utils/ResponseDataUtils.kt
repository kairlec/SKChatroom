package cn.skstudio.local.utils

import cn.skstudio.controller.public.user.PublicUserController
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.ResponseData
import cn.skstudio.pojo.SKImage
import java.io.IOException
import javax.servlet.http.HttpServletResponse


abstract class ResponseDataUtils private constructor() {
    companion object {
        fun fromException(e: Exception): ServiceErrorEnum {
            return ServiceErrorEnum.fromException(e)
        }

        fun OK(dataObject: Any? = null): String {
            return ServiceErrorEnum.NO_ERROR.data(dataObject).toString()
        }

        fun Error(e: Exception): String {
            return fromException(e).toString()
        }

        fun Error(ServiceErrorEnum: ServiceErrorEnum): String {
            return ServiceErrorEnum.toString()
        }

        fun successData(dataObject: Any?): String {
            return ResponseData(0, "OK", dataObject).toString()
        }

        fun writeResponseImage(response: HttpServletResponse, image: SKImage) {
            response.setDateHeader("Expires", 0)
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate")
            response.addHeader("Cache-Control", "post-check=0, pre-check=0")
            response.setHeader("Pragma", "no-cache")
            response.contentType = image.contentType
            try {
                response.outputStream.use { outputStream -> image.write(outputStream) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    init {
        throw AssertionError("工具类不允许实例化")
    }
}