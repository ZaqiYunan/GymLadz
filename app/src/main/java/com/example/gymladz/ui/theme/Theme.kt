package com.example.gymladz.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BrightCyan,
    secondary = Turquoise,
    tertiary = DarkCyan,
    background = DarkTealBackground,
    surface = DarkTealSurface,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    error = ErrorRed,
    onError = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = BrightCyan,
    secondary = Turquoise,
    tertiary = DarkCyan,
    background = DarkTealBackground,
    surface = DarkTealSurface,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    error = ErrorRed,
    onError = TextWhite
)

@Composable
fun GymLadzTheme(
    darkTheme: Boolean = true,  // Always use dark theme
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,  // Disable dynamic colors for consistent theming
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}