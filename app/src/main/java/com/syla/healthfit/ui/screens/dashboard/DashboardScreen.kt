package com.syla.healthfit.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.syla.healthfit.R
import com.syla.healthfit.model.NutritionSuggestion

@Composable
fun DashboardScreen(
    state: DashboardState,
    onNavigateSteps: () -> Unit,
    onNavigateWater: () -> Unit,
    onNavigateNutrition: () -> Unit,
    onNavigateProfile: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { SummaryRow(state) }
            item {
                Text(text = stringResource(id = R.string.dashboard_quick_actions), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onNavigateSteps, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(id = R.string.steps_card_title))
                    }
                    Button(onClick = onNavigateWater, modifier = Modifier.weight(1f)) {
                        Text(text = stringResource(id = R.string.water_card_title))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateNutrition, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.nutrition_card_title))
                }
            }
            if (state.suggestions.isNotEmpty()) {
                item {
                    Text(text = stringResource(id = R.string.nutrition_suggestions), style = MaterialTheme.typography.titleMedium)
                }
                items(state.suggestions) { suggestion -> SuggestionCard(suggestion) }
            }
            item {
                Button(onClick = onNavigateProfile, modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(id = R.string.nav_profile))
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(state: DashboardState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricCard(
            title = stringResource(id = R.string.steps_card_title),
            value = "${state.metrics.steps}",
            sub = stringResource(id = R.string.daily_goal) + ": ${state.profile.dailyStepGoal}",
            progress = if (state.profile.dailyStepGoal == 0) 0f else (state.metrics.steps.toFloat() / state.profile.dailyStepGoal).coerceIn(0f, 1f)
        )
        MetricCard(
            title = stringResource(id = R.string.water_card_title),
            value = "${state.metrics.waterMl} ml",
            sub = stringResource(
                id = R.string.water_progress,
                state.metrics.waterGlasses,
                if (state.profile.glassSizeMl == 0) 0 else state.profile.dailyWaterGoalMl / state.profile.glassSizeMl
            ),
            progress = if (state.profile.dailyWaterGoalMl == 0) 0f else (state.metrics.waterMl.toFloat() / state.profile.dailyWaterGoalMl).coerceIn(0f, 1f)
        )
        MetricCard(
            title = stringResource(id = R.string.nutrition_card_title),
            value = "${state.metrics.caloriesConsumed} kcal",
            sub = stringResource(id = R.string.remaining_calories) + ": ${state.metrics.caloriesRemaining} kcal",
            progress = if (state.metrics.calorieTarget == 0) 0f else (state.metrics.caloriesConsumed.toFloat() / state.metrics.calorieTarget).coerceIn(0f, 1f)
        )
    }
}

@Composable
private fun MetricCard(title: String, value: String, sub: String, progress: Float) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
            Text(text = sub)
        }
    }
}

@Composable
private fun SuggestionCard(suggestion: NutritionSuggestion) {
    Card {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = suggestion.title, style = MaterialTheme.typography.titleSmall)
            Text(text = suggestion.description, style = MaterialTheme.typography.bodySmall)
            Text(text = "${suggestion.calories} kcal", fontWeight = FontWeight.Medium)
        }
    }
}
