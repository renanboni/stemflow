package com.boni.stemflow.feature.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
internal fun PlayerControls(
    isPlaying: Boolean,
    onTogglePlayPause: () -> Unit,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onSkipBackward) {
            Icon(
                painter = painterResource(R.drawable.ic_backward),
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp),
            )
        }
        Spacer(Modifier.width(28.dp))
        GlassIconButton(
            icon = painterResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
            ),
            contentDescription = if (isPlaying) "Pause" else "Play",
            onClick = onTogglePlayPause,
            size = 72.dp,
            containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
        )
        Spacer(Modifier.width(28.dp))
        IconButton(onClick = onSkipForward) {
            Icon(
                painter = painterResource(R.drawable.ic_forward),
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp),
            )
        }
    }
}

@Preview(widthDp = 414)
@Composable
private fun PlayerControlsPlayingPreview() {
    StemflowTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
        ) {
            PlayerControls(
                isPlaying = true,
                onTogglePlayPause = {},
                onSkipBackward = {},
                onSkipForward = {},
            )
        }
    }
}

@Preview(widthDp = 414)
@Composable
private fun PlayerControlsPausedPreview() {
    StemflowTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
        ) {
            PlayerControls(
                isPlaying = false,
                onTogglePlayPause = {},
                onSkipBackward = {},
                onSkipForward = {},
            )
        }
    }
}
