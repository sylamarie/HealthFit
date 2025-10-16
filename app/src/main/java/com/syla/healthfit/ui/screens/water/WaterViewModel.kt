package com.syla.healthfit.ui.screens.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syla.healthfit.data.repository.DailyMetricsRepository
import com.syla.healthfit.data.repository.ProfileRepository
import com.syla.healthfit.model.DailyMetrics
import com.syla.healthfit.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

data class WaterUiState(
    val profile: UserProfile = UserProfile(),
    val metrics: DailyMetrics = DailyMetrics(LocalDate.now(), 0, 0, 0, 0, 0)
) {
    val glassesGoal: Int
        get() = if (profile.glassSizeMl == 0) 0 else profile.dailyWaterGoalMl / profile.glassSizeMl

    val mlRemaining: Int
        get() = (profile.dailyWaterGoalMl - metrics.waterMl).coerceAtLeast(0)
}

@HiltViewModel
class WaterViewModel @Inject constructor(
    private val metricsRepository: DailyMetricsRepository,
    profileRepository: ProfileRepository,
    private val clock: Clock
) : ViewModel() {
    private val today = LocalDate.now(clock)

    val uiState: StateFlow<WaterUiState> = combine(
        profileRepository.profileStream(),
        metricsRepository.metricsFor(today)
    ) { profile, metrics ->
        WaterUiState(profile, metrics)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WaterUiState())

    fun toggleGlass(index: Int) {
        val state = uiState.value
        viewModelScope.launch {
            metricsRepository.toggleWaterGlass(today, index, state.profile.glassSizeMl)
        }
    }
}