package cn.skstudio.config.system

/**
 * @author: Kairlec
 * @version: 1.0
 * @description: 在程序启动的时候固定配置
 */


object StaticConfig {
    //验证码个数
    const val captchaCount = 4

    //用户名最长字符个数
    const val maxUsernameLength = 20

    //密码最长字符个数
    const val maxPasswordLength = 16

    //密码最短字符个数
    const val minPasswordLength = 6

    //最大昵称字符个数
    const val maxNickNameLength = 10

    //最长签名字符个数
    const val maxSignatureLength = 32

    //最长组名
    const val maxGroupName = 8

    //头像最大大小
    const val maxAvatarSize = 2048 * 1024

    //允许的性别值
    enum class AllowSexString(private val ch: String) {
        MAIL("男"),
        FEMALE("女"),
        UNKNOWN("未知");

        companion object {
            fun allow(string: String): Boolean {
                for (sex in values()) {
                    if (sex.ch == string) {
                        return true
                    }
                }
                return false
            }
        }
    }

    //雪花算法工作值和数据中心值
    const val snowFlakeWorkerId = 16L
    const val snowFlakeDataCenterId = 16L

    //全体成员ID标志
    const val signIDToAllUser = -1L

}