package com.syla.healthfit

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.syla.healthfit.ui.Routes
import com.syla.healthfit.ui.screens.ChecklistScreen
import com.syla.healthfit.ui.screens.DashboardScreen
import com.syla.healthfit.ui.screens.ProfileScreen
import com.syla.healthfit.ui.theme.HealthFitTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    private val vm: MainViewModel by viewModels()
    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    private val requestActivityRecognition =
        registerForActivityResult(RequestPermission()) { granted ->
            if (granted) setupStepSensor()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        maybeRequestPermissionAndSetupSensor()

        setContent {
            HealthFitTheme {
                val nav = rememberNavController()
                val state by vm.ui.collectAsState()

                Surface(color = MaterialTheme.colorScheme.background) {
                    NavHost(
                        navController = nav,
                        startDestination = Routes.Dashboard
                    ) {
                        composable(Routes.Dashboard) {
                            DashboardScreen(
                                state = state,
                                onGoProfile = { nav.navigate(Routes.Profile) },
                                onGoChecklist = { nav.navigate(Routes.Checklist) },
                                onManualStepAdd = { vm.setSteps((state.stepsToday + it).coerceAtLeast(0)) }
                            )
                        }
                        composable(Routes.Profile) {
                            ProfileScreen(
                                initial = state.profile,
                                onSave = { vm.saveProfile(it); nav.popBackStack() },
                                onBack = { nav.popBackStack() }
                            )
                        }
                        composable(Routes.Checklist) {
                            ChecklistScreen(
                                state = state,
                                onToggle = { id, done -> vm.toggleChecklist(id, done) },
                                onBack = { nav.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun maybeRequestPermissionAndSetupSensor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) setupStepSensor() else requestActivityRecognition.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            setupStepSensor()
        }
    }

    private fun setupStepSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val total = event?.values?.firstOrNull()?.toInt() ?: return
        vm.applyStepSensor(total)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onDestroy() { super.onDestroy(); sensorManager?.unregisterListener(this) }
}