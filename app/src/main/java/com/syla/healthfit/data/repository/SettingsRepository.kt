package com.syla.healthfit.data.repository

import com.syla.healthfit.data.preferences.SettingsDataSource
import com.syla.healthfit.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDataSource: SettingsDataSource
) {
    val themeMode: Flow<ThemeMode> = settingsDataSource.theme
    val notificationsEnabled: Flow<Boolean> = settingsDataSource.notificationsEnabled

    suspend fun setTheme(mode: ThemeMode) = settingsDataSource.setTheme(mode)
    suspend fun setNotifications(enabled: Boolean) = settingsDataSource.setNotifications(enabled)
}