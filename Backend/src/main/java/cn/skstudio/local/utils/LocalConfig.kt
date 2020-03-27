package cn.skstudio.local.utils

import cn.skstudio.service.impl.*
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.readValue
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

    @Autowired
    private lateinit var redisServiceTemp: RedisServiceImpl

    @Autowired
    private lateinit var objectMapperTemp: ObjectMapper

    @PostConstruct
    fun init() {
        objectMapper = objectMapperTemp

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
        if (userService.initializeAdmin() == null) {
            logger.warn("Init user admin failed")
        }
        redisService = redisServiceTemp
        logger.info("Init finished")
    }

    companion object {
        lateinit var objectMapper: ObjectMapper
            private set


        fun String.Companion.toJSON(`object`: Any): String {
            return objectMapper.writeValueAsString(`object`)
        }

        inline fun <reified T> String.json2Object(): T? {
            return try {
                objectMapper.readValue(this)
            } catch (e: JsonParseException) {
                null
            }
        }

        fun String.toJsonNode(): JsonNode? {
            return  try {
                objectMapper.readTree(this)
            } catch (e: JsonParseException) {
                null
            }
        }

        fun String.toObjectNode(): ObjectNode? {
            return  try {
                objectMapper.readTree(this) as ObjectNode
            } catch (e: JsonParseException) {
                null
            }
        }

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
        lateinit var redisService: RedisServiceImpl
            private set
    }
}