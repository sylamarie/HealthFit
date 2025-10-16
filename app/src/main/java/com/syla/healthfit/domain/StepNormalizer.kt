package com.syla.healthfit.domain

import java.time.LocalDate

data class StepNormalizationResult(
    val stepsToday: Int,
    val baselineToPersist: Int,
    val baselineDate: LocalDate
)

object StepNormalizer {
    fun normalize(
        totalFromSensor: Int,
        storedBaseline: Int?,
        baselineDate: LocalDate?,
        today: LocalDate
    ): StepNormalizationResult {
        if (storedBaseline == null || baselineDate == null || baselineDate.isBefore(today)) {
            return StepNormalizationResult(0, totalFromSensor, today)
        }
        val normalized = (totalFromSensor - storedBaseline).coerceAtLeast(0)
        return StepNormalizationResult(normalized, storedBaseline, baselineDate)
    }
}