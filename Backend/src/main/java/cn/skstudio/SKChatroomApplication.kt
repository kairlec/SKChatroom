package cn.skstudio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RestController


@RestController
@EnableConfigurationProperties
@SpringBootApplication(exclude = [
    RedisAutoConfiguration::class,
    RedisRepositoriesAutoConfiguration::class
]
)
open class SKChatroomApplication

fun main(args: Array<String>) {
    runApplication<SKChatroomApplication>(*args)
}