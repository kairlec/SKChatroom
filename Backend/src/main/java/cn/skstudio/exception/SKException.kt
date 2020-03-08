package cn.skstudio.exception

import cn.skstudio.local.utils.ResponseDataUtils

class SKException : RuntimeException {
    private var serviceErrorEnum: ServiceErrorEnum? = null

    /**
     * 无参默认构造UNSPECIFIED
     */
    constructor() : super()

    /**
     * 由业务错误ServiceError引发
     */
    constructor(serviceErrorEnum: ServiceErrorEnum?) : super() {
        this.serviceErrorEnum = serviceErrorEnum
    }

    /**
     * 指定详细描述构造通用异常
     *
     * @param detailedMessage 详细描述
     */
    constructor(detailedMessage: String?) : super(detailedMessage)

    /**
     * 指定导火索构造通用异常
     *
     * @param t 导火索
     */
    constructor(t: Throwable?) : super(t) {}

    /**
     * 构造通用异常
     *
     * @param detailedMessage 详细描述
     * @param t               导火索
     */
    constructor(detailedMessage: String?, t: Throwable?) : super(detailedMessage, t)

    fun getServiceError(): ServiceErrorEnum? {
        return serviceErrorEnum
    }
}