package com.boni.stemflow.feature.player

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boni.stemflow.core.designsystem.component.ArtworkImage
import com.boni.stemflow.core.designsystem.component.ErrorState
import com.boni.stemflow.core.designsystem.component.Loading
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.core.ui.component.TrackOptionsSheet
import com.boni.stemflow.feature.player.components.PlaybackTime
import com.boni.stemflow.feature.player.components.PlayerControls
import com.boni.stemflow.feature.player.components.PlayerHeader
import com.boni.stemflow.feature.player.components.TrackInfo

private const val SKIP_INTERVAL_MS = 10_000L

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onBack: () -> Unit,
    onOpenAlbum: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val readyState = uiState as? PlayerUiState.Ready
    val track = readyState?.track
    val playback = readyState?.playback
    val playbackController = rememberPlayerPlaybackController()
    val noPreviewMessage = stringResource(R.string.player_error_no_preview)
    val errorMessage = when (val state = uiState) {
        PlayerUiState.Loading -> null
        is PlayerUiState.Error -> state.message
        is PlayerUiState.Ready -> state.playback.errorMessage
    }
    val isLoading = uiState == PlayerUiState.Loading || readyState?.playback?.isBuffering == true
    val onRetry = when {
        uiState is PlayerUiState.Error -> viewModel::retry
        readyState?.playback?.errorMessage != null && track != null && playbackController != null -> {
            {
                viewModel.onPlaybackRetry()
                playbackController.retry(track, noPreviewMessage, viewModel::onPlaybackError)
            }
        }
        else -> null
    }
    var showTrackOptions by remember { mutableStateOf(false) }

    DisposableEffect(track?.trackId) {
        val playedTrackId = track?.trackId
        onDispose {
            if (playedTrackId != null) viewModel.onTrackPlayed(playedTrackId)
        }
    }

    MediaPlayer(
        track = track,
        playback = playback,
        controller = playbackController,
        onBufferingChanged = viewModel::onPlaybackBufferingChanged,
        onDurationChanged = viewModel::onPlaybackDurationChanged,
        onPositionChanged = viewModel::onPlaybackPositionChanged,
        onError = viewModel::onPlaybackError,
        onConsumeSeek = viewModel::consumeSeek,
    )

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            PlayerHeader(
                title = track?.collectionName.orEmpty(),
                onBack = onBack,
                onMore = { if (track != null) showTrackOptions = true },
            )
        },
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
        ) {
            when {
                isLoading -> Loading(Modifier.fillMaxSize())
                errorMessage != null -> ErrorState(
                    message = errorMessage,
                    modifier = Modifier.fillMaxSize(),
                    onRetry = onRetry,
                )
                readyState != null -> PlayerBody(
                    track = readyState.track,
                    isPlaying = readyState.playback.isPlaying,
                    isRepeating = readyState.playback.repeatEnabled,
                    positionMs = readyState.playback.positionMs,
                    durationMs = readyState.playback.durationMs,
                    onTogglePlayPause = viewModel::togglePlayPause,
                    onToggleRepeat = viewModel::toggleRepeat,
                    onSkipBackward = { viewModel.seekBy(-SKIP_INTERVAL_MS) },
                    onSkipForward = { viewModel.seekBy(SKIP_INTERVAL_MS) },
                    onSeek = viewModel::seekTo,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    if (showTrackOptions && track != null) {
        TrackOptionsSheet(
            title = track.name,
            artist = track.artistName,
            onOpenAlbum = {
                showTrackOptions = false
                onOpenAlbum(track.collectionId)
            },
            onDismiss = { showTrackOptions = false },
        )
    }
}

@Composable
private fun PlayerBody(
    track: Track,
    isPlaying: Boolean,
    isRepeating: Boolean,
    positionMs: Long,
    durationMs: Long,
    onTogglePlayPause: () -> Unit,
    onToggleRepeat: () -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        ArtworkImage(
            url = track.artworkUrl600 ?: track.artworkUrl100,
            contentDescription = track.collectionName,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.size(264.dp),
            sharedKey = "artwork-${track.trackId}",
        )
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            TrackInfo(
                title = track.name,
                artist = track.artistName,
                isRepeating = isRepeating,
                onToggleRepeat = onToggleRepeat,
            )
            PlaybackTime(
                positionMs = positionMs,
                durationMs = durationMs,
                onSeek = onSeek,
            )
            PlayerControls(
                isPlaying = isPlaying,
                onTogglePlayPause = onTogglePlayPause,
                onSkipBackward = onSkipBackward,
                onSkipForward = onSkipForward,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Preview(widthDp = 414, heightDp = 896)
@Composable
private fun PlayerScreenReadyPreview() {
    val track = Track(
        trackId = 1L,
        name = "Get Lucky",
        artistName = "Daft Punk feat. Pharrell Williams",
        artistId = 1L,
        collectionId = 1L,
        collectionName = "Random Access Memories",
        artworkUrl100 = null,
        artworkUrl600 = null,
        previewUrl = null,
        trackTimeMillis = 260_000L,
        primaryGenreName = "Pop",
        releaseDate = null,
        trackViewUrl = null,
    )
    StemflowTheme {
        PlayerBody(
            track = track,
            isPlaying = true,
            isRepeating = false,
            positionMs = 86_000L,
            durationMs = 260_000L,
            onTogglePlayPause = {},
            onToggleRepeat = {},
            onSkipBackward = {},
            onSkipForward = {},
            onSeek = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
