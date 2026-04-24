package com.boni.stemflow.feature.player.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import kotlin.math.abs
import kotlinx.coroutines.launch

@Composable
internal fun PlaybackTime(
    positionMs: Long,
    durationMs: Long,
    amplitude: Float,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rawProgress = if (durationMs > 0) {
        (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    } else {
        0f
    }
    val progress = remember { Animatable(rawProgress) }
    var mode by remember { mutableStateOf<ScrubMode>(ScrubMode.Follow) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(rawProgress, mode) {
        when (val current = mode) {
            is ScrubMode.Settling -> {
                if (abs(rawProgress - current.target) < CATCH_UP_THRESHOLD) {
                    mode = ScrubMode.Follow
                }
            }

            ScrubMode.Follow -> {
                progress.animateTo(
                    targetValue = rawProgress,
                    animationSpec = tween(
                        durationMillis = PROGRESS_TWEEN_MS,
                        easing = LinearEasing
                    ),
                )
            }

            ScrubMode.Dragging -> Unit
        }
    }

    val displayProgress = progress.value
    val displayPositionMs = if (durationMs > 0) {
        (displayProgress * durationMs).toLong()
    } else {
        positionMs
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SeekBar(
            progress = displayProgress,
            amplitude = amplitude,
            enabled = durationMs > 0,
            onSeekStart = { fraction ->
                mode = ScrubMode.Dragging
                scope.launch { progress.snapTo(fraction.coerceIn(0f, 1f)) }
            },
            onSeekChange = { fraction ->
                scope.launch { progress.snapTo(fraction.coerceIn(0f, 1f)) }
            },
            onSeekCommit = {
                val target = progress.value
                mode = ScrubMode.Settling(target)
                onSeek((target * durationMs).toLong())
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatTime(displayPositionMs),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
            Text(
                text = "-${formatTime((durationMs - displayPositionMs).coerceAtLeast(0L))}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun SeekBar(
    progress: Float,
    amplitude: Float,
    enabled: Boolean,
    onSeekStart: (Float) -> Unit,
    onSeekChange: (Float) -> Unit,
    onSeekCommit: () -> Unit,
) {
    val clamped = progress.coerceIn(0f, 1f)
    val animatedAmplitude by animateFloatAsState(
        targetValue = amplitude.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = AMPLITUDE_TWEEN_MS, easing = LinearEasing),
        label = "amplitude",
    )
    val barHeight = BAR_BASE_HEIGHT + BAR_AMPLITUDE_RANGE * animatedAmplitude
    val handleScale = 1f + HANDLE_AMPLITUDE_SCALE * animatedAmplitude

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .let { base ->
                if (!enabled) base
                else base.pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        val width = size.width.toFloat().coerceAtLeast(1f)
                        onSeekStart(down.position.x / width)
                        drag(down.id) { change ->
                            change.consume()
                            onSeekChange(change.position.x / width)
                        }
                        onSeekCommit()
                    }
                }
            },
    ) {
        val trackWidth: Dp = maxWidth
        val onSurface = MaterialTheme.colorScheme.onBackground
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth()
                .height(barHeight)
                .clip(CircleShape)
                .background(onSurface.copy(alpha = 0.25f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(trackWidth * clamped)
                .height(barHeight)
                .clip(CircleShape)
                .background(onSurface.copy(alpha = 0.6f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (trackWidth * clamped) - HANDLE_HALF)
                .size(24.dp)
                .scale(handleScale)
                .clip(CircleShape)
                .background(onSurface),
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private val HANDLE_HALF = 12.dp
private val BAR_BASE_HEIGHT = 4.dp
private val BAR_AMPLITUDE_RANGE = 18.dp
private const val HANDLE_AMPLITUDE_SCALE = 0.35f
private const val PROGRESS_TWEEN_MS = 500
private const val AMPLITUDE_TWEEN_MS = 100
private const val CATCH_UP_THRESHOLD = 0.02f

private sealed interface ScrubMode {
    data object Follow : ScrubMode
    data object Dragging : ScrubMode
    data class Settling(val target: Float) : ScrubMode
}

@Preview(widthDp = 414)
@Composable
private fun PlaybackTimePreview() {
    StemflowTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
        ) {
            PlaybackTime(
                positionMs = 86_000L,
                durationMs = 260_000L,
                amplitude = 0.6f,
                onSeek = {},
            )
        }
    }
}
