package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg
import cn.skstudio.config.static.StaticConfig
import cn.skstudio.fastjson.LongToStringSerializer
import cn.skstudio.utils.SnowFlake
import com.alibaba.fastjson.annotation.JSONField

@NoArg
data class Group(
        @JSONField(serializeUsing = LongToStringSerializer::class)
        var groupID: Long,
        var groupOrder: Int?,
        @JSONField(serializeUsing = LongToStringSerializer::class)
        var userID: Long,
        var groupName: String?
) {

    companion object {
        private val snowFlake = SnowFlake(StaticConfig.snowFlakeWorkerId, StaticConfig.snowFlakeDataCenterId)
        fun getNewGroupID(): Long {
            return snowFlake.nextId()
        }

        fun newDefaultGroup(userID:Long):Group{
            return Group(-userID,-1,userID,"默认分组")
        }

    }
}