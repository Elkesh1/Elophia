package com.example.elophia.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elophia.ui.home.HomeScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.elophia.ui.explore.ExploreScreen
import com.example.elophia.ui.shop.ShopScreen
import com.example.elophia.viewmodel.ShopViewModel


data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)


val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.HOME,
        label = "Home",
        icon = Icons.Default.Home
    ),
    BottomNavItem(
        route = Routes.EXPLORE,
        label = "Explore",
        icon = Icons.Default.Search
    ),
    BottomNavItem(
        route = Routes.SHOP,
        label = "Shop",
        icon = Icons.Default.Store
    ),
    BottomNavItem(
        route = Routes.YOU,
        label = "You",
        icon = Icons.Default.Person
    )
)
@Composable
fun MainScaffold(
    onCreateShopClick: () -> Unit,
    onManageShopClick: (String) -> Unit,
    onSignOut: () -> Unit
) {

    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val shopViewModel: ShopViewModel = viewModel()
    val shopState by shopViewModel.shopState.collectAsState()

    LaunchedEffect(Unit) {
        shopViewModel.checkMyShop()
    }

    Scaffold(
        bottomBar = {

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == item.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            // Unselected = navy (visible on cream)
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),

                            // Selected = gold
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,

                            // Soft gold pill behind selected icon
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = bottomNavController,
            startDestination = Routes.HOME
        ) {

            composable(Routes.HOME) {

                HomeScreen()
            }

            composable(Routes.EXPLORE) {
                ExploreScreen()
            }

            composable(Routes.SHOP) {
                ShopScreen(
                    shopState = shopState,
                    onCreateShopClick = { onCreateShopClick() },
                    onManageShopClick = { shopId -> onManageShopClick(shopId) },
                    shopViewModel = shopViewModel
                )
            }

            composable(Routes.YOU) {
                com.example.elophia.ui.you.YouScreenTemp(
                    onSignOutClick = {
                        // Sign out and go back to AuthEntry
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                        // Trigger navigation to auth from parent nav controller
                        onSignOut()
                    }
                )
            }
        }
    }
}