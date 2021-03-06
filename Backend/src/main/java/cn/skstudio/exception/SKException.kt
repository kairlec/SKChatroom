package cn.skstudio.exception

class SKException : RuntimeException {
    private val serviceErrorEnum: ServiceErrorEnum?

    constructor() : super() {
        this.serviceErrorEnum = null
    }

    constructor(serviceErrorEnum: ServiceErrorEnum, cause: Throwable? = null) : super(serviceErrorEnum.msg, cause) {
        this.serviceErrorEnum = serviceErrorEnum
    }

    constructor(detailedMessage: String) : super(detailedMessage) {
        this.serviceErrorEnum = null
    }

    constructor(t: Throwable) : super(t) {
        this.serviceErrorEnum = null
    }

    constructor(detailedMessage: String, t: Throwable) : super(detailedMessage, t) {
        this.serviceErrorEnum = null
    }

    fun getServiceErrorEnum(): ServiceErrorEnum? {
        return serviceErrorEnum
    }
}