package cn.skstudio.local.utils

import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.ResponseData


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
    }

    init {
        throw AssertionError("工具类不允许实例化")
    }
}