package com.syla.healthfit.data.repository

import com.syla.healthfit.data.local.dao.FoodItemDao
import com.syla.healthfit.data.local.dao.FoodLogDao
import com.syla.healthfit.data.local.entity.FoodItemEntity
import com.syla.healthfit.data.local.entity.FoodLogEntity
import com.syla.healthfit.model.FoodItem
import com.syla.healthfit.model.FoodLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val foodItemDao: FoodItemDao,
    private val foodLogDao: FoodLogDao
) {
    fun foodItems(): Flow<List<FoodItem>> =
        foodItemDao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun search(query: String): List<FoodItem> =
        foodItemDao.search(query).map { it.toDomain() }

    suspend fun findById(id: Long): FoodItem? =
        foodItemDao.getById(id)?.toDomain()

    suspend fun findByName(name: String): FoodItem? =
        foodItemDao.findByName(name)?.toDomain()

    fun logsFor(date: LocalDate): Flow<List<FoodLog>> =
        foodLogDao.observeForDate(date).map { logs -> logs.map { it.toDomain() } }

    suspend fun insertFoodItem(item: FoodItem) {
        foodItemDao.upsert(item.toEntity())
    }

    suspend fun insertLog(log: FoodLog) {
        foodLogDao.upsert(log.toEntity())
    }

    suspend fun deleteLog(logId: Long) {
        foodLogDao.getById(logId)?.let { foodLogDao.delete(it) }
    }

    suspend fun totalCaloriesFor(date: LocalDate): Int =
        foodLogDao.totalCalories(date) ?: 0

    suspend fun insertSeedData(items: List<FoodItem>) {
        foodItemDao.insertAll(items.map { it.toEntity() })
    }
}

private fun FoodItemEntity.toDomain(): FoodItem =
    FoodItem(id = id, name = name, kcalPer100g = kcalPer100g, defaultUnit = defaultUnit)

private fun FoodItem.toEntity(): FoodItemEntity =
    FoodItemEntity(id = if (id == 0L) 0 else id, name = name, kcalPer100g = kcalPer100g, defaultUnit = defaultUnit)

private fun FoodLogEntity.toDomain(): FoodLog =
    FoodLog(
        id = id,
        date = date,
        foodItemId = foodItemId,
        customName = customName,
        amount = amount,
        unit = unit,
        computedKcal = computedKcal
    )

private fun FoodLog.toEntity(): FoodLogEntity =
    FoodLogEntity(
        id = if (id == 0L) 0 else id,
        date = date,
        foodItemId = foodItemId,
        customName = customName,
        amount = amount,
        unit = unit,
        computedKcal = computedKcal
    )
