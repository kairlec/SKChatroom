package cn.skstudio.dao

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface ConfigMapper {
    fun initialize(): Int?

    fun insertConfig(@Param("name") name: String,
                     @Param("value") value: String): Int?

    fun deleteConfig(@Param("name") name: String): Int?

    fun containsConfig(@Param("name") name: String): Int?

    fun setConfig(@Param("name") name: String,
                  @Param("value") value: String): Int?

    fun getConfig(@Param("name") name: String): String?
}