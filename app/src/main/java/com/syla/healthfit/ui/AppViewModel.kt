package com.syla.healthfit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syla.healthfit.data.repository.DailyMetricsRepository
import com.syla.healthfit.data.repository.ProfileRepository
import com.syla.healthfit.data.repository.SettingsRepository
import com.syla.healthfit.domain.HealthCalculator
import com.syla.healthfit.model.DailyMetrics
import com.syla.healthfit.model.ThemeMode
import com.syla.healthfit.model.UserProfile
import com.syla.healthfit.workers.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val settingsRepository: SettingsRepository,
    private val metricsRepository: DailyMetricsRepository,
    private val reminderScheduler: ReminderScheduler,
    private val clock: Clock
) : ViewModel() {
    private val today = MutableStateFlow(LocalDate.now(clock))

    val profile: StateFlow<UserProfile> = profileRepository.profileStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile())

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.System)

    val todayMetrics: StateFlow<DailyMetrics> = today
        .flatMapLatest { date -> metricsRepository.metricsFor(date) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            DailyMetrics(LocalDate.now(clock), 0, 0, 0, 0, 0)
        )

    val calorieReminderVisible: StateFlow<Boolean> = combine(todayMetrics, profile) { metrics, _ ->
        val nowHour = ZonedDateTime.now(clock).hour
        HealthCalculator.shouldShowCalorieReminder(metrics.caloriesRemaining, nowHour)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        viewModelScope.launch {
            profile.collect { profile ->
                val todayDate = LocalDate.now(clock)
                metricsRepository.ensureTodayMetrics(profile, todayDate)
            }
        }
        viewModelScope.launch {
            while (true) {
                val now = LocalDate.now(clock)
                if (now != today.value) {
                    today.value = now
                }
                kotlinx.coroutines.delay(30 * 60 * 1000L)
            }
        }
        reminderScheduler.scheduleWaterReminders()
        reminderScheduler.scheduleCalorieReminder()
    }
}