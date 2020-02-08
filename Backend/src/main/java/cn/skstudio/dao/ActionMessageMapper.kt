package cn.skstudio.dao

import cn.skstudio.pojo.ActionMessage
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.sql.Timestamp

@Mapper
interface ActionMessageMapper {
    fun initialize(): Int?

    fun getAllFromActionMessages(@Param("userID") userID: Long?, @Param("after") after: Timestamp): List<ActionMessage>?

    fun getAllToActionMessages(@Param("userID") userID: Long?, @Param("after") after: Timestamp): List<ActionMessage>?

    fun getFromToActionMessages(@Param("fromUserID") fromUserID: Long?, @Param("toUserID") toUserID: Long?, @Param("after") after: Timestamp): List<ActionMessage>?

    fun addActionMessage(message: ActionMessage): Int?

    fun read(@Param("messageID") messageID: Long?): Int?

    //TODO 延缓消息
}