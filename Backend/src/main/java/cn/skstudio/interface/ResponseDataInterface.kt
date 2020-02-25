package cn.skstudio.`interface`

interface ResponseDataInterface {
    /**
     * 错误码
     * @return
     */
    val code: Int

    /**
     * 错误信息
     * @return
     */
    val msg: String

    /**
     * 附带数据
     * @return
     */
    val data: Any?


}