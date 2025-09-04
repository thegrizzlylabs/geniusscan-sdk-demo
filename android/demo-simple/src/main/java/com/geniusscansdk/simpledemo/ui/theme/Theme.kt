package com.geniusscansdk.simpledemo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.geniusscansdk.simpledemo.R

@Composable
fun SimpleDemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lightColorScheme = lightColorScheme(
        primary = colorResource(id = R.color.md_theme_primary),
        onPrimary = colorResource(id = R.color.md_theme_onPrimary),
        primaryContainer = colorResource(id = R.color.md_theme_primaryContainer),
        onPrimaryContainer = colorResource(id = R.color.md_theme_onPrimaryContainer),
        secondary = colorResource(id = R.color.md_theme_secondary),
        onSecondary = colorResource(id = R.color.md_theme_onSecondary),
        secondaryContainer = colorResource(id = R.color.md_theme_secondaryContainer),
        onSecondaryContainer = colorResource(id = R.color.md_theme_onSecondaryContainer),
        tertiary = colorResource(id = R.color.md_theme_tertiary),
        onTertiary = colorResource(id = R.color.md_theme_onTertiary),
        tertiaryContainer = colorResource(id = R.color.md_theme_tertiaryContainer),
        onTertiaryContainer = colorResource(id = R.color.md_theme_onTertiaryContainer),
        background = colorResource(id = R.color.md_theme_background),
        onBackground = colorResource(id = R.color.md_theme_onBackground),
        surface = colorResource(id = R.color.md_theme_surface),
        onSurface = colorResource(id = R.color.md_theme_onSurface),
        onSurfaceVariant = colorResource(id = R.color.md_theme_onSurfaceVariant),
        surfaceContainerLowest = colorResource(id = R.color.md_theme_surfaceContainerLowest),
        surfaceContainerLow = colorResource(id = R.color.md_theme_surfaceContainerLow),
        surfaceContainer = colorResource(id = R.color.md_theme_surfaceContainer),
        surfaceContainerHigh = colorResource(id = R.color.md_theme_surfaceContainerHigh),
        surfaceContainerHighest = colorResource(id = R.color.md_theme_surfaceContainerHighest),
        error = colorResource(id = R.color.md_theme_error),
        onError = colorResource(id = R.color.md_theme_onError),
        errorContainer = colorResource(id = R.color.md_theme_errorContainer),
        onErrorContainer = colorResource(id = R.color.md_theme_onErrorContainer),
        outline = colorResource(id = R.color.md_theme_outline)
    )
    val darkColorScheme = darkColorScheme(
        primary = colorResource(id = R.color.md_theme_primary),
        onPrimary = colorResource(id = R.color.md_theme_onPrimary),
        primaryContainer = colorResource(id = R.color.md_theme_primaryContainer),
        onPrimaryContainer = colorResource(id = R.color.md_theme_onPrimaryContainer),
        secondary = colorResource(id = R.color.md_theme_secondary),
        onSecondary = colorResource(id = R.color.md_theme_onSecondary),
        secondaryContainer = colorResource(id = R.color.md_theme_secondaryContainer),
        onSecondaryContainer = colorResource(id = R.color.md_theme_onSecondaryContainer),
        tertiary = colorResource(id = R.color.md_theme_tertiary),
        onTertiary = colorResource(id = R.color.md_theme_onTertiary),
        tertiaryContainer = colorResource(id = R.color.md_theme_tertiaryContainer),
        onTertiaryContainer = colorResource(id = R.color.md_theme_onTertiaryContainer),
        background = colorResource(id = R.color.md_theme_background),
        onBackground = colorResource(id = R.color.md_theme_onBackground),
        surface = colorResource(id = R.color.md_theme_surface),
        onSurface = colorResource(id = R.color.md_theme_onSurface),
        onSurfaceVariant = colorResource(id = R.color.md_theme_onSurfaceVariant),
        surfaceContainerLowest = colorResource(id = R.color.md_theme_surfaceContainerLowest),
        surfaceContainerLow = colorResource(id = R.color.md_theme_surfaceContainerLow),
        surfaceContainer = colorResource(id = R.color.md_theme_surfaceContainer),
        surfaceContainerHigh = colorResource(id = R.color.md_theme_surfaceContainerHigh),
        surfaceContainerHighest = colorResource(id = R.color.md_theme_surfaceContainerHighest),
        error = colorResource(id = R.color.md_theme_error),
        onError = colorResource(id = R.color.md_theme_onError),
        errorContainer = colorResource(id = R.color.md_theme_errorContainer),
        onErrorContainer = colorResource(id = R.color.md_theme_onErrorContainer),
        outline = colorResource(id = R.color.md_theme_outline)
    )

    val colorScheme =
        if (!darkTheme) {
            lightColorScheme
        } else {
            darkColorScheme
        }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun sectionTitleStyle() = TextStyle(
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp
)
