package cn.skstudio.service

import cn.skstudio.pojo.Group
import org.springframework.stereotype.Service

@Service
interface FriendGroupService {
    fun initialize():Int?

    fun addGroup(group: Group): Int?

    fun updateGroup(group: Group): Int?

    fun deleteGroup(groupID: Long): Int?

    fun getUserGroup(userID: Long): List<Group>?

}