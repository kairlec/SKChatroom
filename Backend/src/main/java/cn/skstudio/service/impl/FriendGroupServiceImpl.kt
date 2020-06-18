package cn.skstudio.service.impl

import cn.skstudio.dao.FriendGroupMapper
import cn.skstudio.pojo.Group
import cn.skstudio.service.FriendGroupService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import kotlin.system.exitProcess

@Service
open class FriendGroupServiceImpl : FriendGroupService {
    companion object {
        private val logger = LogManager.getLogger(FriendGroupServiceImpl::class.java)
    }

    //前置注入
    @Autowired
    private lateinit var userServiceImpl: UserServiceImpl

    @Autowired
    private lateinit var friendGroupMapper: FriendGroupMapper

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @PostConstruct
    fun init() {
        if (initialize() == null) {
            logger.fatal("Init database table [FriendGroup] failed")
            exitProcess(SpringApplication.exit(applicationContext))
        }else {
            logger.info("Init database table [FriendGroup] success")
        }
    }

    override fun initialize(): Int? {
        return try {
            friendGroupMapper.initialize()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun addGroup(group: Group): Int? {
        return try {
            friendGroupMapper.addGroup(group)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun updateGroup(group: Group): Int? {
        return try {
            friendGroupMapper.updateGroup(group)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun deleteGroup(groupID: Long): Int? {
        return try {
            friendGroupMapper.deleteGroup(groupID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getUserGroup(userID: Long): List<Group>? {
        return try {
            friendGroupMapper.getUserGroup(userID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getGroup(groupID: Long): Group? {
        return try {
            friendGroupMapper.getGroup(groupID)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}