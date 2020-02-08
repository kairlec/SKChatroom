package cn.skstudio.dao

import cn.skstudio.pojo.User
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface UserMapper {
    //初始化数据表(建表)
    fun initialize(): Int?

    //初始化管理员
    fun initializeAdmin(user: User): Int?

    //获取所有普通用户
    fun getAll(): List<User>?

    //获取所有管理员
    fun getAllAdmin(): List<User>?

    //根据邮箱获取用户
    fun getUserByEmail(@Param("email") email: String): User?

    //根据用户名获取用户
    fun getUserByUsername(@Param("username") username: String): User?

    //根据ID获取用户
    fun getUserByID(@Param("id") id: Long): User?

    //插入用户
    fun insertUser(user: User): Int?

    //更新用户常规信息
    fun updateUser(user: User): Int?

    //根据ID删除用户
    fun deleteUser(@Param("id") id: Long): Int?

    //更新用户的登录信息(包括lastSessionID和IP)
    fun updateLoginInfo(user: User): Int?

    //更新用户的密码信息
    fun updatePassword(user: User): Int?

}