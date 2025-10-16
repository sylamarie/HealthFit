package com.syla.healthfit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.syla.healthfit.data.local.dao.DailyMetricsDao
import com.syla.healthfit.data.local.dao.FoodItemDao
import com.syla.healthfit.data.local.dao.FoodLogDao
import com.syla.healthfit.data.local.dao.UserProfileDao
import com.syla.healthfit.data.local.entity.DailyMetricsEntity
import com.syla.healthfit.data.local.entity.FoodItemEntity
import com.syla.healthfit.data.local.entity.FoodLogEntity
import com.syla.healthfit.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        DailyMetricsEntity::class,
        FoodItemEntity::class,
        FoodLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun dailyMetricsDao(): DailyMetricsDao
    abstract fun foodItemDao(): FoodItemDao
    abstract fun foodLogDao(): FoodLogDao
}