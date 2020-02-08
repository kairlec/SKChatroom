package cn.skstudio.config.static

import java.util.ArrayList

class StaticConfig {
    companion object {
        const val captchaCount = 4
        const val maxUsernameLength = 20
        const val maxPasswordLength = 16
        const val minPasswordLength = 6
        const val maxNickNameLength = 30
        const val maxSignatureLength = 32
        val allowSexString: ArrayList<String?> = object : ArrayList<String?>() {
            init {
                add("男")
                add("女")
                add("未知")
            }
        }
        const val snowFlakeWorkerId = 16L
        const val snowFlakeDataCenterId = 16L

        const val signIDToAllUser = -1L

        const val defaultFriendGroupID=-1L
    }
}