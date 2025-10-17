package com.syla.healthfit.model

import java.time.LocalDate

enum class Sex { Male, Female, Other }

enum class ActivityLevel(val multiplier: Double) {
    Sedentary(1.2),
    LightlyActive(1.375),
    ModeratelyActive(1.55),
    VeryActive(1.725),
    ExtraActive(1.9)
}

data class UserProfile(
    val id: Int = 1,
    val age: Int = 0,
    val sex: Sex = Sex.Other,
    val heightCm: Int = 0,
    val weightKg: Float = 0f,
    val goalWeightKg: Float = 0f,
    val activityLevel: ActivityLevel = ActivityLevel.Sedentary,
    val glassSizeMl: Int = 250,
    val dailyStepGoal: Int = 8_000,
    val dailyWaterGoalMl: Int = 2_000,
    val dailyCalorieTarget: Int = 2_000,
    val lastUpdated: LocalDate = LocalDate.now()
)

data class DailyMetrics(
    val date: LocalDate,
    val steps: Int,
    val waterGlasses: Int,
    val waterMl: Int,
    val caloriesConsumed: Int,
    val calorieTarget: Int
) {
    val caloriesRemaining: Int get() = (calorieTarget - caloriesConsumed).coerceAtLeast(0)
}

data class FoodItem(
    val id: Long,
    val name: String,
    val kcalPer100g: Float,
    val defaultUnit: String
)

data class FoodLog(
    val id: Long,
    val date: LocalDate,
    val foodItemId: Long?,
    val customName: String?,
    val amount: Float,
    val unit: String,
    val computedKcal: Int
)

data class SuggestedPortion(
    val food: FoodItem,
    val amount: Float,
    val unit: String
)

data class NutritionSuggestion(
    val title: String,
    val description: String,
    val calories: Int,
    val portions: List<SuggestedPortion> = emptyList()
)
