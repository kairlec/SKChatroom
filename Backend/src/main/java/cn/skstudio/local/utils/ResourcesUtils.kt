package cn.skstudio.local.utils

import cn.skstudio.pojo.SKImage
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

object ResourcesUtils {
    enum class ResourceType {
        Avatar;
    }

    private val resourceRootPath = Path.of("Resources")

    fun getResourcePath(resourceType: ResourceType, resourceName: String): Path {
        return Path.of(resourceRootPath.toAbsolutePath().toString(), "${resourceType.name}${File.separator}$resourceName")
    }

    @Deprecated("此方法会读取inputStream流内数据导致流内数据丢失")
    fun saveResource(resourceType: ResourceType, resourceName: String, inputStream: InputStream, overWrite: Boolean = false) {
        val resourcePath = getResourcePath(resourceType, resourceName)
        if (Files.exists(resourcePath) && !overWrite) {
            throw java.nio.file.FileAlreadyExistsException(resourcePath.toString(), null, "File is already exist and overwrite option is not set of false.")
        }
        Files.deleteIfExists(resourcePath)
        Files.createDirectories(resourcePath.parent)
        Files.createFile(resourcePath)
        resourcePath.toFile().outputStream().use {
            inputStream.transferTo(it)
        }
    }

    fun saveResource(resourceType: ResourceType, resourceName: String, multipartFile: MultipartFile, overWrite: Boolean = false) {
        val resourcePath = getResourcePath(resourceType, resourceName)
        if (Files.exists(resourcePath) && !overWrite) {
            throw java.nio.file.FileAlreadyExistsException(resourcePath.toString(), null, "File is already exist and overwrite option is not set of false.")
        }
        Files.deleteIfExists(resourcePath)
        Files.createDirectories(resourcePath.parent)
        Files.createFile(resourcePath)
        multipartFile.transferTo(resourcePath.toFile())
    }

    fun resourceExists(resourceType: ResourceType, resourceName: String): Boolean {
        return Files.exists(getResourcePath(resourceType, resourceName))
    }

    fun deleteResource(resourceType: ResourceType, resourceName: String) {
        Files.deleteIfExists(getResourcePath(resourceType, resourceName))
    }

    fun getImageResource(resourceType: ResourceType, resourceName: String): SKImage {
        val resourcePath = getResourcePath(resourceType, resourceName)
        if (!Files.exists(resourcePath)) {
            throw FileNotFoundException(resourcePath.toString())
        }
        return SKImage(resourcePath.toFile())
    }
}