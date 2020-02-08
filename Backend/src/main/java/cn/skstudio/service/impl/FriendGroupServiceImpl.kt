package cn.skstudio.service.impl

import cn.skstudio.dao.FriendGroupMapper
import cn.skstudio.pojo.Group
import cn.skstudio.service.FriendGroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FriendGroupServiceImpl : FriendGroupService {
    @Autowired
    private lateinit var friendGroupMapper: FriendGroupMapper

    override fun initialize(): Int? {
        return try {
            friendGroupMapper.initialize()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun addGroup(group: Group): Int? {
        return try {
            friendGroupMapper.addGroup(group)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun updateGroup(group: Group): Int? {
        return try {
            friendGroupMapper.updateGroup(group)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun deleteGroup(groupID: Long): Int? {
        return try {
            friendGroupMapper.deleteGroup(groupID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserGroup(userID: Long): List<Group>? {
        return try {
            friendGroupMapper.getUserGroup(userID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}