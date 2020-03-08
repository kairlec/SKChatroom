package cn.skstudio.service.impl

import cn.skstudio.config.static.StaticConfig
import cn.skstudio.controller.user.UserController
import cn.skstudio.dao.FriendGroupMapper
import cn.skstudio.dao.UserMapper
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.ResourcesUtils
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

    private fun convertAvatar(user: User?): User? {
        if (user == null) {
            return null
        }
        if (user.avatar != null && user.avatar != "@DEFAULT?") {
            val base64Data = ResourcesUtils.getImageResource(ResourcesUtils.ResourceType.Avatar, user.avatar!!).toBase64()
            user.avatar = base64Data
        }
        return user
    }

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
                val updateUser = user.readyToUpdate()
                updateUser[User.UpdateUser.ADMIN_FIELD] = true
                updateUser[User.UpdateUser.USERNAME_FIELD] = "skadmin"
                updateUser[User.UpdateUser.PASSWORD_FIELD, true] = "skadmin"
                updateUser[User.UpdateUser.NICKNAME_FIELD] = "管理员"
                updateUser[User.UpdateUser.EMAIL_FIELD] = ""
                userMapper.initializeAdmin(updateUser)
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
            val list = userMapper.getAll()
            list?.forEach { convertAvatar(it) }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getAllAdmin(): List<User>? {
        return try {
            val list = userMapper.getAllAdmin()
            list?.forEach { convertAvatar(it) }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserByEmail(email: String): User? {
        return try {
            convertAvatar(userMapper.getUserByEmail(email))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserByUsername(username: String): User? {
        return try {
            convertAvatar(userMapper.getUserByUsername(username))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserByNickname(nickname: String): List<User>? {
        return try {
            val list = userMapper.getUserByNickname(nickname)
            list?.forEach { convertAvatar(it) }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun searchUserByNickname(nickname: String): List<User>? {
        return try {
            val list = userMapper.searchUserByNickname(nickname)
            list?.forEach { convertAvatar(it) }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserByID(id: Long): User? {
        return try {
            convertAvatar(userMapper.getUserByID(id))
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

    override fun updateUser(user: User.UpdateUser): Int? {
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

    override fun updateLoginInfo(user: User.UpdateUser): Int? {
        return try {
            userMapper.updateLoginInfo(user)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun updatePassword(user: User.UpdateUser): Int? {
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