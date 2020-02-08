package cn.skstudio.dao

import cn.skstudio.pojo.Friend
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface FriendMapper {
    fun initialize(): Int?

    fun addFriend(@Param("userID") userID: Long, @Param("friendID") friendID: Long, @Param("userGroupID") userGroupID: Long, @Param("friendGroupID") friendGroupID: Long): Int?

    fun deleteFriend(@Param("userID") userID: Long, @Param("friendID") friendID: Long): Int?

    fun moveFriend(@Param("userID") userID: Long, @Param("friendID") friendID: Long, @Param("GroupID") GroupID: Long): Int?

    fun getFriendList(@Param("userID") userID: Long): List<Friend>?

}