package cn.skstudio.service

import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.User
import org.springframework.stereotype.Service

@Service
interface UserService {
    //初始化数据表(建表)
    fun initialize(): Int?

    fun initializeAdmin():Int?

    //获取所有普通用户
    fun getAll(): List<User>?

    //获取所有管理员
    fun getAllAdmin(): List<User>?

    //根据邮箱获取用户
    fun getUserByEmail(email: String): User?

    //根据用户名获取用户
    fun getUserByUsername(username: String): User?

    //根据昵称获取用户列表
    fun getUserByNickname(nickname: String): List<User>?

    //根据ID获取用户
    fun getUserByID(id: Long): User?

    //插入用户
    fun insertUser(user: User): Int?

    //更新用户常规信息
    fun updateUser(user: User): Int?

    //根据ID删除用户
    fun deleteUser(id: Long): Int?

    //更新用户的登录信息(包括lastSessionID和IP)
    fun updateLoginInfo(user: User): Int?

    //更新用户的密码信息
    fun updatePassword(user: User): Int?

    //检查用户名是否符合
    fun checkUsername(username: String): ServiceErrorEnum

    //检查邮箱是否合格
    fun checkEmail(email: String): Boolean
}