package cn.skstudio.config.static

/**
 * @author: Kairlec
 * @version: 1.0
 * @description: 在程序启动的时候固定配置
 */

import java.util.*

class StaticConfig {
    companion object {
        //验证码个数
        const val captchaCount = 4
        //用户名最长字符个数
        const val maxUsernameLength = 20
        //密码最长字符个数
        const val maxPasswordLength = 16
        //密码最短字符个数
        const val minPasswordLength = 6
        //最大昵称字符个数
        const val maxNickNameLength = 30
        //最长签名字符个数
        const val maxSignatureLength = 32
        //允许的性别值
        val allowSexString: ArrayList<String?> = object : ArrayList<String?>() {
            init {
                add("男")
                add("女")
                add("未知")
            }
        }
        //雪花算法工作值和数据中心值
        const val snowFlakeWorkerId = 16L
        const val snowFlakeDataCenterId = 16L

        //全体成员ID标志
        const val signIDToAllUser = -1L

        //默认分组的ID
        const val defaultFriendGroupID=-1L
    }
}