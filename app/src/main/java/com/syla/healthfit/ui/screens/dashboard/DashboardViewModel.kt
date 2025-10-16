package com.syla.healthfit.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syla.healthfit.data.repository.DailyMetricsRepository
import com.syla.healthfit.data.repository.FoodRepository
import com.syla.healthfit.data.repository.ProfileRepository
import com.syla.healthfit.domain.HealthCalculator
import com.syla.healthfit.model.DailyMetrics
import com.syla.healthfit.model.NutritionSuggestion
import com.syla.healthfit.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

data class DashboardState(
    val profile: UserProfile = UserProfile(),
    val metrics: DailyMetrics = DailyMetrics(LocalDate.now(), 0, 0, 0, 0, 0),
    val suggestions: List<NutritionSuggestion> = emptyList()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    profileRepository: ProfileRepository,
    metricsRepository: DailyMetricsRepository,
    foodRepository: FoodRepository,
    private val clock: Clock
) : ViewModel() {
    private val today = LocalDate.now(clock)

    val state: StateFlow<DashboardState> = combine(
        profileRepository.profileStream(),
        metricsRepository.metricsFor(today),
        foodRepository.foodItems()
    ) { profile, metrics, pantry ->
        val suggestions = HealthCalculator.calorieSuggestions(metrics.caloriesRemaining, profile, pantry)
        DashboardState(profile = profile, metrics = metrics, suggestions = suggestions.take(3))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState())
}