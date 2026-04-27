package com.boni.stemflow.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Custom color tokens not covered by Material3's [ColorScheme] slots. Keeps the raw palette
 * internal to the design-system module; callers read them off the theme as
 * `MaterialTheme.colorScheme.textTertiary`.
 */

val ColorScheme.textTertiary: Color
    get() = StemflowTextTertiary

val ColorScheme.elementTertiary: Color
    get() = StemflowElementTertiary
