package com.boni.stemflow.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.boni.stemflow.core.designsystem.component.Thumbnail
import com.boni.stemflow.core.designsystem.modifier.bounceClickable
import com.boni.stemflow.core.designsystem.theme.elementTertiary
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.core.designsystem.theme.textTertiary
import com.boni.stemflow.core.designsystem.R as DesignSystemR
import com.boni.stemflow.core.ui.R

@Composable
fun SongRow(
    title: String,
    artist: String,
    artworkUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMoreClick: (() -> Unit)? = null,
    sharedKey: Any? = null,
) {
    Row(
        modifier = modifier
            .bounceClickable(onClick = onClick)
            .padding(start = 24.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Thumbnail(url = artworkUrl, contentDescription = null, sharedKey = sharedKey)
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .semantics(mergeDescendants = true) {},
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (onMoreClick != null) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .bounceClickable(onClick = onMoreClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_more_menu),
                    contentDescription = stringResource(R.string.core_ui_more_options_for, title),
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.elementTertiary,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SongRowPreview() {
    StemflowTheme {
        SongRow(
            title = "Song Title",
            artist = "Artist Name",
            artworkUrl = null,
            onClick = {},
        )
    }
}
