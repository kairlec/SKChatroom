package cn.skstudio.dao

import cn.skstudio.pojo.Group
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface FriendGroupMapper {
    fun initialize(): Int?

    fun addGroup(group: Group): Int?

    fun updateGroup(group: Group): Int?

    fun deleteGroup(@Param("groupID") groupID: Long?): Int?

    fun getUserGroup(@Param("userID") userID: Long?): List<Group>?

    fun getGroup(@Param("groupID") groupID: Long?): Group?

}