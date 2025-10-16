package com.syla.healthfit.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.syla.healthfit.MainActivity
import com.syla.healthfit.R
import com.syla.healthfit.data.repository.DailyMetricsRepository
import com.syla.healthfit.data.repository.ProfileRepository
import com.syla.healthfit.data.repository.SettingsRepository
import com.syla.healthfit.domain.HealthCalculator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDate

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val profileRepository: ProfileRepository,
    private val metricsRepository: DailyMetricsRepository,
    private val clock: Clock
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        if (!settingsRepository.notificationsEnabled.first()) return Result.success()
        val profile = profileRepository.profileStream().first()
        val today = LocalDate.now(clock)
        val metrics = metricsRepository.metricsFor(today).first()
        val glassesGoal = if (profile.glassSizeMl == 0) 0 else profile.dailyWaterGoalMl / profile.glassSizeMl
        val remaining = (glassesGoal - metrics.waterGlasses).coerceAtLeast(0)
        if (remaining <= 0) return Result.success()
        ReminderNotificationHelper.ensureChannel(applicationContext)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            100,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, ReminderNotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Time to hydrate")
            .setContentText("${remaining} glasses left to hit today's water goal")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(201, notification)
        return Result.success()
    }
}

@HiltWorker
class CalorieReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val profileRepository: ProfileRepository,
    private val metricsRepository: DailyMetricsRepository,
    private val clock: Clock
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        if (!settingsRepository.notificationsEnabled.first()) return Result.success()
        val profile = profileRepository.profileStream().first()
        val today = LocalDate.now(clock)
        val metrics = metricsRepository.metricsFor(today).first()
        val remaining = (metrics.calorieTarget - metrics.caloriesConsumed).coerceAtLeast(0)
        val now = java.time.ZonedDateTime.now(clock)
        if (!HealthCalculator.shouldShowCalorieReminder(remaining, now.hour)) {
            return Result.success()
        }
        ReminderNotificationHelper.ensureChannel(applicationContext)
        val intent = PendingIntent.getActivity(
            applicationContext,
            101,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, ReminderNotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Fuel up")
            .setContentText("You still have about ${remaining} kcal remaining today")
            .setContentIntent(intent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(202, notification)
        return Result.success()
    }
}