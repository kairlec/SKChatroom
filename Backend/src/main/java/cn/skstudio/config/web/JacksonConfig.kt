package cn.skstudio.config.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import java.text.DateFormat

/**
 *@program: Backend
 *@description: Jackson全局配置
 *@author: Kairlec
 *@create: 2020-03-14 20:17
 */

@Configuration
open class JacksonConfig {
    /**
     * Jackson全局转化long类型为String
     */
    @Bean
    open fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.serializerByType(Long::class.java, ToStringSerializer.instance) }
    }

    @Bean
    @Primary
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @ConditionalOnMissingBean(ObjectMapper::class)
    open fun jacksonObjectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
        val objectMapper: ObjectMapper = builder.createXmlMapper(false).build()
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        objectMapper.disable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS)
        objectMapper.dateFormat = DateFormat.getDateTimeInstance()
        return objectMapper
    }

    @Bean
    @Primary
    @ConditionalOnBean(ObjectMapper::class)
    open fun jacksonConverters(objectMapper: ObjectMapper): HttpMessageConverters {
        val jackson2HttpMessageConverter = MappingJackson2HttpMessageConverter()
        jackson2HttpMessageConverter.objectMapper = objectMapper
        return HttpMessageConverters(jackson2HttpMessageConverter)
    }
}