package com.boni.stemflow.feature.player

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
internal fun MediaPlayer(
    track: Track?,
    playback: PlaybackUiState?,
    controller: PlayerPlaybackController?,
    onBufferingChanged: (Boolean) -> Unit,
    onDurationChanged: (Long) -> Unit,
    onPositionChanged: (Long) -> Unit,
    onError: (String?) -> Unit,
    onConsumeSeek: () -> Unit,
) {
    val playbackErrorMessage = stringResource(R.string.player_error_playback)
    val noPreviewMessage = stringResource(R.string.player_error_no_preview)

    if (controller == null) return

    DisposableEffect(controller) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                onBufferingChanged(playbackState == Player.STATE_BUFFERING)
                onDurationChanged(controller.durationMs.coerceAtLeast(0L))
            }

            override fun onPlayerError(error: PlaybackException) {
                onError(error.message ?: playbackErrorMessage)
            }
        }
        controller.addListener(listener)
        onDispose { controller.removeListener(listener) }
    }

    LaunchedEffect(controller, track?.trackId) {
        controller.prepare(track, noPreviewMessage, onError)
    }

    LaunchedEffect(controller, playback?.isPlaying) {
        controller.play(playback?.isPlaying == true)
    }

    LaunchedEffect(controller, playback?.repeatEnabled) {
        controller.repeat(playback?.repeatEnabled == true)
    }

    LaunchedEffect(controller, playback?.seekTarget) {
        val target = playback?.seekTarget ?: return@LaunchedEffect
        controller.seekTo(target)
        onConsumeSeek()
    }

    LaunchedEffect(controller) {
        while (true) {
            onPositionChanged(controller.positionMs)
            delay(POSITION_TICK_MS)
        }
    }
}

@Composable
internal fun rememberPlayerPlaybackController(): PlayerPlaybackController? {
    val context = LocalContext.current.applicationContext
    var controller by retain { mutableStateOf<PlayerPlaybackController?>(null) }

    DisposableEffect(Unit) {
        val token = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java),
        )
        val future = MediaController.Builder(context, token).buildAsync()
        future.addListener(
            {
                if (!future.isCancelled) {
                    controller = runCatching {
                        PlayerPlaybackController(future.get())
                    }.getOrNull()
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

@Stable
internal class PlayerPlaybackController internal constructor(
    private val controller: MediaController,
) {
    val durationMs: Long get() = controller.duration

    val positionMs: Long get() = controller.currentPosition

    fun prepare(
        track: Track?,
        noPreviewMessage: String,
        onError: (String?) -> Unit,
    ) {
        val url = track?.previewUrl
        if (url.isNullOrBlank()) {
            controller.stop()
            controller.clearMediaItems()
            if (track != null) onError(noPreviewMessage)
            return
        }

        controller.setMediaItem(track.toMediaItem(url))
        controller.prepare()
    }

    fun retry(
        track: Track,
        noPreviewMessage: String,
        onError: (String?) -> Unit,
    ) {
        prepare(track, noPreviewMessage, onError)
        controller.playWhenReady = true
    }

    fun play(isPlaying: Boolean) {
        controller.playWhenReady = isPlaying
    }

    fun repeat(isRepeating: Boolean) {
        controller.repeatMode = if (isRepeating) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }

    fun seekTo(positionMs: Long) {
        controller.seekTo(positionMs)
    }

    fun addListener(listener: Player.Listener) {
        controller.addListener(listener)
    }

    fun removeListener(listener: Player.Listener) {
        controller.removeListener(listener)
    }

    fun release() {
        controller.release()
    }
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
