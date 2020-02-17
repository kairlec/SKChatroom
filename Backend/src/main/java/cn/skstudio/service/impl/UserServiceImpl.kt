package cn.skstudio.service.impl

import cn.skstudio.config.static.StaticConfig
import cn.skstudio.dao.FriendGroupMapper
import cn.skstudio.dao.UserMapper
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.pojo.Group
import cn.skstudio.pojo.User
import cn.skstudio.service.UserService
import cn.skstudio.utils.SendEmail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class UserServiceImpl : UserService {
    @Autowired
    private lateinit var userMapper: UserMapper

    @Autowired
    private lateinit var friendGroupMapper: FriendGroupMapper

    override fun initialize(): Int? {
        return try {
            userMapper.initialize()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun initializeAdmin(): Int? {
        return try {
            val adminList = getAllAdmin()
            if (adminList == null || adminList.isEmpty()) {
                val user = User()
                user.userID = 1
                user.admin = true
                user.username = "skadmin"
                user.updatePassword("skadmin")
                user.updateNickname("管理员")
                user.email = ""
                userMapper.initializeAdmin(user)
                friendGroupMapper.addGroup(Group.newDefaultGroup(user.userID))
            } else {
                1
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getAll(): List<User>? {
        return try {
            userMapper.getAll()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllAdmin(): List<User>? {
        return try {
            userMapper.getAllAdmin()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserByEmail(email: String): User? {
        return try {
            userMapper.getUserByEmail(email)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserByUsername(username: String): User? {
        return try {
            userMapper.getUserByUsername(username)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserByNickname(nickname: String): List<User>? {
        return try {
            userMapper.getUserByNickname(nickname)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun searchUserByNickname(nickname: String): List<User>? {
        return try {
            userMapper.searchUserByNickname(nickname)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserByID(id: Long): User? {
        return try {
            userMapper.getUserByID(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun insertUser(user: User): Int? {
        return try {
            userMapper.insertUser(user)
            friendGroupMapper.addGroup(Group.newDefaultGroup(user.userID))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun updateUser(user: User): Int? {
        return try {
            userMapper.updateUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun deleteUser(id: Long): Int? {
        return try {
            userMapper.deleteUser(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun updateLoginInfo(user: User): Int? {
        return try {
            userMapper.updateLoginInfo(user)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun updatePassword(user: User): Int? {
        return try {
            userMapper.updatePassword(user)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun checkUsername(username: String): ServiceErrorEnum {
        val realUsername = username.trim { it <= ' ' }
        if (realUsername.isEmpty()) {
            return ServiceErrorEnum.USERNAME_EMPTY
        }
        if (realUsername.length > StaticConfig.maxUsernameLength) {
            return ServiceErrorEnum.USERNAME_TOO_LONG
        }
        val pattern = Pattern.compile("[0-9a-zA-Z]*")
        val matcher = pattern.matcher(realUsername)
        if (!matcher.matches()) {
            return ServiceErrorEnum.USERNAME_CONTAINS_SP_CHAR
        }
        val digital = Pattern.compile("[0-9]*")
        val dMatcher = digital.matcher(realUsername)
        return if (dMatcher.matches()) {
            ServiceErrorEnum.USERNAME_ALL_DIGITAL
        } else ServiceErrorEnum.NO_ERROR
    }

    override fun checkEmail(email: String): Boolean {
        return SendEmail.checkEmail(email)
    }
}