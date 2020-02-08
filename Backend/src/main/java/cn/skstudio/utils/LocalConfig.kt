package cn.skstudio.utils

import cn.skstudio.service.ActionMessageService
import cn.skstudio.service.impl.*
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class LocalConfig {
    @Autowired
    private lateinit var userServiceTemp: UserServiceImpl

    @Autowired
    private lateinit var mailSenderServiceTemp: MailSenderServiceImpl

    @Autowired
    private lateinit var friendGroupServiceTemp: FriendGroupServiceImpl

    @Autowired
    private lateinit var friendServiceTemp: FriendServiceImpl

    @Autowired
    private lateinit var actionMessageServiceTemp: ActionMessageServiceImpl

    @PostConstruct
    fun init() {
        userService = userServiceTemp
        if (userService.initialize() == null) {
            logger.warn("Init database table [User] failed")
        }
        friendGroupService = friendGroupServiceTemp
        if (friendGroupService.initialize() == null) {
            logger.warn("Init database table [FriendGroup] failed")
        }
        friendService = friendServiceTemp
        if (friendService.initialize() == null) {
            logger.warn("Init database table [Friend] failed")
        }
        actionMessageService = actionMessageServiceTemp
        if (actionMessageService.initialize() == null) {
            logger.warn("Init database table [ActionMessage] failed")
        }
        mailSenderService = mailSenderServiceTemp
        if (mailSenderService.initialize() == null) {
            logger.warn("Init database table [MailSender] failed")
        }
        logger.info("Init finished")
    }

    companion object {
        private val logger = LogManager.getLogger(LocalConfig::class.java)
        lateinit var userService: UserServiceImpl
            private set
        lateinit var mailSenderService: MailSenderServiceImpl
            private set
        lateinit var friendGroupService: FriendGroupServiceImpl
            private set
        lateinit var friendService: FriendServiceImpl
            private set
        lateinit var actionMessageService: ActionMessageServiceImpl
            private set
    }
}