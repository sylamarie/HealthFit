package com.syla.healthfit.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.syla.healthfit.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore("healthfit_settings")

private object Keys {
    val THEME = stringPreferencesKey("theme")
    val NOTIFICATIONS = booleanPreferencesKey("notifications")
    val STEP_BASELINE = intPreferencesKey("step_baseline")
    val STEP_BASELINE_DATE = stringPreferencesKey("step_baseline_date")
    val LAST_METRICS_DATE = stringPreferencesKey("last_metrics_date")
}

@Singleton
class SettingsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val theme: Flow<ThemeMode> = context.settingsDataStore.data.map {
        it[Keys.THEME]?.let { stored -> runCatching { ThemeMode.valueOf(stored) }.getOrNull() }
            ?: ThemeMode.System
    }

    val notificationsEnabled: Flow<Boolean> = context.settingsDataStore.data.map {
        it[Keys.NOTIFICATIONS] ?: true
    }

    val stepBaseline: Flow<Int> = context.settingsDataStore.data.map { it[Keys.STEP_BASELINE] ?: 0 }

    val stepBaselineDate: Flow<LocalDate?> = context.settingsDataStore.data.map {
        it[Keys.STEP_BASELINE_DATE]?.let(LocalDate::parse)
    }

    val lastMetricsDate: Flow<LocalDate?> = context.settingsDataStore.data.map {
        it[Keys.LAST_METRICS_DATE]?.let(LocalDate::parse)
    }

    suspend fun setTheme(mode: ThemeMode) {
        context.settingsDataStore.edit { it[Keys.THEME] = mode.name }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun setStepBaseline(totalSteps: Int, date: LocalDate) {
        context.settingsDataStore.edit {
            it[Keys.STEP_BASELINE] = totalSteps
            it[Keys.STEP_BASELINE_DATE] = date.toString()
        }
    }

    suspend fun clearBaseline() {
        context.settingsDataStore.edit {
            it.remove(Keys.STEP_BASELINE)
            it.remove(Keys.STEP_BASELINE_DATE)
        }
    }

    suspend fun setLastMetricsDate(date: LocalDate) {
        context.settingsDataStore.edit { it[Keys.LAST_METRICS_DATE] = date.toString() }
    }
}