package com.syla.healthfit.domain

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.time.LocalDate

class DailyRolloverTest {
    @Test
    fun createsMetricsWhenNoPreviousDate() {
        assertTrue(DailyRollover.shouldCreateNewMetrics(null, LocalDate.now()))
    }

    @Test
    fun doesNotCreateWhenSameDay() {
        val today = LocalDate.now()
        assertFalse(DailyRollover.shouldCreateNewMetrics(today, today))
    }

    @Test
    fun createsWhenDayHasChanged() {
        val yesterday = LocalDate.now().minusDays(1)
        assertTrue(DailyRollover.shouldCreateNewMetrics(yesterday, LocalDate.now()))
    }
}