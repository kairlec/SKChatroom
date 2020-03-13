package cn.skstudio.controller.group;

import cn.skstudio.`interface`.ResponseDataInterface
import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.config.static.StaticConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.LocalConfig
import cn.skstudio.local.utils.ResponseDataUtils
import cn.skstudio.pojo.Group
import cn.skstudio.pojo.User
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

/**
 * @program: Backend
 * @description: 组操作的API接口
 * @author: Kairlec
 * @create: 2020-02-20 19:49
 */

@RestController
@JsonRequestMapping("/api/group")
class GroupController {


    @RequestMapping(value = ["/delete"])
    fun delete(request: HttpServletRequest, session: HttpSession): ResponseDataInterface {
        val user = session.getAttribute("user") as User
        val groupID = request.getParameter("id")?.toLongOrNull()
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val group = LocalConfig.friendGroupService.getGroup(groupID)
                ?: ServiceErrorEnum.GROUP_NOT_EXISTS.throwout()
        if (!group.ownerVerify(user.userID)) {
            ServiceErrorEnum.GROUP_NOT_ALLOW.throwout()
        }
        if (LocalConfig.friendGroupService.deleteGroup(groupID) == null) {
            ServiceErrorEnum.IO_EXCEPTION.throwout()
        } else {
            return ResponseDataUtils.ok(groupID)
        }
    }

    @RequestMapping(value = ["/create"])
    fun create(request: HttpServletRequest, session: HttpSession): ResponseDataInterface {
        val user = session.getAttribute("user") as User
        val groupName = request.getParameter("name")
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val groupOrder = request.getParameter("order")?.toIntOrNull()
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val group = Group(Group.getNewGroupID(), groupOrder, user.userID, groupName)
        if (LocalConfig.friendGroupService.addGroup(group) == null) {
            ServiceErrorEnum.IO_EXCEPTION.throwout()
        } else {
            return ResponseDataUtils.ok(group)
        }
    }

    @RequestMapping(value = ["/update"])
    fun update(request: HttpServletRequest, session: HttpSession): ResponseDataInterface {
        val user = session.getAttribute("user") as User
        val groupID: Long = request.getParameter("id")?.toLongOrNull()
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val eGroup = LocalConfig.friendGroupService.getGroup(groupID)
                ?: ServiceErrorEnum.GROUP_NOT_EXISTS.throwout()
        if (!eGroup.ownerVerify(user.userID)) {
            ServiceErrorEnum.GROUP_NOT_ALLOW.throwout()
        }
        val groupName = request.getParameter("name")
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        if (groupName.length > StaticConfig.maxGroupName) {
            ServiceErrorEnum.GROUP_NAME_TOO_LONG.throwout()
        }
        val groupOrder = request.getParameter("order")?.toIntOrNull()
                ?: ServiceErrorEnum.INSUFFICIENT_PARAMETERS.throwout()
        val group = Group(groupID, groupOrder, user.userID, groupName)
        if (LocalConfig.friendGroupService.updateGroup(group) == null) {
            ServiceErrorEnum.IO_EXCEPTION.throwout()
        } else {
            return ResponseDataUtils.ok(group)
        }
    }


}
