package cn.skstudio.service

import cn.skstudio.pojo.Friend
import org.springframework.stereotype.Service

@Service
interface FriendService {
    fun initialize(): Int?

    fun addFriend(userID: Long,friendID: Long,  userGroupID: Long,  friendGroupID: Long): Int?

    fun deleteFriend(userID: Long,friendID: Long): Int?

    fun moveFriend( userID: Long, friendID: Long,  GroupID: Long): Int?

    fun getFriendList( userID: Long): List<Friend>?

}