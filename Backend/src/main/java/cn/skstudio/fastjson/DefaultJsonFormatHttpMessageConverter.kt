package cn.skstudio.fastjson

import cn.skstudio.`interface`.ResponseDataInterface
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter
import org.springframework.http.HttpOutputMessage
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

/**
 *@program: SKExplorer
 *@description: 在springboot中使用fastjson作为默认的json序列化器
 *@author: Kairlec
 *@create: 2020-03-11 16:55
 */

@Component
class DefaultJsonFormatHttpMessageConverter : FastJsonHttpMessageConverter() {
    override fun writeInternal(data: Any, outputMessage: HttpOutputMessage) {
        if (data is ResponseDataInterface) {
            outputMessage.body.use {
                it.write(data.toString().toByteArray(StandardCharsets.UTF_8))
            }
            return
        }
        super.writeInternal(data, outputMessage)
    }
}