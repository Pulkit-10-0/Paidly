package com.example.paidly.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.paidly.ui.screens.HistoryScreen
import com.example.paidly.ui.screens.HomeScreen
import com.example.paidly.ui.screens.NotificationSettingsScreen
import com.example.paidly.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val drawerItems = listOf("All", "To Pay", "To Receive", "History", "Notification Settings")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Paidly",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            when (item) {
                                "All" -> navController.navigate("home/ALL") { launchSingleTop = true }
                                "To Pay" -> navController.navigate("home/TO_PAY") { launchSingleTop = true }
                                "To Receive" -> navController.navigate("home/TO_RECEIVE") { launchSingleTop = true }
                                "History" -> navController.navigate(Screen.History.route) { launchSingleTop = true }
                                "Notification Settings" -> navController.navigate("notification_settings") { launchSingleTop = true }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "home/ALL") {
            composable("home/{filterType}") { backStackEntry ->
                val filterType = backStackEntry.arguments?.getString("filterType") ?: "ALL"
                HomeScreen(
                    viewModel = viewModel,
                    navController = navController,
                    filterType = filterType,
                    drawerState = drawerState
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    drawerState = drawerState
                )
            }

            composable("notification_settings") {
                NotificationSettingsScreen(
                    navController = navController,
                    drawerState = drawerState
                )
            }
        }
    }
}
