package com.syla.healthfit.workers

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val WATER_WORK_NAME = "water_reminder_work"
private const val CALORIE_WORK_NAME = "calorie_reminder_work"

@Singleton
class ReminderScheduler @Inject constructor(
    private val workManager: WorkManager
) {
    fun scheduleWaterReminders() {
        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(2, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WATER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleCalorieReminder() {
        val request = PeriodicWorkRequestBuilder<CalorieReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(19, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            CALORIE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}