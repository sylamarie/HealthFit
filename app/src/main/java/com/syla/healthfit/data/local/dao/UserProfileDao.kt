package com.syla.healthfit.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.syla.healthfit.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Upsert
    suspend fun upsert(profile: UserProfileEntity)
}