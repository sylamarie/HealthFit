package com.syla.healthfit.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.syla.healthfit.R

enum class AppDestination(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    Dashboard("dashboard", R.string.nav_dashboard, Icons.Filled.Assessment),
    Steps("steps", R.string.nav_steps, Icons.Filled.FitnessCenter),
    Water("water", R.string.nav_water, Icons.Outlined.LocalDrink),
    Nutrition("nutrition", R.string.nav_nutrition, Icons.Outlined.Restaurant),
    Profile("profile", R.string.nav_profile, Icons.Filled.Person),
    Settings("settings", R.string.nav_settings, Icons.Filled.Settings);

    companion object {
        val drawerDestinations = listOf(Dashboard, Steps, Water, Nutrition, Profile, Settings)
    }
}
