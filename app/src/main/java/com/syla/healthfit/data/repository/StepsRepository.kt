package com.syla.healthfit.data.repository

import com.syla.healthfit.data.preferences.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepsRepository @Inject constructor(
    private val settingsDataSource: SettingsDataSource
) {
    val baseline: Flow<Int> = settingsDataSource.stepBaseline
    val baselineDate: Flow<LocalDate?> = settingsDataSource.stepBaselineDate

    suspend fun setBaseline(total: Int, date: LocalDate) {
        settingsDataSource.setStepBaseline(total, date)
    }

    suspend fun clearBaseline() {
        settingsDataSource.clearBaseline()
    }
}