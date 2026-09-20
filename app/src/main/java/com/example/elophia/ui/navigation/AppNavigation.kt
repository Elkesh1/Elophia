package com.example.elophia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elophia.ui.auth.AuthEntryScreen
import com.example.elophia.ui.auth.EmailSignInScreen
import com.example.elophia.ui.auth.EmailSignUpScreen
import com.example.elophia.ui.auth.PhoneAuthScreen
import com.example.elophia.ui.product.AddProductScreen
import com.example.elophia.ui.product.EditProductScreen
import com.example.elophia.ui.shop.CreateShopScreen
import com.example.elophia.ui.shop.MyShopScreen
import com.example.elophia.ui.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth

object Routes {
    const val SPLASH = "splash"

    const val AUTH_ENTRY = "auth_entry"
    const val EMAIL_SIGN_UP = "email_sign_up"
    const val EMAIL_SIGN_IN = "email_sign_in"
    const val PHONE_AUTH = "phone_auth"

    const val SIGN_UP = "sign_up"
    const val SIGN_IN = "sign_in"

    const val HOME = "home"
    const val EXPLORE = "explore"
    const val SHOP = "shop"
    const val YOU = "you"

    const val CREATE_SHOP = "create_shop"
    const val MY_SHOP = "my_shop"
    const val ADD_PRODUCT = "add_product"
    const val EDIT_PRODUCT = "edit_product"
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val startDestination = remember {
        if (auth.currentUser != null) Routes.HOME else Routes.SIGN_UP
    }

    NavHost (
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Routes.AUTH_ENTRY) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.AUTH_ENTRY) {
            AuthEntryScreen(
                onGoogleSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH_ENTRY) { inclusive = true }
                    }
                },
                onPhoneClick = {
                    navController.navigate(Routes.PHONE_AUTH)
                },
                onEmailClick = {
                    navController.navigate(Routes.EMAIL_SIGN_IN)
                }
            )
        }

        composable(Routes.PHONE_AUTH) {
            PhoneAuthScreen(
                onVerified = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH_ENTRY) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.EMAIL_SIGN_IN) {
            EmailSignInScreen(
                onSignInSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH_ENTRY) { inclusive = true}
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onSignUpClick = {
                    navController.navigate(Routes.EMAIL_SIGN_UP)
                }
            )
        }

        composable(Routes.EMAIL_SIGN_UP) {
            EmailSignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH_ENTRY) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onSignInClick = {
                    navController.navigate(Routes.EMAIL_SIGN_IN) {
                        popUpTo(Routes.EMAIL_SIGN_IN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {

            MainScaffold(

                onCreateShopClick = {
                    navController.navigate(Routes.CREATE_SHOP)
                },

                onManageShopClick = { shopId ->
                    navController.navigate(
                        "${Routes.MY_SHOP}/$shopId"
                    )
                },
                onSignOut = {
                    navController.navigate(Routes.AUTH_ENTRY) {
                        popUpTo(0) { inclusive = true}
                    }
                }
            )
        }

        composable(Routes.CREATE_SHOP) {

            CreateShopScreen(
                onShopCreated = { shopId ->
                    navController.navigate(
                        "${Routes.ADD_PRODUCT}/$shopId"
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "${Routes.MY_SHOP}/{shopId}"
        ) { backStackEntry ->

            val shopId = backStackEntry.arguments
                ?.getString("shopId")
                ?: return@composable

            MyShopScreen(
                shopId = shopId,

                onAddProductClick = {
                    navController.navigate(
                        "${Routes.ADD_PRODUCT}/$shopId"
                    )
                },

                onEditProductClick = { productId ->
                    navController.navigate(
                        "${Routes.EDIT_PRODUCT}/$productId"
                    )
                },

                onDeleteProduct = { productId ->
                    println("Delete clicked for product: $productId")
                }
            )
        }

        composable(
            route = "${Routes.EDIT_PRODUCT}/{productId}"
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable

            EditProductScreen(
                productId = productId,
                onProductUpdated = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${Routes.ADD_PRODUCT}/{shopId}"
        ) { backStackEntry ->

            val shopId = backStackEntry.arguments
                ?.getString("shopId")
                ?: return@composable

            AddProductScreen(
                shopId = shopId,

                onProductAdded = {
                    navController.popBackStack()
                }
            )
        }
    }
}