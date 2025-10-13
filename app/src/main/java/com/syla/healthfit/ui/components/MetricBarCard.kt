package com.syla.healthfit.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MetricBarCard(
    title: String,
    current: Int,
    goal: Int,
    buttonLabel: String? = null,
    onButton: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val target = if (goal <= 0) 0f else (current.toFloat() / goal).coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 600, easing = EaseOutCubic),
        label = "metricBar"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(progress = { animated }, modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "$current", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Goal: $goal", style = MaterialTheme.typography.labelLarge)
            }
            if (buttonLabel != null && onButton != null) {
                Spacer(Modifier.height(4.dp))
                FilledTonalButton(onClick = onButton) {
                    Text(buttonLabel)
                }
            }
        }
    }
}