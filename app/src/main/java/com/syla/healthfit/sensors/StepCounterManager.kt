package com.syla.healthfit.sensors

import com.syla.healthfit.data.repository.DailyMetricsRepository
import com.syla.healthfit.data.repository.ProfileRepository
import com.syla.healthfit.data.repository.StepsRepository
import com.syla.healthfit.domain.StepNormalizer
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepCounterManager @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val stepsRepository: StepsRepository,
    private val metricsRepository: DailyMetricsRepository,
    private val clock: Clock
) {
    suspend fun onSensorTotal(totalSteps: Int) {
        val today = today()
        val profile = profileRepository.profileStream().first()
        metricsRepository.ensureTodayMetrics(profile, today)
        val baselineValue = stepsRepository.baseline.first()
        val baselineDate = stepsRepository.baselineDate.first()
        val result = StepNormalizer.normalize(
            totalFromSensor = totalSteps,
            storedBaseline = baselineValue.takeIf { it > 0 },
            baselineDate = baselineDate,
            today = today
        )
        metricsRepository.updateSteps(today, result.stepsToday)
        if (baselineDate == null || baselineDate.isBefore(today) || baselineValue != result.baselineToPersist) {
            stepsRepository.setBaseline(result.baselineToPersist, result.baselineDate)
            if (baselineDate == null || baselineDate.isBefore(today)) {
                metricsRepository.resetForNewDay(profile, today)
            }
        }
    }

    suspend fun adjustSteps(delta: Int) {
        metricsRepository.adjustSteps(today(), delta)
    }

    suspend fun clearBaseline() {
        stepsRepository.clearBaseline()
    }

    private fun today(): LocalDate = LocalDate.now(clock)
}