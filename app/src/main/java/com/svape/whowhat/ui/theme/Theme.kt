package com.svape.whowhat.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary            = Teal50,
    onPrimary          = Color(0xFFFFFFFF),
    primaryContainer   = Teal10,
    onPrimaryContainer = Teal90,

    secondary            = Violet50,
    onSecondary          = Color(0xFFFFFFFF),
    secondaryContainer   = Violet10,
    onSecondaryContainer = Violet90,

    tertiary            = Cream40,
    onTertiary          = OnLight,
    tertiaryContainer   = Cream20,
    onTertiaryContainer = OnLight,

    background    = Cream10,
    onBackground  = OnLight,

    surface          = Color(0xFFFFFFFF),
    onSurface        = OnLight,
    surfaceVariant   = Cream20,
    onSurfaceVariant = Color(0xFF4A5553),

    outline       = Cream40,
    outlineVariant = Cream20,

    error            = Color(0xFFBA1A1A),
    onError          = Color(0xFFFFFFFF),
    errorContainer   = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val DarkColorScheme = darkColorScheme(
    primary            = Teal20,
    onPrimary          = Teal90,
    primaryContainer   = Teal80,
    onPrimaryContainer = Teal10,

    secondary            = Violet20,
    onSecondary          = Violet90,
    secondaryContainer   = Violet80,
    onSecondaryContainer = Violet10,

    tertiary            = Cream40,
    onTertiary          = Dark90,
    tertiaryContainer   = Dark60,
    onTertiaryContainer = Cream20,

    background    = Dark90,
    onBackground  = OnDark,

    surface          = Dark80,
    onSurface        = OnDark,
    surfaceVariant   = Dark70,
    onSurfaceVariant = Color(0xFFAABCBA),

    outline       = Dark60,
    outlineVariant = Dark70,

    error            = Color(0xFFFFB4AB),
    onError          = Color(0xFF690005),
    errorContainer   = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun WhowhatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}