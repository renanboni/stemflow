package com.boni.stemflow.feature.library.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.boni.stemflow.core.designsystem.R as DesignR
import com.boni.stemflow.core.designsystem.component.GlassIconButton
import com.boni.stemflow.core.designsystem.component.SearchField
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.feature.library.R

@Composable
internal fun LibraryHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    collapseProgress: Float,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    searchFocusRequester: FocusRequester? = null,
) {
    val progress by animateFloatAsState(
        targetValue = collapseProgress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "libraryHeaderCollapse",
    )
    val eased = FastOutSlowInEasing.transform(progress)

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        TitleRow(progress = eased, onSearchClick = onSearchClick)
        CollapsibleSearchField(
            query = query,
            onQueryChange = onQueryChange,
            progress = eased,
            focusRequester = searchFocusRequester,
        )
    }
}

@Composable
private fun TitleRow(progress: Float, onSearchClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        GlassIconButton(
            icon = painterResource(DesignR.drawable.ic_search),
            contentDescription = stringResource(DesignR.string.designsystem_search),
            onClick = onSearchClick,
            modifier = Modifier
                .align(BiasAlignment(horizontalBias = -1f, verticalBias = 0f))
                .padding(start = 20.dp)
                .graphicsLayer {
                    alpha = progress
                    val s = 0.5f + 0.5f * progress
                    scaleX = s
                    scaleY = s
                },
        )
        Text(
            text = stringResource(R.string.library_title),
            fontSize = lerp(24.sp, 16.sp, progress),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .align(BiasAlignment(horizontalBias = -1f + progress, verticalBias = 0f))
                .padding(start = lerp(24.dp, 0.dp, progress)),
        )
    }
}

@Composable
private fun CollapsibleSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    progress: Float,
    focusRequester: FocusRequester? = null,
) {
    Box(
        modifier = Modifier
            .graphicsLayer { alpha = 1f - progress }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val collapsedHeight = (placeable.height * (1f - progress)).toInt().coerceAtLeast(0)
                layout(placeable.width, collapsedHeight) {
                    placeable.place(0, 0)
                }
            },
    ) {
        SearchField(
            query = query,
            onQueryChange = onQueryChange,
            focusRequester = focusRequester,
        )
    }
}

@Preview
@Composable
private fun LibraryHeaderExpandedPreview() {
    StemflowTheme {
        LibraryHeader(
            query = "",
            onQueryChange = {},
            collapseProgress = 0f,
            onSearchClick = {},
        )
    }
}

@Preview
@Composable
private fun LibraryHeaderMidPreview() {
    StemflowTheme {
        LibraryHeader(
            query = "",
            onQueryChange = {},
            collapseProgress = 0.5f,
            onSearchClick = {},
        )
    }
}

@Preview
@Composable
private fun LibraryHeaderCollapsedPreview() {
    StemflowTheme {
        LibraryHeader(
            query = "",
            onQueryChange = {},
            collapseProgress = 1f,
            onSearchClick = {},
        )
    }
}
