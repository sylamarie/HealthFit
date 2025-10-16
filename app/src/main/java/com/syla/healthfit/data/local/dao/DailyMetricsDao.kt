package com.syla.healthfit.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.syla.healthfit.data.local.entity.DailyMetricsEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyMetricsDao {
    @Upsert
    suspend fun upsert(metrics: DailyMetricsEntity)

    @Query("SELECT * FROM daily_metrics WHERE date = :date LIMIT 1")
    fun observeForDate(date: LocalDate): Flow<DailyMetricsEntity?>

    @Query("SELECT * FROM daily_metrics WHERE date = :date LIMIT 1")
    suspend fun getForDate(date: LocalDate): DailyMetricsEntity?

    @Query("UPDATE daily_metrics SET steps = :steps WHERE date = :date")
    suspend fun updateSteps(date: LocalDate, steps: Int)

    @Query("UPDATE daily_metrics SET waterGlasses = :glasses, waterMl = :waterMl WHERE date = :date")
    suspend fun updateWater(date: LocalDate, glasses: Int, waterMl: Int)

    @Query("UPDATE daily_metrics SET caloriesConsumed = :calories WHERE date = :date")
    suspend fun updateCalories(date: LocalDate, calories: Int)
}