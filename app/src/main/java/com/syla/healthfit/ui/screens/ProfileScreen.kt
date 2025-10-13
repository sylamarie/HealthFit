package com.syla.healthfit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.syla.healthfit.domain.GoalCalculator
import com.syla.healthfit.model.Sex
import com.syla.healthfit.model.UserProfile
import com.syla.healthfit.ui.components.SectionHeader
import com.syla.healthfit.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    initial: UserProfile,
    onSave: (UserProfile) -> Unit,
    onBack: () -> Unit
) {
    var age by remember(initial) { mutableStateOf(if (initial.age == 0) "" else initial.age.toString()) }
    var height by remember(initial) { mutableStateOf(if (initial.heightCm == 0) "" else initial.heightCm.toString()) }
    var weight by remember(initial) { mutableStateOf(if (initial.weightKg == 0f) "" else initial.weightKg.toString()) }
    var goal by remember(initial) { mutableStateOf(if (initial.goalWeightKg == 0f) "" else initial.goalWeightKg.toString()) }
    var sex by remember(initial) { mutableStateOf(initial.sex) }

    val scroll = rememberScrollState()
    val candidateProfile = UserProfile(
        age = age.toIntOrNull() ?: 0,
        heightCm = height.toIntOrNull() ?: 0,
        weightKg = weight.toFloatOrNull() ?: 0f,
        goalWeightKg = goal.toFloatOrNull() ?: 0f,
        sex = sex
    )
    val goalsPreview = GoalCalculator.calculate(candidateProfile)
    val isValid = candidateProfile.age > 0 && candidateProfile.heightCm > 0 && candidateProfile.weightKg > 0f && candidateProfile.goalWeightKg > 0f

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
                title = { Text("Your Profile", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(padding)
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            SectionHeader(title = "Body metrics")
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                ProfileField(
                    label = "Age (years)",
                    value = age,
                    onChange = { age = it.filter(Char::isDigit) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                ProfileField(
                    label = "Height (cm)",
                    value = height,
                    onChange = { height = it.filter(Char::isDigit) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                ProfileField(
                    label = "Weight (kg)",
                    value = weight,
                    onChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                ProfileField(
                    label = "Goal weight (kg)",
                    value = goal,
                    onChange = { goal = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            SectionHeader(title = "Tracking preferences")
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                SexFilterChip("Female", sex == Sex.Female) { sex = Sex.Female }
                SexFilterChip("Male", sex == Sex.Male) { sex = Sex.Male }
                SexFilterChip("Other", sex == Sex.Other) { sex = Sex.Other }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                SectionHeader(title = "Daily preview")
                Text(
                    text = if (isValid) {
                        "Calories: ${goalsPreview.caloriesKcal} kcal\nWater: ${goalsPreview.waterMl} ml\nSteps: ${goalsPreview.stepsTarget}"
                    } else {
                        "Fill in your profile to generate tailored goals."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSave(candidateProfile) },
                enabled = isValid
            ) {
                Text("Save & Recalculate")
            }

            if (!isValid) {
                Text(
                    text = "Enter positive values to unlock tailored guidance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SexFilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) }
    )
}