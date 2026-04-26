package com.boni.stemflow.core.designsystem.component

import androidx.compose.animation.core.animate
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class CollapsibleHeaderState internal constructor(
    val listState: LazyListState,
    private val thresholdPx: Float,
) {
    private var collapsedPx by mutableFloatStateOf(0f)

    val progress: Float
        get() = (collapsedPx / thresholdPx).coerceIn(0f, 1f)

    suspend fun expand() {
        animate(initialValue = collapsedPx, targetValue = 0f) { value, _ ->
            collapsedPx = value
        }
    }

    val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val dy = available.y
            if (dy >= 0f || collapsedPx >= thresholdPx) return Offset.Zero
            val consume = minOf(-dy, thresholdPx - collapsedPx)
            collapsedPx += consume
            return Offset(0f, -consume)
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            val dy = available.y
            if (dy <= 0f || collapsedPx <= 0f) return Offset.Zero
            val consume = minOf(dy, collapsedPx)
            collapsedPx -= consume
            return Offset(0f, consume)
        }
    }
}

@Composable
fun rememberCollapsibleHeaderState(
    listState: LazyListState = rememberLazyListState(),
    threshold: Dp = 56.dp,
): CollapsibleHeaderState {
    val thresholdPx = with(LocalDensity.current) { threshold.toPx() }
    return remember(listState, thresholdPx) {
        CollapsibleHeaderState(listState, thresholdPx)
    }
}
