package com.yavuzmobile.borsaanalizim.ui.navgraph

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yavuzmobile.borsaanalizim.ui.account.AccountScreen
import com.yavuzmobile.borsaanalizim.ui.balancesheet.BalanceSheetScreen
import com.yavuzmobile.borsaanalizim.ui.comparestocks.CompareStocksScreen
import com.yavuzmobile.borsaanalizim.ui.comparestocks.CompareStocksViewModel
import com.yavuzmobile.borsaanalizim.ui.comparestocksdetail.CompareStocksDetailScreen
import com.yavuzmobile.borsaanalizim.ui.favorite.FavoriteScreen
import com.yavuzmobile.borsaanalizim.ui.splash.SplashScreen
import com.yavuzmobile.borsaanalizim.ui.stock.StocksScreen

@Composable
fun Navigation() {

    val navController = rememberNavController()
    val compareStocksViewModel: CompareStocksViewModel = hiltViewModel()

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
            val code: String = currentBackStackEntry.arguments?.getString("code").orEmpty()
            BalanceSheetScreen(navController, code)
        }
        composable(NavigationItem.CompareStocksScreen.route) {
            CompareStocksScreen(navController, compareStocksViewModel)
        }
        composable(NavigationItem.CompareStocksDetailScreen.route) {
            CompareStocksDetailScreen(navController, compareStocksViewModel)
        }
        composable(NavigationItem.FavoriteScreen.route) {
            FavoriteScreen(navController)
        }
        composable(NavigationItem.AccountScreen.route) {
            AccountScreen(navController)
        }
    }

}