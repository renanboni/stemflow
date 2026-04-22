package com.boni.stemflow.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// Dark-only palette extracted from the Stemflow Figma.
// The design is monochromatic: black surface, white text, gray secondary text,
// with a single teal accent used in the splash gradient.

internal val StemflowBlack = Color(0xFF000000)
internal val StemflowWhite = Color(0xFFFFFFFF)

// Elevated surfaces (cards, bottom sheet) — flattened from rgba(38,38,38,0.8).
internal val StemflowSurfaceElevated = Color(0xFF262626)

// Borders / dividers (frame separators in Figma).
internal val StemflowOutline = Color(0xFF444444)

// Secondary text tier — timeline labels, helper text.
internal val StemflowTextSecondary = Color(0xFFA8A8A8)

// Tertiary text tier — artist names in list rows.
internal val StemflowTextTertiary = Color(0xFF737373)

// Splash gradient accent (teal).
internal val StemflowAccent = Color(0xFF008CA0)

// Material3 defaults for dark theme; Figma doesn't spec error colors.
internal val StemflowError = Color(0xFFFFB4AB)
internal val StemflowOnError = Color(0xFF690005)
