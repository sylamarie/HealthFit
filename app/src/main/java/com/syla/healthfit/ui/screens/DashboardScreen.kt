package com.syla.healthfit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.syla.healthfit.UiState
import com.syla.healthfit.ui.theme.Spacing
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
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
    val hydrationDone = state.checklist.firstOrNull { it.id == "water" }?.done == true
    val workoutDone = state.checklist.firstOrNull { it.id == "workout" }?.done == true

    val stepsGoal = state.goals.stepsTarget.takeIf { it > 0 } ?: 8_000
    val waterGoal = state.goals.waterMl.takeIf { it > 0 } ?: 2_400
    val caloriesGoal = state.goals.caloriesKcal.takeIf { it > 0 } ?: 1_900

    val stepsProgress = (state.stepsToday.toFloat() / stepsGoal).coerceIn(0f, 1f)
    val hydrationProgress = if (hydrationDone) 1f else 0.35f
    val estimatedCalories = ((state.stepsToday * 0.045f) + if (workoutDone) 230 else 120).roundToInt()
    val caloriesProgress = (estimatedCalories.toFloat() / caloriesGoal).coerceIn(0f, 1f)

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            DashboardTopBar(
                onBack = onBack,
                onGoProfile = onGoProfile
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            DashboardHeader(
                name = greetingName(state),
                stepsToday = state.stepsToday,
                stepsGoal = stepsGoal,
                stepsProgress = stepsProgress,
                onManualStepAdd = onManualStepAdd
            )

            HighlightMetricsRow(
                metrics = listOf(
                    HighlightMetric(
                        title = "Calories",
                        value = "${estimatedCalories.formatNumber()} kcal",
                        caption = "Goal ${caloriesGoal.formatNumber()} kcal",
                        progress = caloriesProgress
                    ),
                    HighlightMetric(
                        title = "Active Minutes",
                        value = if (workoutDone) "48 min" else "32 min",
                        caption = "Keep moving for 60 min",
                        progress = if (workoutDone) 0.8f else 0.53f
                    ),
                    HighlightMetric(
                        title = "Recovery",
                        value = if (workoutDone) "Great" else "In Progress",
                        caption = if (workoutDone) "Muscles feeling fresh" else "Stretch after your run",
                        progress = if (workoutDone) 1f else 0.45f
                    )
                )
            )

            QuickActionsRow(
                hydrationComplete = hydrationDone,
                onManualStepAdd = onManualStepAdd,
                onGoChecklist = onGoChecklist
            )

            TodayWorkoutCard(plan = remember(workoutDone) { sampleTodayWorkout(workoutDone) })

            HydrationCard(
                waterGoal = waterGoal,
                hydrationProgress = hydrationProgress,
                hydrationDone = hydrationDone,
                onGoChecklist = onGoChecklist
            )

            GoalsSection(
                goals = remember(stepsGoal, workoutDone) { sampleGoals(stepsGoal, workoutDone) }
            )

            ActivityTimeline(events = remember(workoutDone) { sampleTimeline(workoutDone) })

            Spacer(modifier = Modifier.height(Spacing.lg))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    onBack: (() -> Unit)?,
    onGoProfile: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "HealthFit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Stronger every day",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = Spacing.md)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onGoProfile) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "Profile"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun DashboardHeader(
    name: String,
    stepsToday: Int,
    stepsGoal: Int,
    stepsProgress: Float,
    onManualStepAdd: (Int) -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()) }
    val todayText = remember { LocalDate.now().format(formatter) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .padding(horizontal = Spacing.lg, vertical = Spacing.xl)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Text(
                    text = "Hi, $name",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = todayText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text(
                        text = "${stepsToday.formatNumber()} steps",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Goal ${stepsGoal.formatNumber()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                    )
                    LinearProgressIndicator(
                        progress = { stepsProgress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    )
                }

                AssistChip(
                    onClick = { onManualStepAdd(500) },
                    label = { Text("Log 500 more steps") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        labelColor = MaterialTheme.colorScheme.primary,
                        leadingIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
private fun HighlightMetricsRow(metrics: List<HighlightMetric>) {
    if (metrics.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Your vitals")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            metrics.forEach { metric ->
                MetricCard(metric = metric)
            }
        }
    }
}

@Composable
private fun RowScope.MetricCard(metric: HighlightMetric) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                text = metric.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = metric.value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = metric.caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            LinearProgressIndicator(
                progress = { metric.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    hydrationComplete: Boolean,
    onManualStepAdd: (Int) -> Unit,
    onGoChecklist: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Quick actions")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            QuickActionButton(
                icon = Icons.Rounded.Add,
                title = "Add 500 steps",
                subtitle = "Stay ahead",
                onClick = { onManualStepAdd(500) }
            )
            QuickActionButton(
                icon = Icons.Rounded.CheckCircle,
                title = "Daily checklist",
                subtitle = "Update progress",
                onClick = onGoChecklist
            )
            QuickActionButton(
                icon = Icons.Rounded.LocalDrink,
                title = if (hydrationComplete) "Water logged" else "Log water",
                subtitle = if (hydrationComplete) "All done" else "Tap to record",
                onClick = onGoChecklist
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.QuickActionButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TodayWorkoutCard(plan: TodayWorkoutPlan) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Today's workout")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        Text(
                            text = plan.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = plan.focus,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = plan.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    GoalPill(text = plan.duration)
                    GoalPill(text = plan.equipment)
                }
            }
        }
    }
}

