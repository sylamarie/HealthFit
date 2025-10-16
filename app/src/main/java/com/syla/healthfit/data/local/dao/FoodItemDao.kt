package com.syla.healthfit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.syla.healthfit.data.local.entity.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Upsert
    suspend fun upsert(item: FoodItemEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items ORDER BY name")
    fun observeAll(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' ORDER BY name LIMIT 20")
    suspend fun search(query: String): List<FoodItemEntity>

    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): FoodItemEntity?

    @Query("SELECT * FROM food_items WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findByName(name: String): FoodItemEntity?
}