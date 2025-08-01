package com.OxGames.Pluvia.ui.component.settings

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.OxGames.Pluvia.R
import com.OxGames.Pluvia.ui.theme.PluviaTheme
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.base.internal.LocalSettingsGroupEnabled
import com.alorma.compose.settings.ui.base.internal.SettingsTileColors
import com.alorma.compose.settings.ui.base.internal.SettingsTileDefaults
import com.alorma.compose.settings.ui.base.internal.SettingsTileScaffold

@Composable
fun SettingsListDropdown(
    modifier: Modifier = Modifier,
    enabled: Boolean = LocalSettingsGroupEnabled.current,
    value: Int,
    items: List<String>,
    fallbackDisplay: String = "",
    onItemSelected: (Int) -> Unit,
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    colors: SettingsTileColors = SettingsTileDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    action: @Composable (() -> Unit)? = null,
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    SettingsTileScaffold(
        modifier = Modifier
            .clickable(
                enabled = enabled,
                onClick = { isDropdownExpanded = true },
            )
            .then(modifier),
        enabled = enabled,
        title = title,
        subtitle = subtitle,
        icon = icon,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
    ) {
        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false },
        ) {
            items.forEachIndexed { index, text ->
                DropdownMenuItem(
                    enabled = enabled,
                    text = { Text(text = text) },
                    onClick = {
                        onItemSelected(index)
                        isDropdownExpanded = false
                    },
                )
            }
        }

        Row {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth(0.2f),
                text = if (value >= 0 && value < items.size) items[value] else fallbackDisplay,
                style = TextStyle(
                    fontSize = 16.sp,
                    textAlign = TextAlign.End,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier.width(16.dp))
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = if (isDropdownExpanded) {
                    Icons.Filled.ArrowDropUp
                } else {
                    Icons.Filled.ArrowDropDown
                },
                contentDescription = stringResource(R.string.desc_dropdown_arrow),
            )
            if (action != null) {
                Spacer(modifier.width(16.dp))
                action()
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Preview
@Composable
private fun Preview_SettingsListDropdown() {
    PluviaTheme {
        SettingsGroup(title = { Text(text = "Test") }) {
            SettingsListDropdown(
                value = 2,
                items = listOf("One", "Two", "Three", "Four"),
                title = { Text(text = "Text Field") },
                onItemSelected = {},
            )
        }
    }
}
