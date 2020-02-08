package cn.skstudio.`interface`

interface ResponseDataInterface {
    /**
     * 获取错误码
     *
     * @return
     */
    val code: Int

    /**
     * 获取错误码
     *
     * @return
     */
    val message: String

    /**
     * 获取数据
     *
     * @return
     */
    val data: Any?


}