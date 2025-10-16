package com.syla.healthfit.domain

import com.syla.healthfit.model.ActivityLevel
import com.syla.healthfit.model.FoodItem
import com.syla.healthfit.model.UserProfile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HealthCalculatorTest {
    @Test
    fun bmiAndCategoryAreCalculated() {
        val profile = UserProfile(age = 32, heightCm = 180, weightKg = 75f)
        val bmi = HealthCalculator.bmi(profile.weightKg, profile.heightCm)
        assertTrue(bmi in 23.0..24.0)
        val category = HealthCalculator.bmiCategory(bmi)
        assertEquals(HealthCalculator.BmiCategory.Normal, category)
    }

    @Test
    fun tdeeUsesActivityMultiplier() {
        val profile = UserProfile(
            age = 28,
            heightCm = 175,
            weightKg = 70f,
            activityLevel = ActivityLevel.ModeratelyActive
        )
        val sedentary = profile.copy(activityLevel = ActivityLevel.Sedentary)
        val activeTdee = HealthCalculator.tdee(profile)
        val sedentaryTdee = HealthCalculator.tdee(sedentary)
        assertTrue(activeTdee > sedentaryTdee)
    }

    @Test
    fun caloriesAreCalculatedPerUnit() {
        val item = FoodItem(id = 1, name = "Brown Rice", kcalPer100g = 111f, defaultUnit = "g")
        val calories = HealthCalculator.caloriesForFood(amount = 150f, unit = "g", item = item)
        assertEquals(167, calories)
        val spoonItem = FoodItem(id = 2, name = "Peanut Butter", kcalPer100g = 588f, defaultUnit = "tbsp")
        val spoonCalories = HealthCalculator.caloriesForFood(amount = 2f, unit = "tbsp", item = spoonItem)
        assertEquals(176, spoonCalories)
    }
}