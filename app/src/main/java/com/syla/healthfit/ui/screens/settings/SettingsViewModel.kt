package com.syla.healthfit.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syla.healthfit.data.repository.ProfileRepository
import com.syla.healthfit.data.repository.SettingsRepository
import com.syla.healthfit.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val theme: ThemeMode = ThemeMode.System,
    val notificationsEnabled: Boolean = true,
    val glassSize: Int = 250
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val profileFlow = profileRepository.profileStream()

    val state: StateFlow<SettingsUiState> = combine(
        settingsRepository.themeMode,
        settingsRepository.notificationsEnabled,
        profileFlow
    ) { theme, notifications, profile ->
        SettingsUiState(theme = theme, notificationsEnabled = notifications, glassSize = profile.glassSizeMl)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun updateTheme(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setTheme(mode) }
    }

    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setNotifications(enabled) }
    }

    fun updateGlassSize(size: Int) {
        viewModelScope.launch {
            val profile = profileFlow.first()
            profileRepository.save(profile.copy(glassSizeMl = size))
        }
    }
}