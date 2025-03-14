package com.OxGames.Pluvia.ui.screen.library.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.OxGames.Pluvia.R
import com.OxGames.Pluvia.data.LibraryItem
import com.OxGames.Pluvia.ui.component.ListItemImage
import com.OxGames.Pluvia.ui.internal.fakeAppInfo
import com.OxGames.Pluvia.ui.theme.PluviaTheme

@Composable
internal fun AppItem(
    modifier: Modifier = Modifier,
    appInfo: LibraryItem,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = modifier.clickable { onClick() },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        headlineContent = { Text(text = appInfo.name) },
        supportingContent = if (appInfo.isShared) {
            { Text(text = stringResource(R.string.friend_shared_game), fontStyle = FontStyle.Italic, fontSize = 12.sp) }
        } else {
            null
        },
        leadingContent = {
            ListItemImage(
                image = { appInfo.clientIconUrl },
                contentDescription = null, // TODO
            )
        },
    )
}

/***********
 * PREVIEW *
 ***********/

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Preview
@Composable
private fun Preview_AppItem() {
    PluviaTheme {
        Surface {
            LazyColumn {
                items(
                    items = List(5) { idx ->
                        val item = fakeAppInfo(idx)
                        LibraryItem(
                            index = idx,
                            appId = item.id,
                            name = item.name,
                            iconHash = item.iconHash,
                            isShared = idx % 2 == 0,
                        )
                    },
                    itemContent = {
                        AppItem(appInfo = it, onClick = {})
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    },
                )
            }
        }
    }
}
