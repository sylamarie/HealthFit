package com.syla.healthfit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.syla.healthfit.R
import com.syla.healthfit.ui.screens.dashboard.DashboardScreen
import com.syla.healthfit.ui.screens.dashboard.DashboardViewModel
import com.syla.healthfit.ui.screens.nutrition.NutritionScreen
import com.syla.healthfit.ui.screens.nutrition.NutritionViewModel
import com.syla.healthfit.ui.screens.profile.ProfileScreen
import com.syla.healthfit.ui.screens.profile.ProfileViewModel
import com.syla.healthfit.ui.screens.settings.SettingsScreen
import com.syla.healthfit.ui.screens.settings.SettingsViewModel
import com.syla.healthfit.ui.screens.steps.StepsScreen
import com.syla.healthfit.ui.screens.steps.StepsViewModel
import com.syla.healthfit.ui.screens.water.WaterScreen
import com.syla.healthfit.ui.screens.water.WaterViewModel

@Composable
fun HealthFitApp(
    permissionGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentDestination = AppDestination.drawerDestinations.firstOrNull { it.route == currentRoute }
        ?: AppDestination.Dashboard
    val title = stringResource(id = currentDestination.titleRes)

    NavigationDrawer(
        currentDestination = currentDestination,
        onDestinationSelected = { destination ->
            navController.navigate(destination.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        topBarTitle = title
    ) {
        NavHost(navController = navController, startDestination = AppDestination.Dashboard.route) {
            composable(AppDestination.Dashboard.route) {
                val viewModel: DashboardViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                DashboardScreen(
                    state = state,
                    onNavigateSteps = { navController.navigate(AppDestination.Steps.route) },
                    onNavigateWater = { navController.navigate(AppDestination.Water.route) },
                    onNavigateNutrition = { navController.navigate(AppDestination.Nutrition.route) },
                    onNavigateProfile = { navController.navigate(AppDestination.Profile.route) }
                )
            }
            composable(AppDestination.Steps.route) {
                val viewModel: StepsViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                StepsScreen(
                    state = state,
                    permissionGranted = permissionGranted,
                    onIncrement = { viewModel.adjustSteps(it) },
                    onManualSet = { viewModel.setManualSteps(it) },
                    onRequestPermission = onRequestPermission
                )
            }
            composable(AppDestination.Water.route) {
                val viewModel: WaterViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                WaterScreen(state = state, onToggle = { viewModel.toggleGlass(it) })
            }
            composable(AppDestination.Nutrition.route) {
                val viewModel: NutritionViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                NutritionScreen(
                    state = uiState,
                    onAmountChange = viewModel::updateAmount,
                    onUnitChange = viewModel::updateUnit,
                    onQueryChange = viewModel::updateQuery,
                    onCustomCaloriesChange = viewModel::updateCustomCalories,
                    onSelectFood = viewModel::selectFood,
                    onSave = viewModel::saveEntry,
                    onEdit = viewModel::editLog,
                    onDelete = viewModel::deleteLog
                )
            }
            composable(AppDestination.Profile.route) {
                val viewModel: ProfileViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                ProfileScreen(
                    state = state,
                    onUpdate = viewModel::setForm,
                    onSave = viewModel::save
                )
            }
            composable(AppDestination.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()
                SettingsScreen(
                    state = state,
                    onThemeChange = viewModel::updateTheme,
                    onNotifications = viewModel::updateNotifications,
                    onGlassSizeChange = viewModel::updateGlassSize
                )
            }
        }
    }
}