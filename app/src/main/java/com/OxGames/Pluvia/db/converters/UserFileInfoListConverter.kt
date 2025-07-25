package com.OxGames.Pluvia.db.converters

import androidx.room.TypeConverter
import com.OxGames.Pluvia.data.UserFileInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserFileInfoListConverter {
    @TypeConverter
    fun fromUserFileInfoList(userFileInfoList: List<UserFileInfo>?): String? = userFileInfoList?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toUserFileInfoList(value: String?): List<UserFileInfo>? = value?.let { Json.decodeFromString<List<UserFileInfo>>(it) }
}
