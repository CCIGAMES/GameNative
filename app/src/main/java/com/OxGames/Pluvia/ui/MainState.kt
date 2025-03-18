package com.OxGames.Pluvia.ui

import com.OxGames.Pluvia.enums.AppTheme
import com.OxGames.Pluvia.ui.screen.PluviaScreen
import com.materialkolor.PaletteStyle

data class MainState(
    val appTheme: AppTheme = AppTheme.NIGHT,
    val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    val resettedScreen: PluviaScreen? = null,
    val currentScreen: PluviaScreen = PluviaScreen.LoginUser,
    val hasLaunched: Boolean = false,
    val loadingDialogVisible: Boolean = false,
    val loadingDialogProgress: Float = 0F,
    val annoyingDialogShown: Boolean = false,
    val hasCrashedLastStart: Boolean = false,
    val isSteamConnected: Boolean = false,
    val launchedAppId: Int = 0,
    val bootToContainer: Boolean = false,
)
