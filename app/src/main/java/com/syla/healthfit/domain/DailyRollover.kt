package com.syla.healthfit.domain

import java.time.LocalDate

object DailyRollover {
    fun shouldCreateNewMetrics(lastDate: LocalDate?, today: LocalDate): Boolean {
        if (lastDate == null) return true
        return lastDate.isBefore(today)
    }
}