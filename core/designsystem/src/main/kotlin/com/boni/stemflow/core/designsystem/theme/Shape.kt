package com.boni.stemflow.core.designsystem.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val StemflowShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

/**
 * Shape tokens not covered by Material3's [Shapes] slots. Access via
 * `MaterialTheme.shapes.cover`.
 */
val Shapes.cover: CornerBasedShape
    get() = RoundedCornerShape(20.dp)
