package cn.skstudio.service

import org.springframework.stereotype.Service

@Service
interface ConfigService {
    fun initialize(): Int?

    fun getPrivateKey(): String?

    fun setPrivateKey(privateKey: String): Int?

    fun getPublicKey(): String?

    fun setPublicKey(publicKey: String): Int?

    fun containsConfig(name: String): Boolean?

    fun setConfig(name: String, value: String): Int?

    fun getConfig(name: String): String?
}