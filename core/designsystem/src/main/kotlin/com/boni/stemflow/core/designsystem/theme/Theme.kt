package com.boni.stemflow.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StemflowColorScheme = darkColorScheme(
    primary = StemflowWhite,
    onPrimary = StemflowBlack,
    tertiary = StemflowAccent,
    background = StemflowBlack,
    onBackground = StemflowWhite,
    surface = StemflowBlack,
    onSurface = StemflowWhite,
    surfaceVariant = StemflowSurfaceElevated,
    onSurfaceVariant = StemflowTextSecondary,
    outline = StemflowOutline,
    error = StemflowError,
    onError = StemflowOnError,
)

@Composable
fun StemflowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = StemflowColorScheme,
        typography = StemflowTypography,
        shapes = StemflowShapes,
        content = content,
    )
}
