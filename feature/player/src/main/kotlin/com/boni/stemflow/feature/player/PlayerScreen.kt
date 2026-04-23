package com.boni.stemflow.feature.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.feature.player.components.CoverArt
import com.boni.stemflow.feature.player.components.PlaybackTime
import com.boni.stemflow.feature.player.components.PlayerHeader
import com.boni.stemflow.feature.player.components.TrackInfo
import com.boni.stemflow.feature.player.components.PlayerControls

private const val SKIP_INTERVAL_MS = 10_000L

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val trackState by viewModel.state.collectAsStateWithLifecycle()
    val track = (trackState as? TrackLoadState.Ready)?.track
    val playback = rememberPlayerPlaybackState()

    LaunchedEffect(track?.trackId) {
        playback.onTrackLoaded(track)
        if (track != null) viewModel.onTrackPlayed(track.trackId)
    }

    MediaPlayer(
        track = track,
        state = playback
    )

    PlayerScreen(
        trackState = trackState,
        isPlaying = playback.isPlaying,
        isRepeating = playback.repeatEnabled,
        positionMs = playback.positionMs,
        durationMs = playback.durationMs,
        onBack = onBack,
        onTogglePlayPause = playback::togglePlayPause,
        onToggleRepeat = playback::toggleRepeat,
        onSkipBackward = { playback.seekBy(-SKIP_INTERVAL_MS) },
        onSkipForward = { playback.seekBy(SKIP_INTERVAL_MS) },
        onSeek = playback::seekTo,
        modifier = modifier,
    )
}

@Composable
internal fun PlayerScreen(
    trackState: TrackLoadState,
    isPlaying: Boolean,
    isRepeating: Boolean,
    positionMs: Long,
    durationMs: Long,
    onBack: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onToggleRepeat: () -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Black,
        topBar = {
            PlayerHeader(
                title = (trackState as? TrackLoadState.Ready)?.track?.collectionName.orEmpty(),
                onBack = onBack,
                onMore = {},
            )
        },
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
        ) {
            when (trackState) {
                TrackLoadState.Loading -> LoadingState(Modifier.fillMaxSize())
                is TrackLoadState.Error -> ErrorState(trackState.message, Modifier.fillMaxSize())
                is TrackLoadState.Ready -> PlayerBody(
                    track = trackState.track,
                    isPlaying = isPlaying,
                    isRepeating = isRepeating,
                    positionMs = positionMs,
                    durationMs = durationMs,
                    onTogglePlayPause = onTogglePlayPause,
                    onToggleRepeat = onToggleRepeat,
                    onSkipBackward = onSkipBackward,
                    onSkipForward = onSkipForward,
                    onSeek = onSeek,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
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
        CoverArt(
            artworkUrl = track.artworkUrl600 ?: track.artworkUrl100,
            contentDescription = track.collectionName,
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

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp,
        )
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
        PlayerScreen(
            trackState = TrackLoadState.Ready(track),
            isPlaying = true,
            isRepeating = false,
            positionMs = 86_000L,
            durationMs = 260_000L,
            onBack = {},
            onTogglePlayPause = {},
            onToggleRepeat = {},
            onSkipBackward = {},
            onSkipForward = {},
            onSeek = {},
        )
    }
}

@Preview(widthDp = 414, heightDp = 896)
@Composable
private fun PlayerScreenLoadingPreview() {
    StemflowTheme {
        PlayerScreen(
            trackState = TrackLoadState.Loading,
            isPlaying = false,
            isRepeating = false,
            positionMs = 0L,
            durationMs = 0L,
            onBack = {},
            onTogglePlayPause = {},
            onToggleRepeat = {},
            onSkipBackward = {},
            onSkipForward = {},
            onSeek = {},
        )
    }
}

@Preview(widthDp = 414, heightDp = 896)
@Composable
private fun PlayerScreenErrorPreview() {
    StemflowTheme {
        PlayerScreen(
            trackState = TrackLoadState.Error("Track not found"),
            isPlaying = false,
            isRepeating = false,
            positionMs = 0L,
            durationMs = 0L,
            onBack = {},
            onTogglePlayPause = {},
            onToggleRepeat = {},
            onSkipBackward = {},
            onSkipForward = {},
            onSeek = {},
        )
    }
}
