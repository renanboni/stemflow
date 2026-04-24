package com.boni.stemflow.feature.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.feature.player.R
import kotlinx.coroutines.delay

/**
 * Composable that owns an [ExoPlayer] tied to its composition. Drive playback declaratively by
 * mutating [state] (via the [PlayerPlaybackState] intents or by setting fields from the caller's
 * UI); this composable writes observed transport events back into the same holder. Player stops
 * and releases when this composable leaves composition.
 */
@Composable
fun MediaPlayer(
    track: Track?,
    state: PlayerPlaybackState,
) {
    val applicationContext = LocalContext.current.applicationContext
    val playbackErrorMessage = stringResource(R.string.player_error_playback)
    val noPreviewMessage = stringResource(R.string.player_error_no_preview)
    val exoPlayer = retain { ExoPlayer.Builder(applicationContext).build() }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                state.onBufferingChanged(playbackState == Player.STATE_BUFFERING)
                state.onDurationChanged(exoPlayer.duration.coerceAtLeast(0L))
            }

            override fun onPlayerError(error: PlaybackException) {
                state.onError(error.message ?: playbackErrorMessage)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            runCatching { exoPlayer.stop() }
            runCatching { exoPlayer.release() }
        }
    }

    LaunchedEffect(track?.trackId) {
        val url = track?.previewUrl
        if (url.isNullOrBlank()) {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            if (track != null) state.onError(noPreviewMessage)
            return@LaunchedEffect
        }
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
    }

    LaunchedEffect(state.isPlaying) {
        exoPlayer.playWhenReady = state.isPlaying
    }

    LaunchedEffect(state.repeatEnabled) {
        exoPlayer.repeatMode = if (state.repeatEnabled) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    LaunchedEffect(state.seekTarget) {
        val target = state.seekTarget ?: return@LaunchedEffect
        exoPlayer.seekTo(target)
        state.consumeSeek()
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            state.onPositionChanged(exoPlayer.currentPosition)
            delay(POSITION_TICK_MS)
        }
    }
}

private const val POSITION_TICK_MS = 500L
