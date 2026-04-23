package com.boni.stemflow.core.designsystem.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Softens the top and bottom edges of a scrollable container by masking them with a vertical
 * alpha gradient. Requires an offscreen compositing layer so the `DstIn` blend only affects this
 * modifier's content.
 */
fun Modifier.fadingEdges(
    top: Dp = 24.dp,
    bottom: Dp = 24.dp,
): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val height = size.height
        if (height <= 0f) return@drawWithContent
        val topStop = (top.toPx() / height).coerceIn(0f, 0.5f)
        val bottomStop = 1f - (bottom.toPx() / height).coerceIn(0f, 0.5f)
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color.Transparent,
                topStop to Color.Black,
                bottomStop to Color.Black,
                1f to Color.Transparent,
            ),
            blendMode = BlendMode.DstIn,
        )
    }
