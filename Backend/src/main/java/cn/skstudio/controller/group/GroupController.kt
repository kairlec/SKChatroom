package cn.skstudio.controller.group;

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
@RequestMapping("/api/group")
class GroupController {
    @RequestMapping(value = ["/delete"])
    fun delete(request: HttpServletRequest, session: HttpSession): String {
        val user = session.getAttribute("user") as User
        val groupID: Long
        try {
            groupID = request.getParameter("id")?.toLong()
                    ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID)
        }
        val eGroup = LocalConfig.friendGroupService.getGroup(groupID)
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.GROUP_NOT_EXISTS)
        if (!eGroup.ownerVerify(user.userID)) {
            return ResponseDataUtils.Error(ServiceErrorEnum.GROUP_NOT_ALLOW)
        }
        return if (LocalConfig.friendGroupService.deleteGroup(groupID) == null) {
            ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        } else {
            ResponseDataUtils.successData(groupID)
        }
    }

    @RequestMapping(value = ["/create"])
    fun create(request: HttpServletRequest, session: HttpSession): String {
        val user = session.getAttribute("user") as User
        val groupName = request.getParameter("name")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        val groupOrder: Int
        try {
            groupOrder = request.getParameter("order")?.toInt()
                    ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID)
        }
        val group = Group(Group.getNewGroupID(), groupOrder, user.userID, groupName)
        return if (LocalConfig.friendGroupService.addGroup(group) == null) {
            ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        } else {
            ResponseDataUtils.successData(group)
        }
    }

    @RequestMapping(value = ["/update"])
    fun update(request: HttpServletRequest, session: HttpSession): String {
        val user = session.getAttribute("user") as User
        val groupID: Long
        try {
            groupID = request.getParameter("id")?.toLong()
                    ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID)
        }
        val eGroup = LocalConfig.friendGroupService.getGroup(groupID)
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.GROUP_NOT_EXISTS)
        if (!eGroup.ownerVerify(user.userID)) {
            return ResponseDataUtils.Error(ServiceErrorEnum.GROUP_NOT_ALLOW)
        }
        val groupName = request.getParameter("name")
                ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        if (groupName.length > StaticConfig.maxGroupName) {
            return ResponseDataUtils.Error(ServiceErrorEnum.GROUP_NAME_TOO_LONG)
        }
        val groupOrder: Int
        try {
            groupOrder = request.getParameter("order")?.toInt()
                    ?: return ResponseDataUtils.Error(ServiceErrorEnum.INSUFFICIENT_PARAMETERS)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return ResponseDataUtils.Error(ServiceErrorEnum.ID_INVALID)
        }
        val group = Group(groupID, groupOrder, user.userID, groupName)
        return if (LocalConfig.friendGroupService.updateGroup(group) == null) {
            ResponseDataUtils.Error(ServiceErrorEnum.IO_EXCEPTION)
        } else {
            ResponseDataUtils.successData(group)
        }
    }


}
