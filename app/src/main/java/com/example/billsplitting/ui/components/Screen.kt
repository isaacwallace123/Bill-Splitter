package com.example.billsplitting.ui.components

import androidx.annotation.DrawableRes
import com.example.billsplitting.R

sealed class Screen(val route: String, @DrawableRes val icon: Int, val label: String) {
    object Home : Screen("home", R.drawable.ic_home, "Home")
    object Bills : Screen("bills", R.drawable.ic_money, "Bills")
    object Map : Screen("map", R.drawable.ic_map, "Map")
    object Settings : Screen("settings", R.drawable.ic_settings, "Settings")
}