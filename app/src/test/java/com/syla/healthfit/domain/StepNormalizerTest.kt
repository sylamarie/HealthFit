package com.syla.healthfit.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import java.time.LocalDate

class StepNormalizerTest {
    @Test
    fun baselineResetsOnNewDay() {
        val yesterday = LocalDate.now().minusDays(1)
        val today = LocalDate.now()
        val result = StepNormalizer.normalize(totalFromSensor = 1000, storedBaseline = 500, baselineDate = yesterday, today = today)
        assertEquals(0, result.stepsToday)
        assertEquals(1000, result.baselineToPersist)
        assertEquals(today, result.baselineDate)
    }

    @Test
    fun stepsSubtractBaseline() {
        val today = LocalDate.now()
        val result = StepNormalizer.normalize(totalFromSensor = 2500, storedBaseline = 1000, baselineDate = today, today = today)
        assertEquals(1500, result.stepsToday)
        assertEquals(1000, result.baselineToPersist)
    }
}