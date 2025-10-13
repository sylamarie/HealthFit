package com.syla.healthfit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.syla.healthfit.UiState
import com.syla.healthfit.domain.GoalCalculator
import com.syla.healthfit.ui.components.MetricBarCard
import com.syla.healthfit.ui.components.RadialMetric
import com.syla.healthfit.ui.components.SectionHeader
import com.syla.healthfit.ui.theme.Spacing
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: UiState,
    onGoProfile: () -> Unit = {},
    onGoChecklist: () -> Unit = {},
    onManualStepAdd: (Int) -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    val glasses = GoalCalculator.waterGlasses(state.goals.waterMl)
    val hydrationDone = state.checklist.firstOrNull { it.id == "water" }?.done == true
    val workoutDone = state.checklist.firstOrNull { it.id == "workout" }?.done == true
    val caloriesGoal = state.goals.caloriesKcal
    val caloriesProgress = if (caloriesGoal <= 0) 0 else if (workoutDone) caloriesGoal else (caloriesGoal * 0.75f).roundToInt()
    val waterProgress = if (hydrationDone) state.goals.waterMl else 0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                title = { Text("Today", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onGoProfile) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = "Edit profile"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            SectionHeader(
                title = "Calories",
                actionLabel = "View goals",
                onAction = onGoProfile
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                RadialMetric(
                    title = "Calories target",
                    current = caloriesProgress,
                    goal = caloriesGoal,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SectionHeader(title = "Activity")
            MetricBarCard(
                title = "Steps",
                current = state.stepsToday,
                goal = state.goals.stepsTarget,
                buttonLabel = "+500 steps",
                onButton = { onManualStepAdd(500) }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(onClick = { onManualStepAdd(1000) }) {
                    Text("+1000 steps")
                }
                AssistChip(
                    onClick = onGoChecklist,
                    label = { Text("Open checklist") }
                )
            }

            if (state.stepsToday == 0 && state.goals.stepsTarget > 0) {
                Text(
                    text = "Sync your device or add manual steps to start tracking progress.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SectionHeader(title = "Hydration")
            MetricBarCard(
                title = "Water (ml)",
                current = waterProgress,
                goal = state.goals.waterMl,
                buttonLabel = if (hydrationDone) null else "Mark complete",
                onButton = if (hydrationDone) null else onGoChecklist
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.LocalDrink,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Aim for ~$glasses glasses through the day.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionHeader(title = "Nutrition")
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Focus on balanced meals across breakfast, lunch, and dinner.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "Recommended intake: ${state.goals.caloriesKcal} kcal",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
                    AssistChip(
                        onClick = onGoChecklist,
                        label = { Text(if (workoutDone) "Workout logged" else "Log your workout") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}