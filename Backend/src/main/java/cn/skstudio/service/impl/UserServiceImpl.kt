package cn.skstudio.service.impl

import cn.skstudio.config.system.StaticConfig
import cn.skstudio.dao.UserMapper
import cn.skstudio.exception.ServiceErrorEnum
import cn.skstudio.local.utils.ResourcesUtils
import cn.skstudio.pojo.Group
import cn.skstudio.pojo.User
import cn.skstudio.service.UserService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.util.regex.Pattern
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

@Service
class UserServiceImpl : UserService {
    companion object {
        private val logger = LogManager.getLogger(UserServiceImpl::class.java)

        fun checkUsername(username: String): ServiceErrorEnum {
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

        fun checkEmail(email: String): Boolean {
            return SendEmailService.checkEmail(email)
        }
    }

    @Autowired
    private lateinit var userMapper: UserMapper

    @Autowired
    @Lazy
    private lateinit var friendGroupService: FriendGroupServiceImpl

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @PostConstruct
    fun init() {
        if (initialize() == null) {
            logger.fatal("Init database table [User] failed")
            exitProcess(SpringApplication.exit(applicationContext))
        }
        if (initializeAdmin() == null) {
            logger.fatal("Init user admin failed")
            exitProcess(SpringApplication.exit(applicationContext))
        }
        logger.info("Init database table [User] success")
    }

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
                updateUser.setPassword("skadmin", true)
                updateUser.admin = true
                updateUser.username = "skadmin"
                updateUser.nickname = "管理员"
                updateUser.email = "admin@admin.admin"
                userMapper.initializeAdmin(updateUser)
                friendGroupService.addGroup(Group.newDefaultGroup(user.userID))
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
            friendGroupService.addGroup(Group.newDefaultGroup(user.userID))
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

}