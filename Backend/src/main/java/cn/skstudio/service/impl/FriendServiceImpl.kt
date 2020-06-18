package cn.skstudio.service.impl

import cn.skstudio.dao.FriendMapper
import cn.skstudio.pojo.Friend
import cn.skstudio.service.FriendService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

@Service
class FriendServiceImpl : FriendService {
    companion object {
        private val logger = LogManager.getLogger(FriendGroupServiceImpl::class.java)
    }

    @Autowired
    private lateinit var friendMapper: FriendMapper

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @PostConstruct
    fun init() {
        if (initialize() == null) {
            logger.fatal("Init database table [Friend] failed")
            exitProcess(SpringApplication.exit(applicationContext))
        } else {
            logger.info("Init database table [Friend] success")
        }
    }

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