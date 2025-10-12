package com.syla.healthfit.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.syla.healthfit.model.ChecklistItem
import com.syla.healthfit.model.Sex
import com.syla.healthfit.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore(name = "healthfit_prefs")

private object Keys {
    val AGE = intPreferencesKey("age")
    val HEIGHT = intPreferencesKey("height_cm")
    val WEIGHT = floatPreferencesKey("weight_kg")
    val GOAL_WEIGHT = floatPreferencesKey("goal_weight_kg")
    val SEX = stringPreferencesKey("sex")
    val STEPS_BASELINE = intPreferencesKey("steps_baseline")
}

class UserRepository(private val context: Context) {

    fun profileFlow(): Flow<UserProfile> = context.dataStore.data.map { p ->
        UserProfile(
            age = p[Keys.AGE] ?: 0,
            heightCm = p[Keys.HEIGHT] ?: 0,
            weightKg = p[Keys.WEIGHT] ?: 0f,
            goalWeightKg = p[Keys.GOAL_WEIGHT] ?: 0f,
            sex = runCatching { Sex.valueOf(p[Keys.SEX] ?: Sex.Other.name) }.getOrDefault(Sex.Other)
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { p ->
            p[Keys.AGE] = profile.age
            p[Keys.HEIGHT] = profile.heightCm
            p[Keys.WEIGHT] = profile.weightKg
            p[Keys.GOAL_WEIGHT] = profile.goalWeightKg
            p[Keys.SEX] = profile.sex.name
        }
    }

    fun checklistFlow(date: LocalDate): Flow<List<ChecklistItem>> =
        context.dataStore.data.map { p ->
            listOf("water", "steps", "workout").map { key ->
                val done = p[booleanPreferencesKey("${date}_$key")] ?: false
                ChecklistItem(id = key, title = key.replaceFirstChar { it.uppercase() }, done = done)
            }
        }

    suspend fun setChecklist(date: LocalDate, id: String, done: Boolean) {
        context.dataStore.edit { p -> p[booleanPreferencesKey("${date}_$id")] = done }
    }

    fun stepsFlow(date: LocalDate): Flow<Int> =
        context.dataStore.data.map { p -> p[intPreferencesKey("${date}_steps")] ?: 0 }

    suspend fun setSteps(date: LocalDate, steps: Int) {
        context.dataStore.edit { p -> p[intPreferencesKey("${date}_steps")] = steps.coerceAtLeast(0) }
    }

    fun stepsBaselineFlow(): Flow<Int> = context.dataStore.data.map { it[Keys.STEPS_BASELINE] ?: 0 }
    suspend fun setStepsBaseline(value: Int) { context.dataStore.edit { it[Keys.STEPS_BASELINE] = value } }
}