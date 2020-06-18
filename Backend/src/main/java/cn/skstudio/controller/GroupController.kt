package cn.skstudio.controller;

import cn.skstudio.annotation.JsonRequestMapping
import cn.skstudio.config.system.StaticConfig
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.intf.ResponseDataInterface
import cn.skstudio.local.utils.ResponseDataUtils.responseOK
import cn.skstudio.pojo.Group
import cn.skstudio.pojo.User
import cn.skstudio.service.impl.FriendGroupServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute

/**
 * @program: Backend
 * @description: 组操作的API接口
 * @author: Kairlec
 * @create: 2020-02-20 19:49
 */

@RestController
@JsonRequestMapping(value = ["/api/group"])
class GroupController {

    @Autowired
    private lateinit var friendGroupService: FriendGroupServiceImpl

    @RequestMapping(value = ["/delete"])
    fun delete(@SessionAttribute(name = "user") user: User,
               @RequestParam(name = "id") groupID: Long): ResponseDataInterface {
        val group = friendGroupService.getGroup(groupID)
                ?: ServiceErrorEnum.GROUP_NOT_EXISTS.throwout()
        if (!group.ownerVerify(user.userID)) {
            ServiceErrorEnum.GROUP_NOT_ALLOW.throwout()
        }
        if (groupID == -user.userID) {
            ServiceErrorEnum.DEFAULT_DELETE_NOT_ALLOW.throwout()
        }
        if (friendGroupService.deleteGroup(groupID) == null) {
            ServiceErrorEnum.IO_EXCEPTION.throwout()
        } else {
            return groupID.responseOK
        }
    }

    @RequestMapping(value = ["/create"])
    fun create(@SessionAttribute(name = "user") user: User,
               @RequestParam(name = "name") groupName: String,
               @RequestParam(name = "order") groupOrder: Int): ResponseDataInterface {
        val group = Group(Group.getNewGroupID(), groupOrder, user.userID, groupName)
        if (friendGroupService.addGroup(group) == null) {
            ServiceErrorEnum.IO_EXCEPTION.throwout()
        } else {
            return group.responseOK
        }
    }

    @RequestMapping(value = ["/update"])
    fun update(@SessionAttribute(name = "user") user: User,
               @RequestParam(name = "id") groupID: Long,
               @RequestParam(name = "name") groupName: String,
               @RequestParam(name = "order") groupOrder: Int): ResponseDataInterface {
        val eGroup = friendGroupService.getGroup(groupID)
                ?: ServiceErrorEnum.GROUP_NOT_EXISTS.throwout()
        if (!eGroup.ownerVerify(user.userID)) {
            ServiceErrorEnum.GROUP_NOT_ALLOW.throwout()
        }
        if (groupName.length > StaticConfig.maxGroupName) {
            ServiceErrorEnum.GROUP_NAME_TOO_LONG.throwout()
        }
        val group = Group(groupID, groupOrder, user.userID, groupName)
        if (friendGroupService.updateGroup(group) == null) {
            ServiceErrorEnum.IO_EXCEPTION.throwout()
        } else {
            return group.responseOK
        }
    }


}
