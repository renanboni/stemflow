package com.boni.stemflow.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.boni.stemflow.core.designsystem.animation.sharedArtwork
import com.boni.stemflow.core.designsystem.theme.StemflowTheme

@Composable
fun Thumbnail(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    sharedKey: Any? = null,
) {
    val sized = modifier.size(size)
    val shared = if (sharedKey != null) sized.sharedArtwork(sharedKey) else sized
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = shared
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

@Preview
@Composable
private fun ThumbnailPreview() {
    StemflowTheme {
        Thumbnail(url = null, contentDescription = null)
    }
}
