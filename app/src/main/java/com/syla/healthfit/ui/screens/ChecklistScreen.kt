package com.syla.healthfit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.syla.healthfit.UiState
import com.syla.healthfit.ui.components.SectionHeader
import com.syla.healthfit.ui.components.SwipeChecklistRow
import com.syla.healthfit.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    state: UiState,
    onToggle: (String, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val completed = state.checklist.count { it.done }
    val total = state.checklist.size
    val progress = if (total == 0) 0f else completed / total.toFloat()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { Text("Checklist", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { innerPadding ->
        if (total == 0) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Spacing.md, vertical = Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Text(
                    text = "No tasks yet. Complete your profile to generate new routines.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                item {
                    SectionHeader(
                        title = "Progress",
                        actionLabel = "Reset",
                        onAction = { state.checklist.forEach { onToggle(it.id, false) } }
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = Spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "$completed of $total tasks complete",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                items(state.checklist, key = { it.id }) { item ->
                    SwipeChecklistRow(
                        title = when (item.id) {
                            "water" -> "Drink ${state.goals.waterMl} ml water"
                            "steps" -> "Meet ${state.goals.stepsTarget} steps"
                            else -> item.title
                        },
                        checked = item.done,
                        onToggle = { onToggle(item.id, !item.done) },
                        onDelete = { onToggle(item.id, false) }
                    )
                }
            }
        }
    }
}