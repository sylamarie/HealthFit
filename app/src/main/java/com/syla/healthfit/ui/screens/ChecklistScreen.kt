package com.syla.healthfit.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.syla.healthfit.UiState

@Composable
fun ChecklistScreen(
    state: UiState,
    onToggle: (String, Boolean) -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Daily Checklist", style = MaterialTheme.typography.headlineSmall)

        val titles = mapOf(
            "water" to "Drink ${state.goals.waterMl} ml water",
            "steps" to "Meet ${state.goals.stepsTarget} steps",
            "workout" to "Complete a workout"
        )

        state.checklist.forEach { item ->
            Card(Modifier.fillMaxWidth().clickable { onToggle(item.id, !item.done) }) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Checkbox(checked = item.done, onCheckedChange = { onToggle(item.id, it) })
                    Column {
                        Text(titles[item.id] ?: item.title)
                        Text(
                            text = when (item.id) {
                                "water" -> "Hydration supports recovery."
                                "steps" -> "Daily NEAT boosts burn."
                                "workout" -> "Strength/cardio improves fitness."
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}