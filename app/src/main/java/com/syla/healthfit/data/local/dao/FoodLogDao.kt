package com.syla.healthfit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.syla.healthfit.data.local.entity.FoodLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FoodLogDao {
    @Upsert
    suspend fun upsert(log: FoodLogEntity)

    @Delete
    suspend fun delete(log: FoodLogEntity)

    @Query("SELECT * FROM food_logs WHERE date = :date ORDER BY id DESC")
    fun observeForDate(date: LocalDate): Flow<List<FoodLogEntity>>

    @Query("SELECT SUM(computedKcal) FROM food_logs WHERE date = :date")
    suspend fun totalCalories(date: LocalDate): Int?

    @Query("SELECT * FROM food_logs WHERE id = :id")
    suspend fun getById(id: Long): FoodLogEntity?
}