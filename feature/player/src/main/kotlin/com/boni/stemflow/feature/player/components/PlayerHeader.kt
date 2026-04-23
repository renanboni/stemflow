package com.boni.stemflow.feature.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.boni.stemflow.core.designsystem.R
import com.boni.stemflow.core.designsystem.component.GlassIconButton
import com.boni.stemflow.core.designsystem.theme.StemflowTheme

@OptIn(ExperimentalMaterial3Api::class)
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        },
        navigationIcon = {
            GlassIconButton(
                icon = painterResource(R.drawable.ic_back),
                contentDescription = "Back",
                onClick = onBack,
                modifier = Modifier.padding(start = 20.dp),
            )
        },
        actions = {
            GlassIconButton(
                icon = painterResource(R.drawable.ic_more_menu),
                contentDescription = "More",
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
