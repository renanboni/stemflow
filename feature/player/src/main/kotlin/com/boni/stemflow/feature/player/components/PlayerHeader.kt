package com.boni.stemflow.feature.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.boni.stemflow.core.designsystem.R as DesignR
import com.boni.stemflow.core.designsystem.component.GlassIconButton
import com.boni.stemflow.core.designsystem.theme.StemflowTheme
import com.boni.stemflow.feature.player.R

@Composable
internal fun PlayerHeader(
    title: String,
    onBack: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            GlassIconButton(
                icon = painterResource(DesignR.drawable.ic_back),
                contentDescription = stringResource(DesignR.string.designsystem_back),
                onClick = onBack,
                modifier = Modifier.padding(start = 20.dp),
            )
        },
        actions = {
            GlassIconButton(
                icon = painterResource(DesignR.drawable.ic_more_menu),
                contentDescription = stringResource(R.string.player_more),
                onClick = onMore,
                modifier = Modifier.padding(end = 20.dp),
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        modifier = modifier,
    )
}

@Preview(widthDp = 414)
@Composable
private fun PlayerHeaderPreview() {
    StemflowTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.background),
        ) {
            PlayerHeader(
                title = "Random Access Memories",
                onBack = {},
                onMore = {},
            )
        }
    }
}
