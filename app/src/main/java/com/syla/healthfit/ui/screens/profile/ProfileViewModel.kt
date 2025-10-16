package com.syla.healthfit.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syla.healthfit.data.repository.ProfileRepository
import com.syla.healthfit.domain.HealthCalculator
import com.syla.healthfit.model.ActivityLevel
import com.syla.healthfit.model.Sex
import com.syla.healthfit.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ProfileForm(
    val age: String = "",
    val height: String = "",
    val weight: String = "",
    val goalWeight: String = "",
    val sex: Sex = Sex.Other,
    val activityLevel: ActivityLevel = ActivityLevel.Sedentary,
    val glassSize: String = "250",
    val stepGoal: String = "8000",
    val waterGoal: String = "2000",
    val calorieTarget: String = "2000"
)

data class ProfileUiState(
    val form: ProfileForm = ProfileForm(),
    val bmi: Double = 0.0,
    val bmiCategory: HealthCalculator.BmiCategory = HealthCalculator.BmiCategory.Normal,
    val tdee: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val formState = MutableStateFlow(ProfileForm())
    private val profileFlow = profileRepository.profileStream()

    val uiState: StateFlow<ProfileUiState> = combine(profileFlow, formState) { profile, form ->
        val resolved = if (form.age.isEmpty() && profile.age > 0) formFromProfile(profile) else form
        val workingProfile = profileFromForm(resolved)
        val bmi = HealthCalculator.bmi(workingProfile.weightKg, workingProfile.heightCm)
        val category = HealthCalculator.bmiCategory(bmi)
        val tdee = HealthCalculator.tdee(workingProfile)
        ProfileUiState(form = resolved, bmi = bmi, bmiCategory = category, tdee = tdee)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    init {
        viewModelScope.launch {
            profileFlow.collect { profile ->
                formState.value = formFromProfile(profile)
            }
        }
    }

    fun updateForm(update: (ProfileForm) -> ProfileForm) {
        formState.value = update(formState.value)
    }

    fun setForm(form: ProfileForm) {
        formState.value = form
    }

    fun save() {
        val profile = profileFromForm(formState.value)
        viewModelScope.launch {
            profileRepository.save(profile.copy(lastUpdated = LocalDate.now()))
        }
    }

    private fun formFromProfile(profile: UserProfile): ProfileForm =
        ProfileForm(
            age = profile.age.takeIf { it > 0 }?.toString() ?: "",
            height = profile.heightCm.takeIf { it > 0 }?.toString() ?: "",
            weight = profile.weightKg.takeIf { it > 0f }?.toString() ?: "",
            goalWeight = profile.goalWeightKg.takeIf { it > 0f }?.toString() ?: "",
            sex = profile.sex,
            activityLevel = profile.activityLevel,
            glassSize = profile.glassSizeMl.toString(),
            stepGoal = profile.dailyStepGoal.toString(),
            waterGoal = profile.dailyWaterGoalMl.toString(),
            calorieTarget = profile.dailyCalorieTarget.toString()
        )

    private fun profileFromForm(form: ProfileForm): UserProfile {
        val weight = form.weight.toFloatOrNull() ?: 0f
        val height = form.height.toIntOrNull() ?: 0
        val profile = UserProfile(
            age = form.age.toIntOrNull() ?: 0,
            heightCm = height,
            weightKg = weight,
            goalWeightKg = form.goalWeight.toFloatOrNull() ?: 0f,
            sex = form.sex,
            activityLevel = form.activityLevel,
            glassSizeMl = form.glassSize.toIntOrNull() ?: 250,
            dailyStepGoal = form.stepGoal.toIntOrNull() ?: 8000,
            dailyWaterGoalMl = form.waterGoal.toIntOrNull() ?: 2000,
            dailyCalorieTarget = form.calorieTarget.toIntOrNull() ?: 2000
        )
        val calculatedTarget = HealthCalculator.calorieTarget(profile)
        return profile.copy(dailyCalorieTarget = if (calculatedTarget > 0) calculatedTarget else profile.dailyCalorieTarget)
    }
}