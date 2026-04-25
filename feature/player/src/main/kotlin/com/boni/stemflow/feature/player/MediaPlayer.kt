package com.boni.stemflow.feature.player

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.boni.stemflow.core.domain.model.Track
import com.boni.stemflow.feature.player.service.PlaybackService
import kotlinx.coroutines.delay

@Composable
fun MediaPlayer(
    track: Track?,
    state: PlayerPlaybackState,
) {
    val context = LocalContext.current.applicationContext
    val playbackErrorMessage = stringResource(R.string.player_error_playback)
    val noPreviewMessage = stringResource(R.string.player_error_no_preview)

    val controller = rememberMediaController(context) ?: return

    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                state.onBufferingChanged(playbackState == Player.STATE_BUFFERING)
                state.onDurationChanged(controller.duration.coerceAtLeast(0L))
            }

            override fun onPlayerError(error: PlaybackException) {
                state.onError(error.message ?: playbackErrorMessage)
            }
        }
        controller.addListener(listener)
        onDispose { controller.removeListener(listener) }
    }

    LaunchedEffect(controller, track?.trackId) {
        val url = track?.previewUrl
        if (url.isNullOrBlank()) {
            controller.stop()
            controller.clearMediaItems()
            if (track != null) state.onError(noPreviewMessage)
            return@LaunchedEffect
        }
        controller.setMediaItem(track.toMediaItem(url))
        controller.prepare()
    }

    LaunchedEffect(controller, state.isPlaying) {
        controller.playWhenReady = state.isPlaying
    }

    LaunchedEffect(controller, state.repeatEnabled) {
        controller.repeatMode = if (state.repeatEnabled) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    LaunchedEffect(controller, state.seekTarget) {
        val target = state.seekTarget ?: return@LaunchedEffect
        controller.seekTo(target)
        state.consumeSeek()
    }

    LaunchedEffect(controller) {
        while (true) {
            state.onPositionChanged(controller.currentPosition)
            delay(POSITION_TICK_MS)
        }
    }
}

@Composable
private fun rememberMediaController(context: Context): MediaController? {
    var controller by retain { mutableStateOf<MediaController?>(null) }

    DisposableEffect(Unit) {
        val token = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java),
        )
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener(
            {
                if (!future.isCancelled) {
                    controller = runCatching { future.get() }.getOrNull()
                }
            },
            ContextCompat.getMainExecutor(context),
        )
        onDispose {
            controller?.release()
            controller = null
            MediaController.releaseFuture(future)
        }
    }

    return controller
}

private fun Track.toMediaItem(url: String): MediaItem {
    val metadataBuilder = MediaMetadata.Builder()
        .setTitle(name)
        .setArtist(artistName)
        .setAlbumTitle(collectionName)
    val artUri = (artworkUrl600 ?: artworkUrl100)?.takeIf { it.isNotBlank() }?.toUri()
    if (artUri != null) metadataBuilder.setArtworkUri(artUri)

    return MediaItem.Builder()
        .setUri(url)
        .setMediaId(trackId.toString())
        .setMediaMetadata(metadataBuilder.build())
        .build()
}

private const val POSITION_TICK_MS = 500L
