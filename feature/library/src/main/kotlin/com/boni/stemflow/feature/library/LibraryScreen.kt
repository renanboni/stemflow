package com.boni.stemflow.feature.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.ui.component.SongRow
import com.boni.stemflow.core.ui.component.TrackOptionsSheet

@Composable
fun LibraryScreen(
    onTrackClick: (Long) -> Unit,
    onOpenAlbum: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagedItems = viewModel.pagedTracks.collectAsLazyPagingItems()
    LibraryScreen(
        uiState = uiState,
        pagedItems = pagedItems,
        onQueryChange = viewModel::onQueryChange,
        onTrackClick = onTrackClick,
        onOpenAlbum = onOpenAlbum,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LibraryScreen(
    uiState: LibraryUiState,
    pagedItems: LazyPagingItems<Track>,
    onQueryChange: (String) -> Unit,
    onTrackClick: (Long) -> Unit,
    onOpenAlbum: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTrack by remember { mutableStateOf<Track?>(null) }
    val isRefreshing = pagedItems.loadState.refresh is LoadState.Loading &&
        pagedItems.itemCount > 0

    Scaffold(
        topBar = {
            SearchField(
                query = uiState.query,
                onQueryChange = onQueryChange,
            )
        },
        modifier = modifier,
    ) { inner ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { pagedItems.refresh() },
            modifier = Modifier
                .padding(inner)
                .fillMaxSize(),
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                if (uiState.isOffline) {
                    item { OfflineBanner() }
                }
                if (uiState.query.isBlank() && uiState.recentlyPlayed.isNotEmpty()) {
                    item {
                        SectionHeading("Recently played")
                    }
                    items(uiState.recentlyPlayed, key = { "recent-${it.trackId}" }) { track ->
                        SongRow(
                            title = track.name,
                            artist = track.artistName,
                            artworkUrl = track.artworkUrl100,
                            onClick = { onTrackClick(track.trackId) },
                            onMoreClick = { selectedTrack = track },
                        )
                    }
                }
                items(
                    count = pagedItems.itemCount,
                    key = pagedItems.itemKey { "search-${it.trackId}" },
                ) { idx ->
                    val track = pagedItems[idx] ?: return@items
                    SongRow(
                        title = track.name,
                        artist = track.artistName,
                        artworkUrl = track.artworkUrl100,
                        onClick = { onTrackClick(track.trackId) },
                        onMoreClick = { selectedTrack = track },
                    )
                }
                when (pagedItems.loadState.append) {
                    is LoadState.Loading -> item { AppendSpinner() }
                    is LoadState.Error -> item { AppendError() }
                    else -> Unit
                }
            }
        }
    }

    selectedTrack?.let { track ->
        TrackOptionsSheet(
            title = track.name,
            artist = track.artistName,
            artworkUrl = track.artworkUrl100,
            onOpenAlbum = {
                onOpenAlbum(track.collectionId)
                selectedTrack = null
            },
            onDismiss = { selectedTrack = null },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    val fill = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
    Surface(color = MaterialTheme.colorScheme.surface) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search songs") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fill,
                unfocusedContainerColor = fill,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
    )
}

@Composable
private fun OfflineBanner() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "You're offline — showing cached results",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun AppendSpinner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AppendError() {
    Text(
        text = "Load error — pull to retry",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}
