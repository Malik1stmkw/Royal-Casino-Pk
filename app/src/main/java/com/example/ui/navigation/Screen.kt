package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Wallet : Screen("wallet")
    object Bonuses : Screen("bonuses")
    object Multiplayer : Screen("multiplayer")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object GameDetail : Screen("game/{gameId}") {
        fun createRoute(gameId: Int) = "game/$gameId"
    }
}
