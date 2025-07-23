package com.example.paidly.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object History : Screen("history")
}
