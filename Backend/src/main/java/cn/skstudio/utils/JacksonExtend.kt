package cn.skstudio.utils

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

/**
 *@program: Backend
 *@description: Jackson增强
 *@author: Kairlec
 *@create: 2020-03-14 19:32
 */

val jacksonObjectMapper: ObjectMapper
    get() = jacksonObjectMapper()


fun String.Companion.toJSON(`object`: Any, objectMapper: ObjectMapper = jacksonObjectMapper): String {
    return objectMapper.writeValueAsString(`object`)
}

inline fun <reified T> String.json2Object(objectMapper: ObjectMapper = jacksonObjectMapper): T? {
    return try {
        objectMapper.readValue(this)
    } catch (e: JsonParseException) {
        null
    }
}

fun String.toJsonNode(objectMapper: ObjectMapper = jacksonObjectMapper): JsonNode? {
    return try {
        objectMapper.readTree(this)
    } catch (e: JsonParseException) {
        null
    }
}

fun String.toObjectNode(objectMapper: ObjectMapper = jacksonObjectMapper): ObjectNode? {
    return try {
        objectMapper.readTree(this) as ObjectNode
    } catch (e: JsonParseException) {
        null
    }
}


fun JsonNode?.asLongOrNull() = this?.asText()?.toLongOrNull()


fun JsonNode?.asDoubleOrNull() = this?.asText()?.toDoubleOrNull()

fun JsonNode?.asTextOrNull() = this?.asText()


fun JsonNode?.asIntOrNull() = this?.asText()?.toIntOrNull()


fun JsonNode?.asBooleanOrNull(): Boolean? {
    return this?.asText()?.let {
        return when {
            it.equals("true", true) -> true
            it.equals("false", true) -> false
            else -> null
        }
    }
}

operator fun ObjectNode.set(fieldName: String, data: Any?) {
    this.putPOJO(fieldName, data)
}