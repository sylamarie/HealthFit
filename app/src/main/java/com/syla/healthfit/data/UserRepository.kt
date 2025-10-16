package com.syla.healthfit.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.syla.healthfit.model.ChecklistItem
import com.syla.healthfit.model.Sex
import com.syla.healthfit.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "healthfit_prefs")

private object Keys {
    val AGE = intPreferencesKey("age")
    val HEIGHT = intPreferencesKey("height_cm")
    val WEIGHT = floatPreferencesKey("weight_kg")
    val GOAL_WEIGHT = floatPreferencesKey("goal_weight_kg")
    val SEX = stringPreferencesKey("sex")
    val STEPS_BASELINE = intPreferencesKey("steps_baseline")
}

private fun dailyChecklistKey(dateKey: String, id: String) = "daily_${dateKey}_${id}_done"
private fun dailyStepsKey(dateKey: String) = "daily_${dateKey}_steps_count"

private fun Preferences.getBooleanSafely(name: String): Boolean =
    asMap().entries.firstOrNull { it.key.name == name }?.value as? Boolean ?: false

private fun Preferences.getIntSafely(name: String): Int =
    (asMap().entries.firstOrNull { it.key.name == name }?.value as? Int) ?: 0

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

    fun checklistFlow(dateKey: String): Flow<List<ChecklistItem>> =
        context.dataStore.data.map { p ->
            listOf("water", "steps", "workout").map { key ->
                val prefName = dailyChecklistKey(dateKey, key)
                val done = p.getBooleanSafely(prefName)
                ChecklistItem(id = key, title = key.replaceFirstChar { it.uppercase() }, done = done)
            }
        }

    suspend fun setChecklist(dateKey: String, id: String, done: Boolean) {
        context.dataStore.edit { p -> p[booleanPreferencesKey(dailyChecklistKey(dateKey, id))] = done }
    }

    fun stepsFlow(dateKey: String): Flow<Int> =
        context.dataStore.data.map { p ->
            val prefName = dailyStepsKey(dateKey)
            p.getIntSafely(prefName)
        }

    suspend fun setSteps(dateKey: String, steps: Int) {
        context.dataStore.edit { p -> p[intPreferencesKey(dailyStepsKey(dateKey))] = steps.coerceAtLeast(0) }
    }

    fun stepsBaselineFlow(): Flow<Int> = context.dataStore.data.map { it[Keys.STEPS_BASELINE] ?: 0 }
    suspend fun setStepsBaseline(value: Int) { context.dataStore.edit { it[Keys.STEPS_BASELINE] = value } }
}
