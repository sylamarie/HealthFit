package com.syla.healthfit.data.repository

import com.syla.healthfit.data.local.dao.DailyMetricsDao
import com.syla.healthfit.data.local.entity.DailyMetricsEntity
import com.syla.healthfit.data.preferences.SettingsDataSource
import com.syla.healthfit.model.DailyMetrics
import com.syla.healthfit.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyMetricsRepository @Inject constructor(
    private val dailyMetricsDao: DailyMetricsDao,
    private val settings: SettingsDataSource,
    private val clock: Clock
) {
    fun metricsFor(date: LocalDate): Flow<DailyMetrics> =
        dailyMetricsDao.observeForDate(date).map { entity ->
            entity?.toDomain() ?: DailyMetrics(
                date = date,
                steps = 0,
                waterGlasses = 0,
                waterMl = 0,
                caloriesConsumed = 0,
                calorieTarget = 0
            )
        }

    suspend fun ensureTodayMetrics(profile: UserProfile, date: LocalDate = today()): DailyMetricsEntity {
        val lastDate = settings.lastMetricsDate.first()
        if (lastDate == null || lastDate.isBefore(date)) {
            val entity = DailyMetricsEntity(
                date = date,
                steps = 0,
                waterGlasses = 0,
                waterMl = 0,
                caloriesConsumed = 0,
                calorieTarget = profile.dailyCalorieTarget
            )
            dailyMetricsDao.upsert(entity)
            settings.setLastMetricsDate(date)
        }
        val existing = dailyMetricsDao.getForDate(date)
        if (existing == null) {
            val entity = DailyMetricsEntity(
                date = date,
                steps = 0,
                waterGlasses = 0,
                waterMl = 0,
                caloriesConsumed = 0,
                calorieTarget = profile.dailyCalorieTarget
            )
            dailyMetricsDao.upsert(entity)
            settings.setLastMetricsDate(date)
            return entity
        }
        if (existing.calorieTarget != profile.dailyCalorieTarget) {
            dailyMetricsDao.upsert(existing.copy(calorieTarget = profile.dailyCalorieTarget))
        }
        return dailyMetricsDao.getForDate(date) ?: existing
    }

    suspend fun updateSteps(date: LocalDate, steps: Int) {
        dailyMetricsDao.updateSteps(date, steps.coerceAtLeast(0))
    }

    suspend fun adjustSteps(date: LocalDate, delta: Int) {
        val current = dailyMetricsDao.getForDate(date) ?: return
        val next = (current.steps + delta).coerceAtLeast(0)
        dailyMetricsDao.updateSteps(date, next)
    }

    suspend fun updateWater(date: LocalDate, glasses: Int, glassSizeMl: Int) {
        val sanitized = glasses.coerceAtLeast(0)
        val ml = sanitized * glassSizeMl
        dailyMetricsDao.updateWater(date, sanitized, ml)
    }

    suspend fun toggleWaterGlass(date: LocalDate, index: Int, glassSizeMl: Int) {
        val entity = dailyMetricsDao.getForDate(date) ?: return
        val target = if (index < entity.waterGlasses) index else index + 1
        val next = target.coerceAtLeast(0)
        updateWater(date, next, glassSizeMl)
    }

    suspend fun updateCalories(date: LocalDate, caloriesConsumed: Int) {
        dailyMetricsDao.updateCalories(date, caloriesConsumed.coerceAtLeast(0))
    }

    suspend fun resetForNewDay(profile: UserProfile, date: LocalDate = today()) {
        val entity = DailyMetricsEntity(
            date = date,
            steps = 0,
            waterGlasses = 0,
            waterMl = 0,
            caloriesConsumed = 0,
            calorieTarget = profile.dailyCalorieTarget
        )
        dailyMetricsDao.upsert(entity)
        settings.setLastMetricsDate(date)
    }

    private fun today(): LocalDate = LocalDate.now(clock)
}

private fun DailyMetricsEntity.toDomain(): DailyMetrics =
    DailyMetrics(
        date = date,
        steps = steps,
        waterGlasses = waterGlasses,
        waterMl = waterMl,
        caloriesConsumed = caloriesConsumed,
        calorieTarget = calorieTarget
    )