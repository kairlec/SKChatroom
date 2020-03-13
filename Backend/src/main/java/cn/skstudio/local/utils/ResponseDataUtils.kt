package cn.skstudio.local.utils

import cn.skstudio.`interface`.ResponseDataInterface
import cn.skstudio.exception.SKException
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.SKImage
import java.io.IOException
import javax.servlet.http.HttpServletResponse


object ResponseDataUtils {
    fun fromException(e: Exception): ServiceErrorEnum {
        return ServiceErrorEnum.fromException(e)
    }

    fun ok(dataObject: Any? = null): ResponseDataInterface {
        return ServiceErrorEnum.NO_ERROR.data(dataObject)
    }

    fun error(e: Exception): ResponseDataInterface {
        if(e is SKException){
            e.getServiceError()?.let{
                return error(it)
            }
        }
        return fromException(e)
    }

    fun error(serviceErrorEnum: ServiceErrorEnum): ResponseDataInterface {
        return serviceErrorEnum
    }

    fun writeResponseImage(response: HttpServletResponse, image: SKImage) {
        response.contentType = image.contentType
        try {
            response.outputStream.use { outputStream -> image.write(outputStream) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}