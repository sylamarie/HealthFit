# HealthFit

## Overview
HealthFit is a Kotlin-based Android app that I am building to sharpen my mobile engineering skills while delivering a practical health companion. The experience guides users through entering their personal profile, then surfaces daily nutrition, hydration, and activity goals that adapt to those metrics.

**How it works**
1. Launch the app to land on the dashboard and review today’s calorie, water, and step goals.
2. Navigate to the Profile screen to enter or adjust age, height, weight, goal weight, and activity preferences.
3. Return to the dashboard to see recalculated recommendations and tap through to the Checklist screen.
4. Mark hydration, step, and workout tasks as complete while progress rings update in real time.

**Purpose**
My aim is to practice architecting a polished Compose application that combines sensor data, persistent storage, and approachable UX patterns so users can manage their wellness habits in one place.

[Software Demo Video](https://youtu.be/healthfit-demo)

## Development Environment
- **IDE & SDKs:** Android Studio Giraffe, Android SDK 34, and Gradle 8.x with the Android Gradle Plugin.
- **Languages & Frameworks:** Kotlin, Jetpack Compose for UI, Kotlin Coroutines & Flow for reactive state, and Jetpack Navigation Compose for screen transitions.
- **Storage & Services:** Jetpack DataStore for persisting profile and checklist progress, and the Android `SensorManager` step counter integration for passive activity tracking.

## Running the App
1. Clone the repository and open the project root in Android Studio.
2. Allow Gradle to finish syncing dependencies. If prompted, accept any required SDK or Compose component downloads.
3. Connect an Android device with USB debugging enabled or launch an Android Virtual Device (API 26+ recommended).
4. Press **Run ▶︎** in Android Studio and choose the target device to install and launch the HealthFit app.

## Useful Websites
- [Android Developers – Jetpack Compose Basics](https://developer.android.com/jetpack/compose/tutorial)
- [Material Design 3 Guidelines](https://m3.material.io/)
- [Kotlin Coroutines Documentation](https://kotlinlang.org/docs/coroutines-overview.html)

## Future Work
- Expand nutrition calculations with macronutrient breakdowns and configurable dietary goals.
- Introduce reminder notifications and streak tracking to reinforce daily habits.
- Add exportable progress history charts backed by Room or DataStore Proto serialization.