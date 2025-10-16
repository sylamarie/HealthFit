package com.syla.healthfit.ui.screens.steps

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.syla.healthfit.R

@Composable
fun StepsScreen(
    state: StepsUiState,
    permissionGranted: Boolean,
    onIncrement: (Int) -> Unit,
    onManualSet: (Int) -> Unit,
    onRequestPermission: () -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }
    val manualInput = remember(state.metrics.steps) { mutableStateOf(state.metrics.steps.toString()) }
    val scrollState = rememberScrollState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.steps_today),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${state.metrics.steps}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = state.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.daily_goal) + ": ${state.profile.dailyStepGoal}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { onIncrement(100) }) { Text(text = "+100") }
                Button(onClick = { onIncrement(-100) }) { Text(text = "-100") }
                OutlinedButton(onClick = { showDialog.value = true }) {
                    Text(text = stringResource(R.string.manual_adjust_steps))
                }
            }

            if (!permissionGranted) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.step_permission_denied),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRequestPermission) {
                            Text(text = stringResource(R.string.try_again))
                        }
                    }
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.manual_adjustment)) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(R.string.adjust_steps_hint))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            val value = manualInput.value.toIntOrNull() ?: state.metrics.steps
                            manualInput.value = (value - 10).coerceAtLeast(0).toString()
                        }) {
                            Text(text = "-")
                        }
                        TextField(
                            value = manualInput.value,
                            onValueChange = { manualInput.value = it.filter { ch -> ch.isDigit() } },
                            singleLine = true,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .weight(1f),
                            textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center)
                        )
                        IconButton(onClick = {
                            val value = manualInput.value.toIntOrNull() ?: state.metrics.steps
                            manualInput.value = (value + 10).toString()
                        }) {
                            Text(text = "+")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    manualInput.value.toIntOrNull()?.let(onManualSet)
                    showDialog.value = false
                }) {
                    Text(text = stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}