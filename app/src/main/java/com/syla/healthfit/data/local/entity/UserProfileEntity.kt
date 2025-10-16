package com.syla.healthfit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.syla.healthfit.model.ActivityLevel
import com.syla.healthfit.model.Sex
import java.time.LocalDate

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val age: Int,
    val sex: Sex,
    val heightCm: Int,
    val weightKg: Float,
    val goalWeightKg: Float,
    val activityLevel: ActivityLevel,
    val glassSizeMl: Int,
    val dailyStepGoal: Int,
    val dailyWaterGoalMl: Int,
    val dailyCalorieTarget: Int,
    val lastUpdated: LocalDate
)