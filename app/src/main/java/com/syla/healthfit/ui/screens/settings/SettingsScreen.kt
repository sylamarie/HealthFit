package com.syla.healthfit.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.syla.healthfit.R
import com.syla.healthfit.model.ThemeMode

@Composable
fun SettingsScreen(state: SettingsUiState, onThemeChange: (ThemeMode) -> Unit, onNotifications: (Boolean) -> Unit, onGlassSizeChange: (Int) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(id = R.string.theme), style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeMode.values().forEach { mode ->
                            FilterChip(
                                selected = state.theme == mode,
                                onClick = { onThemeChange(mode) },
                                label = {
                                    Text(
                                        text = when (mode) {
                                            ThemeMode.System -> stringResource(id = R.string.theme_system)
                                            ThemeMode.Light -> stringResource(id = R.string.theme_light)
                                            ThemeMode.Dark -> stringResource(id = R.string.theme_dark)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.notifications))
                    Switch(checked = state.notificationsEnabled, onCheckedChange = onNotifications)
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(id = R.string.glass_size))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(200, 250, 300, 350).forEach { size ->
                            FilterChip(
                                selected = state.glassSize == size,
                                onClick = { onGlassSizeChange(size) },
                                label = { Text("${size} ml") }
                            )
                        }
                    }
                }
            }
        }
    }
}