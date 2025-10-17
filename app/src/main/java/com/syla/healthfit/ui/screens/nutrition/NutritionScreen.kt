package com.syla.healthfit.ui.screens.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.syla.healthfit.R
import com.syla.healthfit.model.FoodItem
import com.syla.healthfit.model.FoodLog
import com.syla.healthfit.model.NutritionSuggestion
import java.util.Locale

@Composable
fun NutritionScreen(
    state: NutritionUiState,
    onAmountChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onCustomCaloriesChange: (String) -> Unit,
    onSelectFood: (FoodItem) -> Unit,
    onSuggestionSelect: (NutritionSuggestion) -> Unit,
    onSave: () -> Unit,
    onEdit: (FoodLog) -> Unit,
    onDelete: (FoodLog) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                NutritionSummary(state)
            }
            item {
                NutritionForm(
                    state = state,
                    onAmountChange = onAmountChange,
                    onUnitChange = onUnitChange,
                    onQueryChange = onQueryChange,
                    onCustomCaloriesChange = onCustomCaloriesChange,
                    onSelectFood = onSelectFood,
                    onSave = onSave
                )
            }
            if (state.suggestionCards.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.nutrition_suggestions),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(state.suggestionCards) { suggestion ->
                    SuggestionCard(suggestion, onClick = { onSuggestionSelect(suggestion) })
                }
            }
            item {
                Text(
                    text = stringResource(id = R.string.nutrition_log_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(state.logs) { log ->
                FoodLogRow(log = log, onEdit = { onEdit(log) }, onDelete = { onDelete(log) })
            }
        }
    }
}

@Composable
private fun NutritionSummary(state: NutritionUiState) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = R.string.calories_consumed), style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${state.totalConsumed} kcal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(id = R.string.remaining_calories))
            Text(
                text = "${state.remainingCalories} kcal",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            if (state.remainingCalories > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.calories_banner_positive, state.remainingCalories),
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.calories_banner_negative),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NutritionForm(
    state: NutritionUiState,
    onAmountChange: (String) -> Unit,
    onUnitChange: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onCustomCaloriesChange: (String) -> Unit,
    onSelectFood: (FoodItem) -> Unit,
    onSave: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = state.form.amount,
                onValueChange = onAmountChange,
                modifier = Modifier.weight(1f),
                label = { Text(stringResource(id = R.string.food_amount)) },
                placeholder = { Text(stringResource(id = R.string.amount_placeholder)) }
            )
            AmountUnitPicker(units = state.units, selected = state.form.unit, onSelect = onUnitChange)
        }
        OutlinedTextField(
            value = state.form.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.food_name)) }
        )
        if (state.searchSuggestions.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.searchSuggestions.take(4).forEach { suggestion ->
                    FilterChip(
                        selected = suggestion.id == state.form.selectedFood?.id,
                        onClick = { onSelectFood(suggestion) },
                        label = { Text(text = suggestion.name, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    )
                }
            }
        }
        OutlinedTextField(
            value = state.form.customCalories,
            onValueChange = onCustomCaloriesChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(id = R.string.custom_calories)) }
        )
        Button(
            onClick = onSave,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = stringResource(id = R.string.add_food))
        }
    }
}

@Composable
private fun AmountUnitPicker(units: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        units.forEach { unit ->
            FilterChip(
                selected = unit == selected,
                onClick = { onSelect(unit) },
                label = { Text(unit) }
            )
        }
    }
}

@Composable
private fun FoodLogRow(log: FoodLog, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = log.customName ?: "Entry", style = MaterialTheme.typography.titleSmall)
            Text(text = "${log.amount} ${log.unit}")
            Text(text = "${log.computedKcal} kcal", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = onEdit) { Text(text = stringResource(id = R.string.edit)) }
                TextButton(onClick = onDelete) { Text(text = stringResource(id = R.string.delete)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuggestionCard(suggestion: NutritionSuggestion, onClick: () -> Unit) {
    Card(onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = suggestion.title, style = MaterialTheme.typography.titleSmall)
            Text(text = suggestion.description, style = MaterialTheme.typography.bodySmall)
            if (suggestion.portions.isNotEmpty()) {
                suggestion.portions.forEach { portion ->
                    Text(
                        text = "~${formatAmount(portion.amount)} ${portion.unit} ${portion.food.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(text = "${suggestion.calories} kcal", fontWeight = FontWeight.Medium)
        }
    }
}

private fun formatAmount(value: Float): String {
    return if (value % 1f == 0f) {
        value.toInt().toString()
    } else {
        String.format(Locale.getDefault(), "%.1f", value)
    }
}
