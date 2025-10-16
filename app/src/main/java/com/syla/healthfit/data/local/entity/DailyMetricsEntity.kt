package com.syla.healthfit.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "daily_metrics",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailyMetricsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val steps: Int,
    val waterGlasses: Int,
    val waterMl: Int,
    val caloriesConsumed: Int,
    val calorieTarget: Int
)