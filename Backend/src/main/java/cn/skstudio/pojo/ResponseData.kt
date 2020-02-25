package cn.skstudio.pojo

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import com.alibaba.fastjson.serializer.SerializerFeature

import cn.skstudio.`interface`.ResponseDataInterface
import cn.skstudio.controller.public.activated.ActivatedController.Companion.logger
import org.apache.logging.log4j.LogManager


open class ResponseData(@JSONField(name = "code", ordinal = 0)
                        final override val code: Int, @JSONField(name = "msg", ordinal = 1)
                        final override val msg: String, @JSONField(name = "data", ordinal = 2)
                        final override val data: Any?
) : ResponseDataInterface {

    override fun toString(): String {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue)
    }


}