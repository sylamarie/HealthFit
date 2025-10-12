package com.syla.healthfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.syla.healthfit.UiState
import com.syla.healthfit.domain.GoalCalculator

@Composable
fun DashboardScreen(
    state: UiState,
    onGoProfile: () -> Unit,
    onGoChecklist: () -> Unit,
    onManualStepAdd: (Int) -> Unit
) {
    val pct = GoalCalculator.progress(state.stepsToday, state.goals.stepsTarget)
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Today's Recommendations", style = MaterialTheme.typography.headlineSmall)
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Calories: ${state.goals.caloriesKcal} kcal")
                Text("Water: ${state.goals.waterMl} ml (~${GoalCalculator.waterGlasses(state.goals.waterMl)} glasses)")
                Text("Steps target: ${state.goals.stepsTarget}")
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Steps: ${state.stepsToday} / ${state.goals.stepsTarget}")
                LinearProgressIndicator(progress = { pct }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onManualStepAdd(500) }) { Text("+500") }
                    OutlinedButton(onClick = { onManualStepAdd(1000) }) { Text("+1000") }
                }
                if (!state.hasValidProfile) {
                    AssistChip(onClick = onGoProfile, label = { Text("Complete profile for accurate goals") })
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onGoChecklist) { Text("Checklist") }
            OutlinedButton(onClick = onGoProfile) { Text("Profile") }
        }
    }
}