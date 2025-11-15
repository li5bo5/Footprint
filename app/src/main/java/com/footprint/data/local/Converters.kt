package com.footprint.data.local

import androidx.room.TypeConverter
import com.footprint.data.model.Mood
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromEpochDays(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun toEpochDays(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun fromMood(value: String?): Mood? = value?.let { Mood.valueOf(it) }

    @TypeConverter
    fun moodToString(mood: Mood?): String? = mood?.name

    @TypeConverter
    fun fromStringList(value: String?): List<String> = value?.takeIf { it.isNotBlank() }
        ?.split("|")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()

    @TypeConverter
    fun listToString(list: List<String>?): String = list?.joinToString("|") ?: ""
}
