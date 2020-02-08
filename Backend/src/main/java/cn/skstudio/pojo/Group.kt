package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg
import cn.skstudio.config.static.StaticConfig
import cn.skstudio.utils.SnowFlake

@NoArg
data class Group(
        var groupID: Long,
        var groupOrder: Int?,
        var userID: Long,
        var groupName: String?,
        var friendList: List<GuestUser>?
) {
    constructor(groupID: Long,
                groupOrder: Int?,
                userID: Long,
                groupName: String?) : this(groupID, groupOrder, userID, groupName, null)

    companion object {
        private val snowFlake = SnowFlake(StaticConfig.snowFlakeWorkerId, StaticConfig.snowFlakeDataCenterId)
        fun getNewGroupID(): Long {
            return snowFlake.nextId()
        }

        fun newDefaultGroup(userID:Long):Group{
            return Group(defaultGroupID,-1,userID,"默认分组",ArrayList())
        }

        private val defaultGroupID
            get() = StaticConfig.defaultFriendGroupID

    }
}