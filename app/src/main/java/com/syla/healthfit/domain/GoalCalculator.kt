package com.syla.healthfit.domain

import com.syla.healthfit.model.DailyGoals
import com.syla.healthfit.model.UserProfile
import kotlin.math.roundToInt

object GoalCalculator {
    fun calculate(profile: UserProfile): DailyGoals {
        val base = (profile.weightKg * 30f).roundToInt()
        val delta = ((profile.goalWeightKg - profile.weightKg) * 10f).toInt().coerceIn(-300, 300)
        val calories = (base + delta).coerceAtLeast(1200)
        val water = (profile.weightKg * 35f).roundToInt().coerceAtLeast(1500)
        val steps = when (profile.age) {
            in 0..29 -> 10_000
            in 30..59 -> 9_000
            else -> 8_000
        }.coerceIn(5_000, 12_000)
        return DailyGoals(calories, water, steps)
    }

    fun waterGlasses(waterMl: Int): Int = (waterMl / 250f).roundToInt()

    fun progress(current: Int, target: Int): Float =
        if (target <= 0) 0f else (current.toFloat() / target).coerceIn(0f, 1f)
}