package com.syla.healthfit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.syla.healthfit.data.UserRepository
import com.syla.healthfit.domain.GoalCalculator
import com.syla.healthfit.model.ChecklistItem
import com.syla.healthfit.model.DailyGoals
import com.syla.healthfit.model.UserProfile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class UiState(
    val profile: UserProfile = UserProfile(),
    val goals: DailyGoals = DailyGoals(0, 0, 0),
    val stepsToday: Int = 0,
    val checklist: List<ChecklistItem> = emptyList(),
    val hasValidProfile: Boolean = false
)

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = UserRepository(app)
    private val today = LocalDate.now()

    private val profile = repo.profileFlow()
    private val goals = profile.map { GoalCalculator.calculate(it) }
    private val steps = repo.stepsFlow(today)
    private val checklist = repo.checklistFlow(today)

    val ui: StateFlow<UiState> = combine(profile, goals, steps, checklist) { p, g, s, c ->
        UiState(
            profile = p,
            goals = g,
            stepsToday = s,
            checklist = c,
            hasValidProfile = p.age > 0 && p.heightCm > 0 && p.weightKg > 0f && p.goalWeightKg > 0f
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun saveProfile(p: UserProfile) = viewModelScope.launch { repo.saveProfile(p) }
    fun toggleChecklist(id: String, done: Boolean) = viewModelScope.launch { repo.setChecklist(today, id, done) }
    fun setSteps(steps: Int) = viewModelScope.launch { repo.setSteps(today, steps) }

    fun applyStepSensor(totalSinceBoot: Int) {
        viewModelScope.launch {
            val base = repo.stepsBaselineFlow().first()
            if (base == 0) repo.setStepsBaseline(totalSinceBoot)
            val baseline = if (base == 0) totalSinceBoot else base
            val todaySteps = (totalSinceBoot - baseline).coerceAtLeast(0)
            repo.setSteps(today, todaySteps)
        }
    }
}