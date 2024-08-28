package com.yavuzmobile.borsaanalizim.ui.navgraph

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yavuzmobile.borsaanalizim.ui.account.AccountScreen
import com.yavuzmobile.borsaanalizim.ui.balancesheet.BalanceSheetScreen
import com.yavuzmobile.borsaanalizim.ui.compare.CompareScreen
import com.yavuzmobile.borsaanalizim.ui.favorite.FavoriteScreen
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
        composable(NavigationItem.StocksCompareScreen.route) {
            StocksScreen(navController, true)
        }
        composable(
            NavigationItem.CompareScreen.route,
            arguments = listOf(
                navArgument("codes") {
                    type = NavType.StringArrayType
                    nullable = false
                },
            ),
        ) { currentBackStackEntry ->
            val codes: List<String> = currentBackStackEntry.arguments?.getStringArrayList("codes") ?: emptyList()
            CompareScreen(navController, codes)
        }
        composable(NavigationItem.FavoriteScreen.route) {
            FavoriteScreen(navController)
        }
        composable(NavigationItem.AccountScreen.route) {
            AccountScreen(navController)
        }
    }

}