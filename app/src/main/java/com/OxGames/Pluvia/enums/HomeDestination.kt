package com.OxGames.Pluvia.enums

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.graphics.vector.ImageVector
import com.OxGames.Pluvia.R

/**
 * Destinations for Home Screen
 */
enum class HomeDestination(@StringRes val string: Int, val icon: ImageVector) {
    Library(R.string.destination_library, Icons.AutoMirrored.Filled.ViewList),
    Downloads(R.string.destination_downloads, Icons.Filled.Download),
    Friends(R.string.destination_friends, Icons.Filled.Groups),
}
