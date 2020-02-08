package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg
import cn.skstudio.config.static.StaticConfig
import cn.skstudio.utils.SnowFlake
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import java.sql.Timestamp
import java.time.LocalDateTime

@NoArg
data class ActionMessage(
        var messageID: Long,//消息ID
        var action: ActionTypeEnum,//消息类型
        var fromID: Long,//发送者ID
        var toID: Long,//接受者ID
        var time: Timestamp,//消息时间
        var topic: String?,//主题
        var context: String,//消息内容
        var isRead: Boolean//已读
) {
    override fun toString(): String {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue)
    }

    companion object {
        private val snowFlake = SnowFlake(StaticConfig.snowFlakeWorkerId, StaticConfig.snowFlakeDataCenterId)
        fun create(action: ActionTypeEnum,//消息类型
                   fromID: Long,//发送者ID
                   toID: Long,//接受者ID
                   topic: String?,//主题
                   context: String//消息内容
        ): ActionMessage {
            return ActionMessage(snowFlake.nextId(), action, fromID, toID, Timestamp.valueOf(LocalDateTime.now()), topic, context, false)
        }
    }
}