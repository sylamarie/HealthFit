package com.syla.healthfit.model

data class UserProfile(
    val age: Int = 0,
    val heightCm: Int = 0,
    val weightKg: Float = 0f,
    val goalWeightKg: Float = 0f,
    val sex: Sex = Sex.Other
)

enum class Sex { Male, Female, Other }

data class DailyGoals(
    val caloriesKcal: Int,
    val waterMl: Int,
    val stepsTarget: Int
)

data class ChecklistItem(
    val id: String,
    val title: String,
    val done: Boolean
)