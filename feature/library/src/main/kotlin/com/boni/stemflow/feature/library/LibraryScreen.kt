package com.boni.stemflow.feature.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.ui.component.SongRow
import com.boni.stemflow.core.ui.component.TrackOptionsSheet
import com.boni.stemflow.feature.library.components.AppendError
import com.boni.stemflow.feature.library.components.AppendSpinner
import com.boni.stemflow.feature.library.components.LibraryHeader
import com.boni.stemflow.feature.library.components.OfflineBanner
import com.boni.stemflow.feature.library.components.SectionHeading
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

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
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val collapseThresholdPx = with(LocalDensity.current) { 120.dp.toPx() }
    val collapseProgress by remember(collapseThresholdPx) {
        derivedStateOf {
            if (listState.firstVisibleItemIndex == 0) {
                (listState.firstVisibleItemScrollOffset / collapseThresholdPx).coerceIn(0f, 1f)
            } else {
                1f
            }
        }
    }

    Scaffold(
        topBar = {
            LibraryHeader(
                query = uiState.query,
                onQueryChange = onQueryChange,
                collapseProgress = collapseProgress,
                onSearchClick = { scope.launch { listState.animateScrollToItem(0) } },
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
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
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

private fun previewTrack(id: Long, title: String, artist: String) = Track(
    trackId = id,
    name = title,
    artistName = artist,
    artistId = 1L,
    collectionId = 10L,
    collectionName = "Album",
    artworkUrl100 = null,
    artworkUrl600 = null,
    previewUrl = null,
    trackTimeMillis = null,
    primaryGenreName = null,
    releaseDate = null,
    trackViewUrl = null,
)

@Preview
@Composable
private fun LibraryScreenRecentPreview() {
    StemflowTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                query = "",
                recentlyPlayed = listOf(
                    previewTrack(1L, "Welcome to the Jungle", "Guns N' Roses"),
                    previewTrack(2L, "November Rain", "Guns N' Roses"),
                    previewTrack(3L, "Sweet Child O' Mine", "Guns N' Roses"),
                ),
                isOffline = false,
            ),
            pagedItems = flowOf(PagingData.empty<Track>()).collectAsLazyPagingItems(),
            onQueryChange = {},
            onTrackClick = {},
            onOpenAlbum = {},
        )
    }
}

@Preview
@Composable
private fun LibraryScreenOfflinePreview() {
    StemflowTheme {
        LibraryScreen(
            uiState = LibraryUiState(
                query = "",
                recentlyPlayed = listOf(previewTrack(1L, "Paradise City", "Guns N' Roses")),
                isOffline = true,
            ),
            pagedItems = flowOf(PagingData.empty<Track>()).collectAsLazyPagingItems(),
            onQueryChange = {},
            onTrackClick = {},
            onOpenAlbum = {},
        )
    }
}

@Preview
@Composable
private fun LibraryScreenEmptyPreview() {
    StemflowTheme {
        LibraryScreen(
            uiState = LibraryUiState(),
            pagedItems = flowOf(PagingData.empty<Track>()).collectAsLazyPagingItems(),
            onQueryChange = {},
            onTrackClick = {},
            onOpenAlbum = {},
        )
    }
}
