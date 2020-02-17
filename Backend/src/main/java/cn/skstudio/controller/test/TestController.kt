package cn.skstudio.controller.test

import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.pojo.User
import org.springframework.util.DigestUtils
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/test")
@RestController
class TestController {
    @RequestMapping(value = ["/newUserID"])
    fun newUserID(): String {
        return ResponseDataUtils.successData(User.getNewID())
    }

    @RequestMapping(value = ["/getPassword/{id}/{pwd}"])
    fun getPassword(@PathVariable("id") id: String, @PathVariable("pwd") pwd: String): String {
        return ResponseDataUtils.successData(DigestUtils.md5DigestAsHex((pwd + id).toByteArray()))
    }


}