package com.syla.healthfit.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun RadialMetric(
    title: String,
    current: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val raw = if (goal <= 0) 0f else (current.toFloat() / goal).coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = raw,
        animationSpec = tween(durationMillis = 700, easing = EaseOutCubic),
        label = "radialProgress"
    )

    // ✅ Read from MaterialTheme in composable context, not inside Canvas draw scope
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary
    val titleStyle = MaterialTheme.typography.titleMedium
    val valueStyle = MaterialTheme.typography.titleLarge

    Column(modifier.padding(16.dp)) {
        Text(title, style = titleStyle)
        Spacer(Modifier.height(8.dp))
        Canvas(modifier = Modifier.size(180.dp)) {
            val stroke = 16.dp.toPx()
            val sizeOffset = stroke / 2
            val diameter = size.minDimension - stroke
            val topLeft = Offset(sizeOffset, sizeOffset)
            val arcSize = Size(diameter, diameter)

            // Track
            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress
            drawArc(
                color = progressColor,
                startAngle = 135f,
                sweepAngle = 270f * animated,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Text("$current / $goal", style = valueStyle)
    }
}