package com.yavuzmobile.borsaanalizim.ui.navgraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yavuzmobile.borsaanalizim.ui.balancesheet.BalanceSheetScreen
import com.yavuzmobile.borsaanalizim.ui.splash.SplashScreen
import com.yavuzmobile.borsaanalizim.ui.stock.StocksScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationItem.SplashScreen.route
    ) {
        composable(NavigationItem.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(NavigationItem.StocksScreen.route) {
            StocksScreen(navController)
        }
        composable(
            NavigationItem.BalanceSheetScreen.route,
            arguments = listOf(
                navArgument("code") {
                    type = NavType.StringType
                    nullable = false
                },
            ),
        ) { currentBackStackEntry ->
            val code: String = currentBackStackEntry.arguments?.getString("code") ?: ""
            BalanceSheetScreen(navController, code)
        }
    }

}