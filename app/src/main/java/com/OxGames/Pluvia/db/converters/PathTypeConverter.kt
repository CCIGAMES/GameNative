package com.OxGames.Pluvia.db.converters

import androidx.room.TypeConverter
import com.OxGames.Pluvia.enums.PathType

class PathTypeConverter {
    @TypeConverter
    fun fromPathType(pathType: PathType): String = pathType.name

    @TypeConverter
    fun toPathType(value: String): PathType = PathType.from(value)
}
