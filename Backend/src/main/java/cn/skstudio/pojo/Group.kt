package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg
import cn.skstudio.config.static.StaticConfig
import cn.skstudio.utils.SnowFlake

@NoArg
data class Group(
        var groupID: Long,
        var groupOrder: Int?,
        var userID: Long,
        var groupName: String?
) {

    fun ownerVerify(userID: Long) = userID == this.userID


    companion object {
        private val snowFlake = SnowFlake(StaticConfig.snowFlakeWorkerId, StaticConfig.snowFlakeDataCenterId)
        fun getNewGroupID() = snowFlake.nextId()


        fun newDefaultGroup(userID: Long) = Group(-userID, -1, userID, "默认分组")


    }
}