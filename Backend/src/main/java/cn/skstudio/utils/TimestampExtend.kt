package cn.skstudio.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class TimestampSerializer : JsonSerializer<Timestamp>() {
    override fun serialize(value: Timestamp, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeString(value.time.toString())
    }
}

class TimestampDeserializer : JsonDeserializer<Timestamp>() {
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Timestamp {
        return Timestamp.valueOf(LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonParser.text.toLong()), ZoneId.systemDefault()))
    }
}
