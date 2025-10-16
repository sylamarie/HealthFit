package com.syla.healthfit.ui.screens.steps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syla.healthfit.data.repository.DailyMetricsRepository
import com.syla.healthfit.data.repository.ProfileRepository
import com.syla.healthfit.model.DailyMetrics
import com.syla.healthfit.model.UserProfile
import com.syla.healthfit.sensors.StepCounterManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

data class StepsUiState(
    val profile: UserProfile = UserProfile(),
    val metrics: DailyMetrics = DailyMetrics(LocalDate.now(), 0, 0, 0, 0, 0)
) {
    val progress: Float
        get() = if (profile.dailyStepGoal <= 0) 0f else (metrics.steps.toFloat() / profile.dailyStepGoal).coerceIn(0f, 1f)
}

@HiltViewModel
class StepsViewModel @Inject constructor(
    profileRepository: ProfileRepository,
    metricsRepository: DailyMetricsRepository,
    private val stepCounterManager: StepCounterManager,
    private val clock: Clock
) : ViewModel() {
    private val today = LocalDate.now(clock)

    val uiState: StateFlow<StepsUiState> = combine(
        profileRepository.profileStream(),
        metricsRepository.metricsFor(today)
    ) { profile, metrics ->
        StepsUiState(profile = profile, metrics = metrics)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StepsUiState())

    fun adjustSteps(delta: Int) {
        viewModelScope.launch { stepCounterManager.adjustSteps(delta) }
    }

    fun setManualSteps(target: Int) {
        val current = uiState.value.metrics.steps
        adjustSteps(target - current)
    }

}