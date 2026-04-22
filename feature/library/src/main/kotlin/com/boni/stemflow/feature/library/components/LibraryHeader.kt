package com.boni.stemflow.feature.library.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.boni.stemflow.core.designsystem.R
import com.boni.stemflow.core.designsystem.component.GlassIconButton
import com.boni.stemflow.core.designsystem.theme.StemflowTheme

@Composable
internal fun LibraryHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    isCollapsed: Boolean,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        if (isCollapsed) {
            CollapsedHeader(onSearchClick = onSearchClick)
        } else {
            ExpandedHeader(query = query, onQueryChange = onQueryChange)
        }
    }
}

@Composable
private fun ExpandedHeader(query: String, onQueryChange: (String) -> Unit) {
    Text(
        text = "Songs",
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
    )
    SearchField(query = query, onQueryChange = onQueryChange)
}

@Composable
private fun CollapsedHeader(onSearchClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        GlassIconButton(
            icon = painterResource(R.drawable.ic_search),
            contentDescription = "Search",
            onClick = onSearchClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp),
        )
        Text(
            text = "Songs",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.Center),
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
            isCollapsed = false,
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
            isCollapsed = true,
            onSearchClick = {},
        )
    }
}
