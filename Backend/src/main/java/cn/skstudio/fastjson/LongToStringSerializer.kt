package cn.skstudio.fastjson

import com.alibaba.fastjson.serializer.JSONSerializer
import com.alibaba.fastjson.serializer.ObjectSerializer
import java.lang.reflect.Type
/**
 * @author: Kairlec
 * @version: 1.0
 * @description: 配置在FastJson中Long序列化为String的序列化器
 */

class LongToStringSerializer : ObjectSerializer {
    override fun write(serializer: JSONSerializer, data: Any?, fieldName: Any?, fieldType: Type?,
                       features: Int) {
        val out = serializer.out
        if (data == null) {
            out.writeNull()
            return
        }
        val strVal = data.toString()
        out.writeString(strVal)
    }
    companion object {
        val instance = LongToStringSerializer()
    }
}