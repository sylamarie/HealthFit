package com.syla.healthfit.domain

import com.syla.healthfit.model.ActivityLevel
import com.syla.healthfit.model.FoodItem
import com.syla.healthfit.model.NutritionSuggestion
import com.syla.healthfit.model.SuggestedPortion
import com.syla.healthfit.model.Sex
import com.syla.healthfit.model.UserProfile
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

object HealthCalculator {
    enum class BmiCategory(val label: String, val description: String) {
        Underweight("Underweight", "Consider increasing nutritious calorie intake to reach a healthy range."),
        Normal("Normal", "Maintain your balanced lifestyle to stay in this healthy range."),
        Overweight("Overweight", "A modest calorie deficit and regular activity can help move toward your goal."),
        Obese("Obese", "Prioritise gradual, sustainable changes and consult a professional if possible.")
    }

    fun bmi(weightKg: Float, heightCm: Int): Double {
        if (weightKg <= 0f || heightCm <= 0) return 0.0
        val meters = heightCm / 100.0
        return weightKg / meters.pow(2.0)
    }

    fun bmiCategory(bmi: Double): BmiCategory = when {
        bmi < 18.5 -> BmiCategory.Underweight
        bmi < 25 -> BmiCategory.Normal
        bmi < 30 -> BmiCategory.Overweight
        else -> BmiCategory.Obese
    }

    fun mifflinStJeorBmr(profile: UserProfile): Double {
        val base = 10 * profile.weightKg + 6.25 * profile.heightCm - 5 * profile.age
        val sexOffset = when (profile.sex) {
            Sex.Male -> 5
            Sex.Female -> -161
            Sex.Other -> -78 // midpoint between male and female adjustment
        }
        return base + sexOffset
    }

    fun tdee(profile: UserProfile): Int {
        if (profile.weightKg <= 0f || profile.heightCm <= 0 || profile.age <= 0) return 0
        val bmr = mifflinStJeorBmr(profile)
        val tdee = bmr * profile.activityLevel.multiplier
        return max(0, tdee.roundToInt())
    }

    fun calorieTarget(profile: UserProfile): Int {
        val maintenance = tdee(profile)
        if (maintenance == 0) return 0
        val delta = profile.goalWeightKg - profile.weightKg
        val adjustment = when {
            delta > 2f -> 450
            delta > 0.5f -> 300
            delta < -2f -> -450
            delta < -0.5f -> -300
            else -> 0
        }
        return (maintenance + adjustment).coerceIn(1_200, 4_000)
    }

    fun caloriesForFood(amount: Float, unit: String, item: FoodItem): Int {
        val grams = when (unit.lowercase()) {
            "g", "gram", "grams" -> amount
            "ml" -> amount // treat ml as grams for water-based foods
            "cup", "cups" -> amount * 240f
            "tbsp", "tablespoon" -> amount * 15f
            "tsp", "teaspoon" -> amount * 5f
            "slice" -> amount * 30f
            "piece", "pcs" -> amount * 50f
            else -> if (unit.equals(item.defaultUnit, ignoreCase = true)) amount * 100f else amount * 80f
        }
        val kcal = (grams / 100f) * item.kcalPer100g
        return kcal.roundToInt().coerceAtLeast(0)
    }

    fun calorieSuggestions(
        remainingCalories: Int,
        profile: UserProfile,
        pantry: List<FoodItem>
    ): List<NutritionSuggestion> {
        if (pantry.isEmpty()) return emptyList()
        val focusedFoods = pantry.take(8)
        val ideas = mutableListOf<NutritionSuggestion>()
        val absolute = abs(remainingCalories)
        val sampleTargets = when {
            remainingCalories > 0 -> listOf(
                (absolute * 0.4).roundToInt().coerceIn(150, 400),
                (absolute * 0.6).roundToInt().coerceIn(200, 500),
                (absolute * 0.8).roundToInt().coerceIn(250, 550)
            )
            remainingCalories < 0 -> listOf(absolute.coerceIn(150, 400), (absolute * 0.5).roundToInt().coerceIn(120, 320))
            else -> listOf(200, 300)
        }
        val action = when {
            remainingCalories > 0 -> "Add"
            remainingCalories < 0 -> "Trim"
            else -> "Maintain"
        }
        sampleTargets.forEachIndexed { index, target ->
            val foodA = focusedFoods[index % focusedFoods.size]
            val foodB = focusedFoods[(index + 2) % focusedFoods.size]
            val descriptor = if (remainingCalories >= 0) {
                "${foodA.name} with ${foodB.name}"
            } else {
                "Swap to ${foodA.name} and ${foodB.name}"
            }
            val portions = mutableListOf<SuggestedPortion>()
            portionForCalories((target * 0.6f).roundToInt(), foodA)?.let { portions += it }
            portionForCalories((target * 0.4f).roundToInt(), foodB)?.let { portions += it }
            if (portions.isEmpty()) {
                portionForCalories(target, foodA)?.let { portions += it }
            }
            ideas += NutritionSuggestion(
                title = "$action ~${target} kcal",
                description = descriptor,
                calories = if (remainingCalories >= 0) target else -target,
                portions = portions
            )
        }
        if (ideas.isEmpty()) {
            ideas += NutritionSuggestion(
                title = "Stay balanced",
                description = "Focus on lean protein, vegetables and hydration.",
                calories = 0
            )
        }
        return ideas
    }

    fun shouldShowCalorieReminder(remainingCalories: Int, hourOfDay: Int): Boolean {
        if (remainingCalories <= 0) return false
        return hourOfDay >= 15 // mid-afternoon reminder
    }
}

private fun portionForCalories(targetCalories: Int, food: FoodItem): SuggestedPortion? {
    if (targetCalories <= 0 || food.kcalPer100g <= 0f) return null
    val gramsNeeded = (targetCalories / food.kcalPer100g) * 100f
    if (gramsNeeded <= 0f) return null
    val unit = normalizeUnit(food.defaultUnit)
    val gramsPerUnit = gramsPerUnit(unit)
    val rawAmount = if (gramsPerUnit <= 0f) gramsNeeded else gramsNeeded / gramsPerUnit
    val roundedAmount = if (rawAmount >= 100f) {
        rawAmount.roundToInt().toFloat()
    } else {
        (rawAmount * 10f).roundToInt() / 10f
    }
    return SuggestedPortion(food = food, amount = roundedAmount, unit = unit)
}

private fun gramsPerUnit(unit: String): Float = when (unit.lowercase()) {
    "g", "gram", "grams" -> 1f
    "ml", "milliliter", "milliliters" -> 1f
    "cup", "cups" -> 240f
    "tbsp", "tablespoon", "tablespoons" -> 15f
    "tsp", "teaspoon", "teaspoons" -> 5f
    "slice", "slices" -> 30f
    "piece", "pieces", "pcs" -> 50f
    else -> 100f
}

private fun normalizeUnit(unit: String): String = when (unit.lowercase()) {
    "gram", "grams" -> "g"
    "milliliter", "milliliters" -> "ml"
    "cup", "cups" -> "cup"
    "tablespoon", "tablespoons" -> "tbsp"
    "teaspoon", "teaspoons" -> "tsp"
    "pieces", "pcs" -> "piece"
    "slice", "slices" -> "slice"
    else -> unit.ifBlank { "g" }
}
