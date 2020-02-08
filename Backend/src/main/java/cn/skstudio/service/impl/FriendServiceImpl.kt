package cn.skstudio.service.impl

import cn.skstudio.dao.FriendMapper
import cn.skstudio.pojo.Friend
import cn.skstudio.service.FriendService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FriendServiceImpl : FriendService {
    @Autowired
    private lateinit var friendMapper: FriendMapper

    override fun initialize(): Int? {
        return try {
            friendMapper.initialize()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun addFriend(userID: Long, friendID: Long, userGroupID: Long, friendGroupID: Long): Int? {
        return try {
            friendMapper.addFriend(userID, friendID, userGroupID, friendGroupID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun deleteFriend(userID: Long, friendID: Long): Int? {
        return try {
            friendMapper.deleteFriend(userID, friendID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun moveFriend(userID: Long, friendID: Long, GroupID: Long): Int? {
        return try {
            friendMapper.moveFriend(userID, friendID, GroupID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getFriendList(userID: Long): List<Friend>? {
        return try {
            friendMapper.getFriendList(userID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}