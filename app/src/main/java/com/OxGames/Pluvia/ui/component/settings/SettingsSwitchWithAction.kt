package com.OxGames.Pluvia.ui.component.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.OxGames.Pluvia.ui.theme.PluviaTheme
import com.alorma.compose.settings.ui.SettingsGroup
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.base.internal.LocalSettingsGroupEnabled
import com.alorma.compose.settings.ui.base.internal.SettingsTileColors
import com.alorma.compose.settings.ui.base.internal.SettingsTileDefaults

@Composable
fun SettingsSwitchWithAction(
    state: Boolean,
    title: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    enabled: Boolean = LocalSettingsGroupEnabled.current,
    icon: @Composable (() -> Unit)? = null,
    subtitle: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    colors: SettingsTileColors = SettingsTileDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingsMenuLink(
        title = title,
        modifier = modifier,
        enabled = enabled,
        icon = icon,
        subtitle = subtitle,
        action = {
            Row {
                Switch(
                    enabled = enabled,
                    checked = state,
                    onCheckedChange = onCheckedChange,
                )
                action?.invoke()
            }
        },
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        onClick = { onCheckedChange(!state) },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Preview
@Composable
private fun Preview_SettingsSwitchWithAction() {
    PluviaTheme {
        SettingsGroup(title = { Text(text = "Test") }) {
            SettingsSwitchWithAction(
                state = true,
                onCheckedChange = {},
                title = { Text(text = "Boolean Field") },
            )
        }
    }
}
