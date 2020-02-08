package cn.skstudio.pojo

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import com.alibaba.fastjson.serializer.SerializerFeature

import cn.skstudio.`interface`.ResponseDataInterface


open class ResponseData : ResponseDataInterface {
    @JSONField(name = "code", ordinal = 0)
    final override val code: Int
    @JSONField(name = "msg", ordinal = 1)
    final override val message: String
    @JSONField(name = "data", ordinal = 2)
    final override val data: Any?

    constructor(code: Int, message: String, data: Any?) {
        this.code = code
        this.message = message
        this.data = data
    }

    override fun toString(): String {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue)
    }


}