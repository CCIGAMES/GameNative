package com.OxGames.Pluvia.ui.component.dialog

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.OxGames.Pluvia.Constants
import com.OxGames.Pluvia.R
import com.OxGames.Pluvia.data.OwnedGames
import com.OxGames.Pluvia.ui.component.ListItemImage
import com.OxGames.Pluvia.ui.component.LoadingScreen
import com.OxGames.Pluvia.ui.theme.PluviaTheme
import com.OxGames.Pluvia.utils.SteamUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListDialog(
    visible: Boolean = true,
    list: List<OwnedGames>,
    onDismissRequest: () -> Unit,
) {
    if (visible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = false,
            ),
            content = {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(text = stringResource(R.string.games)) },
                            navigationIcon = {
                                IconButton(
                                    onClick = onDismissRequest,
                                    content = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.desc_close_dialog),
                                        )
                                    },
                                )
                            },
                        )
                    },
                ) { paddingValues ->
                    val uriHandler = LocalUriHandler.current

                    Box {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = WindowInsets.statusBars
                                        .asPaddingValues()
                                        .calculateTopPadding() + paddingValues.calculateTopPadding(),
                                    bottom = 24.dp + paddingValues.calculateBottomPadding(),
                                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                ),
                        ) {
                            itemsIndexed(items = list, key = { _, item -> item.appId }) { idx, item ->
                                ListItem(
                                    modifier = Modifier
                                        .animateItem()
                                        .clickable {
                                            uriHandler.openUri(Constants.Library.STORE_URL + item.appId)
                                        },
                                    colors = ListItemDefaults.colors(
                                        containerColor = Color.Transparent,
                                    ),
                                    headlineContent = { Text(text = item.name) },
                                    supportingContent = {
                                        Column {
                                            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall) {
                                                if (item.playtimeTwoWeeks > 10) {
                                                    val twoWeeks = SteamUtils.formatPlayTime(item.playtimeTwoWeeks)
                                                    Text(text = stringResource(R.string.games_played_two_weeks, twoWeeks))
                                                }
                                                val total = SteamUtils.formatPlayTime(item.playtimeForever)
                                                Text(text = stringResource(R.string.games_played_forever, total))
                                            }
                                        }
                                    },
                                    leadingContent = {
                                        ListItemImage(
                                            image = { "${Constants.Library.ICON_URL}${item.appId}/${item.imgIconUrl}.jpg" },
                                        )
                                    },
                                )

                                if (idx < list.lastIndex) {
                                    HorizontalDivider()
                                }
                            }
                        }

                        if (list.isEmpty()) {
                            LoadingScreen()
                        }
                    }
                }
            },
        )
    }
}

internal class GamesListPreview : PreviewParameterProvider<List<OwnedGames>> {
    override val values = sequenceOf(
        List(25) {
            OwnedGames(
                appId = it,
                name = "Game Name: $it",
                playtimeTwoWeeks = 210 * it,
                playtimeForever = 19154 * it,
                imgIconUrl = "",
                sortAs = "Game Name Alt: $it",
            )
        },
        emptyList(),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Preview
@Composable
private fun Preview_GamesListDialog(
    @PreviewParameter(GamesListPreview::class) list: List<OwnedGames>,
) {
    PluviaTheme {
        GamesListDialog(
            visible = true,
            list = list,
            onDismissRequest = { },
        )
    }
}
