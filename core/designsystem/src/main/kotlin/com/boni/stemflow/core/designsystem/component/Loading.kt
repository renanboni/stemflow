package com.boni.stemflow.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.boni.stemflow.core.designsystem.theme.StemflowTheme

@Composable
fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Equalizer(color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun Equalizer(color: Color) {
    Row(
        modifier = Modifier.height(BAR_MAX_HEIGHT),
        horizontalArrangement = Arrangement.spacedBy(BAR_GAP),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BAR_DELAYS.forEach { delayMs ->
            Bar(color = color, delayMs = delayMs)
        }
    }
}

@Composable
private fun Bar(color: Color, delayMs: Int) {
    val transition = rememberInfiniteTransition(label = "equalizer")
    val height by transition.animateFloat(
        initialValue = BAR_MIN_HEIGHT.value,
        targetValue = BAR_MAX_HEIGHT.value,
        animationSpec = infiniteRepeatable(
            animation = tween(BAR_DURATION_MS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(delayMs),
        ),
        label = "barHeight",
    )
    Box(
        modifier = Modifier
            .width(BAR_WIDTH)
            .height(height.dp)
            .clip(CircleShape)
            .background(color),
    )
}

private val BAR_DELAYS = listOf(0, 160, 80, 240, 120)
private val BAR_WIDTH = 5.dp
private val BAR_GAP = 6.dp
private val BAR_MIN_HEIGHT: Dp = 6.dp
private val BAR_MAX_HEIGHT: Dp = 36.dp
private const val BAR_DURATION_MS = 480

@Preview
@Composable
private fun LoadingPreview() {
    StemflowTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Loading()
        }
    }
}
