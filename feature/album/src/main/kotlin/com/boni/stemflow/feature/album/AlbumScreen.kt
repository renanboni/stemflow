package com.boni.stemflow.feature.album

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boni.stemflow.core.designsystem.R as DesignR
import com.boni.stemflow.core.designsystem.component.ArtworkImage
import com.boni.stemflow.core.designsystem.component.ErrorState
import com.boni.stemflow.core.designsystem.component.GlassIconButton
import com.boni.stemflow.core.designsystem.component.Loading
import com.boni.stemflow.core.designsystem.modifier.fadingEdges
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.core.designsystem.theme.cover
import com.boni.stemflow.core.designsystem.theme.textTertiary
import com.boni.stemflow.core.domain.model.Album
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.ui.component.SongRow

@Composable
fun AlbumScreen(
    viewModel: AlbumViewModel,
    onBack: () -> Unit,
    onTrackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AlbumScreen(
        state = state,
        onBack = onBack,
        onTrackClick = onTrackClick,
        modifier = modifier,
    )
}

@Composable
internal fun AlbumScreen(
    state: AlbumLoadState,
    onBack: () -> Unit,
    onTrackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    GlassIconButton(
                        icon = painterResource(DesignR.drawable.ic_back),
                        contentDescription = stringResource(DesignR.string.designsystem_back),
                        onClick = onBack,
                        modifier = Modifier.padding(start = 20.dp),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
        ) {
            when (state) {
                AlbumLoadState.Loading -> Loading(Modifier.fillMaxSize())
                is AlbumLoadState.Error -> ErrorState(state.message, Modifier.fillMaxSize())
                is AlbumLoadState.Ready -> AlbumBody(
                    album = state.album,
                    onTrackClick = onTrackClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun AlbumBody(
    album: Album,
    onTrackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ArtworkImage(
            url = album.artworkUrl600,
            contentDescription = album.name,
            shape = MaterialTheme.shapes.cover,
            shadowElevation = 24.dp,
            modifier = Modifier.size(120.dp),
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = album.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = album.artistName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.textTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )
        Spacer(Modifier.height(32.dp))
        TrackList(
            tracks = album.tracks,
            onTrackClick = onTrackClick,
            modifier = Modifier
                .fillMaxSize()
                .fadingEdges(),
        )
    }
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    onTrackClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(tracks, key = { it.trackId }) { track ->
            SongRow(
                title = track.name,
                artist = track.artistName,
                artworkUrl = track.artworkUrl100,
                onClick = { onTrackClick(track.trackId) },
                onMoreClick = {},
                sharedKey = "artwork-${track.trackId}",
            )
        }
    }
}

@Preview(widthDp = 414, heightDp = 896)
@Composable
private fun AlbumScreenReadyPreview() {
    val tracks = List(6) {
        Track(
            trackId = it.toLong(),
            name = "Song ${it + 1}",
            artistName = "Daft Punk",
            artistId = 1L,
            collectionId = 1L,
            collectionName = "Random Access Memories",
            artworkUrl100 = null,
            artworkUrl600 = null,
            previewUrl = null,
            trackTimeMillis = 210_000L,
            primaryGenreName = "Electronic",
            releaseDate = null,
            trackViewUrl = null,
        )
    }
    val album = Album(
        collectionId = 1L,
        name = "Random Access Memories",
        artistName = "Daft Punk",
        artistId = 1L,
        artworkUrl600 = null,
        releaseDate = null,
        trackCount = tracks.size,
        genre = "Electronic",
        tracks = tracks,
    )
    StemflowTheme {
        AlbumScreen(
            state = AlbumLoadState.Ready(album),
            onBack = {},
            onTrackClick = {},
        )
    }
}

@Preview(widthDp = 414, heightDp = 896)
@Composable
private fun AlbumScreenLoadingPreview() {
    StemflowTheme {
        AlbumScreen(
            state = AlbumLoadState.Loading,
            onBack = {},
            onTrackClick = {},
        )
    }
}
