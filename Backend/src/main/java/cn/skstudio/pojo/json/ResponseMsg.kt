package cn.skstudio.pojo.json

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.annotation.JSONField
import com.alibaba.fastjson.serializer.SerializerFeature

class ResponseMsg(@JSONField(name = "type") val type: Int?, @JSONField(name = "message") val message: String? = null, @JSONField(name = "data") val `object`: Any? = null) {

    override fun toString(): String {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue)
    }

    companion object {
        fun successConnected(): ResponseMsg {
            return ResponseMsg(2001, "成功连接到聊天室", null)
        }

        fun updateNickname(`object`: Any?): ResponseMsg {
            return ResponseMsg(2001, "更新用户昵称", `object`)
        }
    }
}