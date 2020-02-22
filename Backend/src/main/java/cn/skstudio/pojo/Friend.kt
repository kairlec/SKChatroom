package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg
import cn.skstudio.fastjson.LongToStringSerializer
import com.alibaba.fastjson.annotation.JSONField

@NoArg
data class Friend(
        @JSONField(serializeUsing = LongToStringSerializer::class)
        var userID: Long,
        @JSONField(serializeUsing = LongToStringSerializer::class)
        var userGroupID: Long
) {
}