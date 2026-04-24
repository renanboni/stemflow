package com.boni.stemflow.core.designsystem.component

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class CollapsibleHeaderState internal constructor(
    val listState: LazyListState,
    private val thresholdPx: Float,
) {
    val progress: Float by derivedStateOf {
        if (listState.firstVisibleItemIndex == 0) {
            (listState.firstVisibleItemScrollOffset / thresholdPx).coerceIn(0f, 1f)
        } else {
            1f
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
