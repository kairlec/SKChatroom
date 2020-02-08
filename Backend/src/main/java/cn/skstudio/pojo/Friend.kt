package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg

@NoArg
data class Friend(
        var userID: Long,
        var userGroupID: Long
) {
}