package com.baz1s.cryptorrsacompose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavDrawerItem(val route: String, var icon: ImageVector, var title: String) {
    data object Cryptor : NavDrawerItem ("cryptor", Icons.Filled.Home, "Cryptor")
    data object Keygen: NavDrawerItem("keygen", Icons.Filled.Build, "KeyGen")
}