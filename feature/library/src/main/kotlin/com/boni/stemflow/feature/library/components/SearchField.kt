package com.boni.stemflow.feature.library.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.boni.stemflow.core.designsystem.R
import com.boni.stemflow.core.designsystem.theme.StemflowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fill = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
    Surface(color = MaterialTheme.colorScheme.surface, modifier = modifier) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = fill,
                unfocusedContainerColor = fill,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
        )
    }
}

@Preview
@Composable
private fun SearchFieldEmptyPreview() {
    StemflowTheme {
        SearchField(query = "", onQueryChange = {})
    }
}

@Preview
@Composable
private fun SearchFieldTypedPreview() {
    StemflowTheme {
        SearchField(query = "guns n roses", onQueryChange = {})
    }
}
