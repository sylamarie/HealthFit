package com.syla.healthfit.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object ReminderNotificationHelper {
    const val CHANNEL_ID = "healthfit_reminders"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "HealthFit reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Gentle nudges to keep you hydrated and fuelled"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}