package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.AppDatabase
import com.example.data.local.CasinoRepository
import com.example.ui.components.CasinoBottomNavBar
import com.example.ui.components.CasinoTopAppBar
import com.example.ui.games.AviatorGameScreen
import com.example.ui.games.BaccaratGameScreen
import com.example.ui.games.BlackjackGameScreen
import com.example.ui.games.CoinFlipGameScreen
import com.example.ui.games.DiceGameScreen
import com.example.ui.games.DragonTigerGameScreen
import com.example.ui.games.HighLowGameScreen
import com.example.ui.games.KenoGameScreen
import com.example.ui.games.MinesGameScreen
import com.example.ui.games.PlinkoGameScreen
import com.example.ui.games.RouletteGameScreen
import com.example.ui.games.ScratchCardGameScreen
import com.example.ui.games.SlotsGameScreen
import com.example.ui.games.TeenPattiGameScreen
import com.example.ui.games.VideoPokerGameScreen
import com.example.ui.games.WheelOfFortuneGameScreen
import com.example.ui.navigation.Screen
import com.example.ui.screens.BonusScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MultiplayerScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.RoyalCasinoTheme
import com.example.viewmodel.CasinoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RoyalCasinoTheme {
                val casinoViewModel: CasinoViewModel = viewModel()
                CasinoMainApp(viewModel = casinoViewModel)
            }
        }
    }
}

@Composable
fun CasinoMainApp(viewModel: CasinoViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val userProfile by viewModel.userProfile.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifications = notifications.count { !it.isRead }

    val isGameScreen = currentRoute?.startsWith("game/") == true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isGameScreen) {
                CasinoTopAppBar(
                    balance = userProfile?.balance ?: 10000.0,
                    unreadNotificationsCount = unreadNotifications,
                    onWalletClick = {
                        navController.navigate(Screen.Wallet.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNotificationsClick = {
                        navController.navigate(Screen.Notifications.route)
                    }
                )
            }
        },
        bottomBar = {
            if (!isGameScreen) {
                CasinoBottomNavBar(
                    currentRoute = currentRoute ?: Screen.Home.route,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onGameSelect = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    },
                    onBonusClick = {
                        navController.navigate(Screen.Bonuses.route)
                    }
                )
            }

            composable(Screen.Multiplayer.route) {
                MultiplayerScreen(
                    viewModel = viewModel,
                    onGameSelect = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    }
                )
            }

            composable(Screen.Wallet.route) {
                WalletScreen(viewModel = viewModel)
            }

            composable(Screen.Bonuses.route) {
                BonusScreen(viewModel = viewModel)
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen(viewModel = viewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = viewModel)
            }

            composable(
                route = Screen.GameDetail.route,
                arguments = listOf(navArgument("gameId") { type = NavType.IntType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getInt("gameId") ?: 1
                val onBack = { navController.popBackStack(); Unit }

                when (gameId) {
                    1 -> SlotsGameScreen(viewModel = viewModel, onBack = onBack)
                    2 -> RouletteGameScreen(viewModel = viewModel, onBack = onBack)
                    3 -> BlackjackGameScreen(viewModel = viewModel, onBack = onBack)
                    4 -> BaccaratGameScreen(viewModel = viewModel, onBack = onBack)
                    5 -> CoinFlipGameScreen(viewModel = viewModel, onBack = onBack)
                    6 -> MinesGameScreen(viewModel = viewModel, onBack = onBack)
                    7 -> AviatorGameScreen(viewModel = viewModel, onBack = onBack)
                    8 -> DragonTigerGameScreen(viewModel = viewModel, onBack = onBack)
                    9 -> TeenPattiGameScreen(viewModel = viewModel, onBack = onBack)
                    10 -> WheelOfFortuneGameScreen(viewModel = viewModel, onBack = onBack)
                    11 -> DiceGameScreen(viewModel = viewModel, onBack = onBack)
                    12 -> PlinkoGameScreen(viewModel = viewModel, onBack = onBack)
                    13 -> ScratchCardGameScreen(viewModel = viewModel, onBack = onBack)
                    14 -> HighLowGameScreen(viewModel = viewModel, onBack = onBack)
                    15 -> KenoGameScreen(viewModel = viewModel, onBack = onBack)
                    16 -> VideoPokerGameScreen(viewModel = viewModel, onBack = onBack)
                    else -> SlotsGameScreen(viewModel = viewModel, onBack = onBack)
                }
            }
        }
    }
}
