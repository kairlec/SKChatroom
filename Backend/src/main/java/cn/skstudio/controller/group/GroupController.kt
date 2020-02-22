package cn.skstudio.controller.group;

import cn.skstudio.local.utils.ResponseDataUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * @program: Backend
 * @description: 组操作的API接口
 * @author: Kairlec
 * @create: 2020-02-20 19:49
 */

@RestController
@RequestMapping("/api/group")
class GroupController {
    @RequestMapping(value = ["/login"], produces = ["application/json; charset=utf-8"])
    fun login(request:HttpServletRequest): String {

        return ResponseDataUtils.OK()
    }
}
