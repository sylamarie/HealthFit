package com.syla.healthfit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.syla.healthfit.model.Sex
import com.syla.healthfit.model.UserProfile

@Composable
fun ProfileScreen(
    initial: UserProfile,
    onSave: (UserProfile) -> Unit
) {
    var age by remember(initial) { mutableStateOf(if (initial.age == 0) "" else initial.age.toString()) }
    var height by remember(initial) { mutableStateOf(if (initial.heightCm == 0) "" else initial.heightCm.toString()) }
    var weight by remember(initial) { mutableStateOf(if (initial.weightKg == 0f) "" else initial.weightKg.toString()) }
    var goal by remember(initial) { mutableStateOf(if (initial.goalWeightKg == 0f) "" else initial.goalWeightKg.toString()) }
    var sex by remember(initial) { mutableStateOf(initial.sex) }

    val valid = listOf(age, height, weight, goal).all { it.toFloatOrNull()?.let { v -> v > 0f } == true }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Your Profile", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = age,
            onValueChange = { age = it.filter(Char::isDigit) },
            label = { Text("Age (years)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = height,
            onValueChange = { height = it.filter(Char::isDigit) },
            label = { Text("Height (cm)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it.filter { ch -> ch.isDigit() || ch == '.' } },
            label = { Text("Weight (kg)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
            value = goal,
            onValueChange = { goal = it.filter { ch -> ch.isDigit() || ch == '.' } },
            label = { Text("Goal Weight (kg)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SexChip("Female", sex == Sex.Female) { sex = Sex.Female }
            SexChip("Male", sex == Sex.Male) { sex = Sex.Male }
            SexChip("Other", sex == Sex.Other) { sex = Sex.Other }
        }

        Button(
            onClick = {
                onSave(
                    UserProfile(
                        age = age.toInt(),
                        heightCm = height.toInt(),
                        weightKg = weight.toFloat(),
                        goalWeightKg = goal.toFloat(),
                        sex = sex
                    )
                )
            },
            enabled = valid
        ) { Text("Save") }

        if (!valid) {
            AssistChip(onClick = {}, label = { Text("Enter all fields > 0") })
        }
    }
}

@Composable
private fun SexChip(text: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) FilledTonalButton(onClick = onClick) { Text(text) }
    else OutlinedButton(onClick = onClick) { Text(text) }
}