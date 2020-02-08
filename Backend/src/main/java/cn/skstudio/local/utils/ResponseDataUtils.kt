package cn.skstudio.local.utils

import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.ResponseData
import java.io.ByteArrayOutputStream
import java.io.PrintWriter


abstract class ResponseDataUtils private constructor() {
    companion object {
        fun fromException(e: Exception): ServiceErrorEnum {
            return ServiceErrorEnum.fromException(e)
        }

        fun OK(): String {
            return ServiceErrorEnum.NO_ERROR.toString()
        }

        fun Error(e: Exception): String {
            return fromException(e).toString()
        }

        fun Error(ServiceErrorEnum: ServiceErrorEnum): String {
            return ServiceErrorEnum.toString()
        }

        fun successData(`object`: Any?): String {
            return ResponseData(0, "OK", `object`).toString()
        }
    }

    init {
        throw AssertionError("工具类不允许实例化")
    }
}