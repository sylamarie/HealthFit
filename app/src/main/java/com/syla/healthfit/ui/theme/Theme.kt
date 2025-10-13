package com.syla.healthfit.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val DarkColorScheme = darkColorScheme(
    primary = Emerald80,
    secondary = MistBlue80,
    tertiary = Sunset80
)
private val LightColorScheme = lightColorScheme(
    primary = Emerald40,
    secondary = MistBlue40,
    tertiary = Sunset40
)

@Composable
fun HealthFitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamic: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val shapes = Shapes(
        extraSmall = RoundedCornerShape(6.dp),
        small = RoundedCornerShape(10.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(20.dp),
        extraLarge = RoundedCornerShape(28.dp)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HealthFitTypography,
        shapes = shapes,
        content = content
    )
}