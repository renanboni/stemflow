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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.boni.stemflow.core.designsystem.R as DesignR
import com.boni.stemflow.core.designsystem.component.GlassIconButton
import com.boni.stemflow.core.designsystem.modifier.bounceClickable
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.feature.player.R

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
        Box(
            modifier = Modifier
                .size(48.dp)
                .bounceClickable(onClick = onSkipBackward),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_backward),
                contentDescription = stringResource(R.string.player_previous),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(36.dp),
            )
        }
        Spacer(Modifier.width(28.dp))
        GlassIconButton(
            icon = painterResource(
                if (isPlaying) DesignR.drawable.ic_pause else DesignR.drawable.ic_play,
            ),
            contentDescription = stringResource(
                if (isPlaying) R.string.player_pause else R.string.player_play,
            ),
            onClick = onTogglePlayPause,
            size = 72.dp,
            containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
        )
        Spacer(Modifier.width(28.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .bounceClickable(onClick = onSkipForward),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_forward),
                contentDescription = stringResource(R.string.player_next),
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