@Composable
private fun HydrationCard(
    waterGoal: Int,
    hydrationProgress: Float,
    hydrationDone: Boolean,
    onGoChecklist: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Hydration")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        Text(
                            text = "Goal ${waterGoal.formatNumber()} ml",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (hydrationDone) "You're fully hydrated" else "Log your water to stay on track",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    AssistChip(
                        onClick = onGoChecklist,
                        label = { Text(if (hydrationDone) "Completed" else "Log now") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.LocalDrink,
                                contentDescription = null
                            )
                        }
                    )
                }
                LinearProgressIndicator(
                    progress = { hydrationProgress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = if (hydrationDone) "You smashed today's hydration goal!" else "Aim for ${waterGoal / 4} ml every few hours.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GoalsSection(goals: List<GoalProgress>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Goals")
        goals.forEach { goal ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            Text(
                                text = goal.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = goal.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Rounded.Flag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { goal.progress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = goal.target,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityTimeline(events: List<TimelineEvent>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(title = "Activity timeline")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                events.forEachIndexed { index, event ->
                    TimelineItem(
                        event = event,
                        drawLine = index != events.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(event: TimelineEvent, drawLine: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            if (drawLine) {
                Spacer(
                    modifier = Modifier
                        .padding(top = Spacing.sm)
                        .width(2.dp)
                        .height(36.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = event.time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    if (drawLine) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.md),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun GoalPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

data class HighlightMetric(
    val title: String,
    val value: String,
    val caption: String,
    val progress: Float
)

data class TodayWorkoutPlan(
    val title: String,
    val focus: String,
    val duration: String,
    val equipment: String,
    val summary: String
)

data class GoalProgress(
    val title: String,
    val subtitle: String,
    val progress: Float,
    val target: String
)

data class TimelineEvent(
    val time: String,
    val title: String,
    val description: String
)

private fun sampleTodayWorkout(completed: Boolean): TodayWorkoutPlan =
    if (completed) {
        TodayWorkoutPlan(
            title = "Mobility Reset",
            focus = "Recovery",
            duration = "24 min",
            equipment = "Mat only",
            summary = "Nice work getting your workout done. Finish the day with a mobility flow to recover faster."
        )
    } else {
        TodayWorkoutPlan(
            title = "Total body burn",
            focus = "Strength & cardio",
            duration = "36 min",
            equipment = "Dumbbells",
            summary = "Three rounds of compound strength paired with tempo runs. Warm up for 5 min before you begin."
        )
    }

private fun sampleGoals(stepsGoal: Int, workoutDone: Boolean): List<GoalProgress> = listOf(
    GoalProgress(
        title = "Daily steps",
        subtitle = "${(stepsGoal * 0.8f).roundToInt().formatNumber()} completed",
        progress = 0.8f,
        target = "Target ${stepsGoal.formatNumber()} steps"
    ),
    GoalProgress(
        title = "Weekly workouts",
        subtitle = if (workoutDone) "3 of 4 sessions" else "2 of 4 sessions",
        progress = if (workoutDone) 0.75f else 0.5f,
        target = "Stay consistent all week"
    ),
    GoalProgress(
        title = "Mindful minutes",
        subtitle = "12 of 20 minutes",
        progress = 0.6f,
        target = "Take a breathing break tonight"
    )
)

private fun sampleTimeline(workoutDone: Boolean): List<TimelineEvent> = listOf(
    TimelineEvent(
        time = "06:45",
        title = "Wake & stretch",
        description = "Focused breathing and dynamic warm-up to start the day."
    ),
    TimelineEvent(
        time = "12:30",
        title = if (workoutDone) "Midday strength" else "Workout scheduled",
        description = if (workoutDone) {
            "Strength and conditioning circuit logged for 36 min."
        } else {
            "Reminder set to tackle your strength session after lunch."
        }
    ),
    TimelineEvent(
        time = "20:15",
        title = "Wind-down",
        description = "Plan tomorrow's meals and prep your gear for the morning run."
    )
)

private fun Int.formatNumber(): String = NumberFormat.getNumberInstance().format(this)

private fun greetingName(state: UiState): String {
    val weight = state.profile.weightKg
    return if (weight > 0f) "athlete" else "there"
}