package com.boni.stemflow.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.boni.stemflow.core.designsystem.animation.sharedArtwork
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.core.designsystem.theme.cover

@Composable
fun ArtworkImage(
    url: String?,
    contentDescription: String?,
    shape: Shape,
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 0.dp,
    sharedKey: Any? = null,
) {
    val placeholder = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
    val shared = if (sharedKey != null) modifier.sharedArtwork(sharedKey) else modifier
    val elevated = if (shadowElevation > 0.dp) {
        shared.shadow(elevation = shadowElevation, shape = shape)
    } else {
        shared
    }
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = elevated
            .clip(shape)
            .background(placeholder),
    )
}

@Preview
@Composable
private fun ArtworkImageLargePreview() {
    StemflowTheme {
        ArtworkImage(
            url = null,
            contentDescription = null,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.size(120.dp),
        )
    }
}

@Preview
@Composable
private fun ArtworkImageCoverPreview() {
    StemflowTheme {
        ArtworkImage(
            url = null,
            contentDescription = null,
            shape = MaterialTheme.shapes.cover,
            shadowElevation = 24.dp,
            modifier = Modifier.size(120.dp),
        )
    }
}
