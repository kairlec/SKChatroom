package cn.skstudio.local.utils

import cn.skstudio.exception.SKException
import cn.skstudio.exception.ServiceErrorEnum
import org.apache.logging.log4j.LogManager


object ResponseDataUtils {
    private val logger = LogManager.getLogger(ResponseDataUtils::class.java)

    val Any?.responseOK
        get() = ServiceErrorEnum.NO_ERROR.data(this)

    val Exception.responseError: ServiceErrorEnum
        get() {
            this.printStackTrace()
            if (this is SKException) {
                logger.error("a SKExplorer has throwout:${this.message}")
                this.getServiceErrorEnum()?.let {
                    logger.error("${it.msg} with data ${it.data}")
                    return it
                }
            }
            logger.error("a Exception has throwout:${this.message}")
            return ServiceErrorEnum.fromException(this)
        }

}