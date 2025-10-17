# Overview
HealthFit is a Kotlin-based Android wellness companion built to refine my mobile engineering craft while experimenting with modern Android patterns. Users create a personalized profile, receive adaptive daily goals, and track hydration, movement, and workout habits on a streamlined dashboard.

To use the app, launch it to view today’s calorie, water, and step targets. Open the Profile screen to enter age, height, weight, goal weight, and activity preferences, then return to the dashboard to see refreshed recommendations. Visit the Checklist screen to mark hydration, step, and workout tasks as complete while animated progress rings respond in real time.

My purpose with HealthFit is to explore how Jetpack Compose, reactive state management, and sensor integrations can deliver actionable insights that keep wellness goals front and center for everyday users.

# Development Environment

I am building HealthFit with Android Studio Giraffe, Android SDK 34, and the Android Gradle Plugin running on Gradle 8.x. This setup provides rich tooling for Compose previews, device emulation, and performance profiling.

The core stack centers on Kotlin with Jetpack Compose for UI, Kotlin Coroutines and Flow for asynchronous state, Jetpack Navigation Compose for screen transitions, and Jetpack DataStore plus the Android `SensorManager` step counter for persistence and passive activity tracking.

# Useful Websites

* [Android Developers – Jetpack Compose Basics](https://developer.android.com/jetpack/compose/tutorial)
* [Material Design 3 Guidelines](https://m3.material.io/)
* [Kotlin Coroutines Documentation](https://kotlinlang.org/docs/coroutines-overview.html)

# Future Work

* Expand nutrition calculations with macronutrient breakdowns and configurable dietary goals.
* Introduce reminder notifications and streak tracking to reinforce daily habits.
* Add exportable progress history charts backed by Room or DataStore Proto serialization.
