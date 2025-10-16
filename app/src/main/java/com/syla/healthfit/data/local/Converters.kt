package com.syla.healthfit.data.local

import androidx.room.TypeConverter
import com.syla.healthfit.model.ActivityLevel
import com.syla.healthfit.model.Sex
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun toDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun fromSex(value: String?): Sex? = value?.let { runCatching { Sex.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toSex(value: Sex?): String? = value?.name

    @TypeConverter
    fun fromActivity(value: String?): ActivityLevel? =
        value?.let { runCatching { ActivityLevel.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toActivity(value: ActivityLevel?): String? = value?.name
}