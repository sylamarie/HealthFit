package com.syla.healthfit.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.syla.healthfit.R
import com.syla.healthfit.domain.HealthCalculator
import com.syla.healthfit.model.ActivityLevel
import com.syla.healthfit.model.Sex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(state: ProfileUiState, onUpdate: (ProfileForm) -> Unit, onSave: () -> Unit) {
    val scroll = rememberScrollState()
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(id = R.string.profile_details), style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = state.form.age,
                onValueChange = { onUpdate(state.form.copy(age = it.filter(Char::isDigit))) },
                label = { Text(stringResource(id = R.string.age)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.form.height,
                onValueChange = { onUpdate(state.form.copy(height = it.filter(Char::isDigit))) },
                label = { Text(stringResource(id = R.string.height)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.form.weight,
                onValueChange = { onUpdate(state.form.copy(weight = it.filter { ch -> ch.isDigit() || ch == '.' })) },
                label = { Text(stringResource(id = R.string.weight)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.form.goalWeight,
                onValueChange = { onUpdate(state.form.copy(goalWeight = it.filter { ch -> ch.isDigit() || ch == '.' })) },
                label = { Text(stringResource(id = R.string.goal_weight)) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownField(
                label = stringResource(id = R.string.sex),
                options = Sex.values().toList(),
                selected = state.form.sex,
                optionLabel = { option ->
                    when (option) {
                        Sex.Male -> stringResource(id = R.string.male)
                        Sex.Female -> stringResource(id = R.string.female)
                        Sex.Other -> stringResource(id = R.string.other)
                    }
                },
                onSelect = { onUpdate(state.form.copy(sex = it)) }
            )
            DropdownField(
                label = stringResource(id = R.string.activity_level),
                options = ActivityLevel.values().toList(),
                selected = state.form.activityLevel,
                optionLabel = { option ->
                    when (option) {
                        ActivityLevel.Sedentary -> stringResource(id = R.string.activity_sedentary)
                        ActivityLevel.LightlyActive -> stringResource(id = R.string.activity_light)
                        ActivityLevel.ModeratelyActive -> stringResource(id = R.string.activity_moderate)
                        ActivityLevel.VeryActive -> stringResource(id = R.string.activity_very)
                        ActivityLevel.ExtraActive -> stringResource(id = R.string.activity_extra)
                    }
                },
                onSelect = { onUpdate(state.form.copy(activityLevel = it)) }
            )
            OutlinedTextField(
                value = state.form.glassSize,
                onValueChange = { onUpdate(state.form.copy(glassSize = it.filter(Char::isDigit))) },
                label = { Text(stringResource(id = R.string.glass_size)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.form.stepGoal,
                onValueChange = { onUpdate(state.form.copy(stepGoal = it.filter(Char::isDigit))) },
                label = { Text(stringResource(id = R.string.daily_step_goal)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.form.waterGoal,
                onValueChange = { onUpdate(state.form.copy(waterGoal = it.filter(Char::isDigit))) },
                label = { Text(stringResource(id = R.string.daily_water_goal)) },
                modifier = Modifier.fillMaxWidth()
            )
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(id = R.string.bmi_label))
                    Text(text = String.format("%.1f", state.bmi), fontWeight = FontWeight.Bold)
                    Text(text = state.bmiCategory.label)
                    Text(text = state.bmiCategory.description, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(id = R.string.tdee_label))
                    Text(text = "${state.tdee} kcal", fontWeight = FontWeight.Bold)
                }
            }
            Button(onClick = onSave, modifier = Modifier.align(Alignment.End)) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownField(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            OutlinedTextField(
                value = optionLabel(selected),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(expanded = expanded.value, onDismissRequest = { expanded.value = false }) {
                options.forEach { option ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onSelect(option)
                            expanded.value = false
                        }
                    )
                }
            }
        }
    }
}