package com.boni.stemflow.feature.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.boni.stemflow.core.designsystem.theme.StemflowTheme

@Composable
internal fun CoverArt(
    artworkUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = artworkUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .size(264.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White.copy(alpha = 0.05f)),
    )
}

@Preview(widthDp = 414, heightDp = 288)
@Composable
private fun CoverArtPreview() {
    StemflowTheme {
        Box(modifier = Modifier.background(Color.Black)) {
            CoverArt(artworkUrl = null, contentDescription = null)
        }
    }
}
