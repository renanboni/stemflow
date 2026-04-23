package com.boni.stemflow.feature.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.boni.stemflow.core.designsystem.R
import com.boni.stemflow.core.designsystem.theme.StemflowTheme

@Composable
internal fun TrackInfo(
    title: String,
    artist: String,
    isRepeating: Boolean,
    onToggleRepeat: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 38.sp,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = artist,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f, fill = false),
            )
            Icon(
                painter = painterResource(R.drawable.ic_play_on_repeat),
                contentDescription = if (isRepeating) "Disable repeat" else "Enable repeat",
                tint = Color.White.copy(alpha = if (isRepeating) 1f else 0.4f),
                modifier = Modifier
                    .size(24.dp)
                    .clickable(role = Role.Button, onClick = onToggleRepeat),
            )
        }
    }
}

@Preview(widthDp = 414)
@Composable
private fun TrackInfoPreview() {
    StemflowTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(24.dp),
        ) {
            TrackInfo(
                title = "Get Lucky",
                artist = "Daft Punk feat. Pharrell Williams",
                isRepeating = false,
                onToggleRepeat = {},
            )
        }
    }
}

@Preview(widthDp = 414)
@Composable
private fun TrackInfoRepeatingPreview() {
    StemflowTheme {
        Box(
            modifier = Modifier
                .background(Color.Black)
                .padding(24.dp),
        ) {
            TrackInfo(
                title = "Get Lucky",
                artist = "Daft Punk feat. Pharrell Williams",
                isRepeating = true,
                onToggleRepeat = {},
            )
        }
    }
}
