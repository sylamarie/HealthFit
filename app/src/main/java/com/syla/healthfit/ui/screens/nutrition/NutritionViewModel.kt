package com.syla.healthfit.ui.screens.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syla.healthfit.data.repository.DailyMetricsRepository
import com.syla.healthfit.data.repository.FoodRepository
import com.syla.healthfit.data.repository.ProfileRepository
import com.syla.healthfit.domain.HealthCalculator
import com.syla.healthfit.model.DailyMetrics
import com.syla.healthfit.model.FoodItem
import com.syla.healthfit.model.FoodLog
import com.syla.healthfit.model.NutritionSuggestion
import com.syla.healthfit.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.util.Locale
import javax.inject.Inject

private val defaultUnits = listOf("g", "ml", "piece", "slice", "tbsp", "cup")

data class NutritionFormState(
    val amount: String = "",
    val unit: String = defaultUnits.first(),
    val query: String = "",
    val customCalories: String = "",
    val selectedFood: FoodItem? = null,
    val editingLog: FoodLog? = null
)

data class NutritionUiState(
    val profile: UserProfile = UserProfile(),
    val metrics: DailyMetrics = DailyMetrics(LocalDate.now(), 0, 0, 0, 0, 0),
    val logs: List<FoodLog> = emptyList(),
    val pantry: List<FoodItem> = emptyList(),
    val form: NutritionFormState = NutritionFormState(),
    val searchSuggestions: List<FoodItem> = emptyList(),
    val suggestionCards: List<NutritionSuggestion> = emptyList()
) {
    val remainingCalories: Int get() = metrics.caloriesRemaining
    val totalConsumed: Int get() = metrics.caloriesConsumed
    val units: List<String> get() = defaultUnits
}

private data class NutritionSnapshot(
    val profile: UserProfile,
    val metrics: DailyMetrics,
    val logs: List<FoodLog>,
    val pantry: List<FoodItem>,
    val form: NutritionFormState
)

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val metricsRepository: DailyMetricsRepository,
    profileRepository: ProfileRepository,
    private val clock: Clock
) : ViewModel() {
    private val today = MutableStateFlow(LocalDate.now(clock))
    private val formState = MutableStateFlow(NutritionFormState())
    private val searchResults = MutableStateFlow<List<FoodItem>>(emptyList())

    private val logsFlow = today.flatMapLatest { date -> foodRepository.logsFor(date) }
    private val metricsFlow = today.flatMapLatest { date -> metricsRepository.metricsFor(date) }
    private val pantryFlow = foodRepository.foodItems()

    private val snapshotFlow = combine(
        profileRepository.profileStream(),
        metricsFlow,
        logsFlow,
        pantryFlow,
        formState
    ) { profile, metrics, logs, pantry, form ->
        NutritionSnapshot(
            profile = profile,
            metrics = metrics,
            logs = logs,
            pantry = pantry,
            form = form
        )
    }

    val uiState: StateFlow<NutritionUiState> = combine(snapshotFlow, searchResults) { snapshot, results ->
        val cards = HealthCalculator.calorieSuggestions(snapshot.metrics.caloriesRemaining, snapshot.profile, snapshot.pantry)
        NutritionUiState(
            profile = snapshot.profile,
            metrics = snapshot.metrics,
            logs = snapshot.logs,
            pantry = snapshot.pantry,
            form = snapshot.form,
            searchSuggestions = results,
            suggestionCards = cards
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NutritionUiState())

    init {
        viewModelScope.launch {
            formState
                .map { it.query }
                .debounce(250)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.length >= 2) {
                        searchResults.value = foodRepository.search(query)
                    } else {
                        searchResults.value = emptyList()
                    }
                }
        }
    }

    fun updateAmount(value: String) {
        formState.value = formState.value.copy(amount = value.filter { it.isDigit() || it == '.' })
    }

    fun updateUnit(unit: String) {
        formState.value = formState.value.copy(unit = unit)
    }

    fun updateQuery(query: String) {
        formState.value = formState.value.copy(query = query, selectedFood = null)
    }

    fun updateCustomCalories(value: String) {
        formState.value = formState.value.copy(customCalories = value.filter { it.isDigit() || it == '.' })
    }

    fun selectFood(item: FoodItem) {
        formState.value = formState.value.copy(
            query = item.name,
            selectedFood = item,
            customCalories = item.kcalPer100g.toString()
        )
    }

    fun applySuggestion(suggestion: NutritionSuggestion) {
        val portion = suggestion.portions.firstOrNull() ?: return
        val amount = portion.amount
        val amountText = if (amount % 1f == 0f) {
            amount.toInt().toString()
        } else {
            String.format(Locale.getDefault(), "%.1f", amount)
        }
        formState.value = NutritionFormState(
            amount = amountText,
            unit = portion.unit,
            query = portion.food.name,
            customCalories = kotlin.math.abs(suggestion.calories).toString(),
            selectedFood = portion.food
        )
    }

    fun editLog(log: FoodLog) {
        viewModelScope.launch {
            val food = log.foodItemId?.let { foodRepository.findById(it) }
            formState.value = NutritionFormState(
                amount = log.amount.toString(),
                unit = log.unit,
                query = food?.name ?: log.customName.orEmpty(),
                customCalories = food?.kcalPer100g?.toString() ?: log.computedKcal.toString(),
                selectedFood = food,
                editingLog = log
            )
        }
    }

    fun clearForm() {
        formState.value = NutritionFormState()
    }

    fun saveEntry() {
        val form = formState.value
        val amount = form.amount.toFloatOrNull() ?: return
        val unit = form.unit
        val food = form.selectedFood ?: run {
            val calories = form.customCalories.toFloatOrNull() ?: return
            FoodItem(id = 0, name = form.query.ifBlank { "Custom" }, kcalPer100g = calories, defaultUnit = unit)
        }
        val log = form.editingLog ?: FoodLog(0, today.value, foodItemId = null, customName = null, amount = amount, unit = unit, computedKcal = 0)
        val computedKcal = HealthCalculator.caloriesForFood(amount, unit, food)
        val entry = log.copy(
            date = today.value,
            foodItemId = if (form.selectedFood != null && form.selectedFood.id != 0L) form.selectedFood.id else null,
            customName = if (form.selectedFood == null) form.query else form.selectedFood.name,
            amount = amount,
            unit = unit,
            computedKcal = computedKcal
        )
        viewModelScope.launch {
            foodRepository.insertLog(entry)
            syncCalories()
            formState.value = NutritionFormState(unit = form.unit)
            searchResults.value = emptyList()
        }
    }

    fun deleteLog(log: FoodLog) {
        viewModelScope.launch {
            foodRepository.deleteLog(log.id)
            syncCalories()
            if (formState.value.editingLog?.id == log.id) {
                formState.value = NutritionFormState()
            }
        }
    }

    private suspend fun syncCalories() {
        val total = foodRepository.totalCaloriesFor(today.value)
        metricsRepository.updateCalories(today.value, total)
    }
}
