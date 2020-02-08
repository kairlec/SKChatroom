package cn.skstudio.pojo

import cn.skstudio.annotation.NoArg

@NoArg
data class MailSender(
        var port: Int,
        var host: String,
        var protocol: String,
        var encoding: String,
        var username: String,
        var password: String
){
    constructor():this(25,"","smtp","UTF-8","","")
}