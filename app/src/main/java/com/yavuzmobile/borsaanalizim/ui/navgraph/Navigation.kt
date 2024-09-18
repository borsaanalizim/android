package com.yavuzmobile.borsaanalizim.ui.navgraph

import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.yavuzmobile.borsaanalizim.model.StockFilter
import com.yavuzmobile.borsaanalizim.ui.account.AccountScreen
import com.yavuzmobile.borsaanalizim.ui.balancesheet.BalanceSheetScreen
import com.yavuzmobile.borsaanalizim.ui.comparestocks.CompareStocksScreen
import com.yavuzmobile.borsaanalizim.ui.comparestocksdetail.CompareStocksDetailScreen
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
            val code: String = currentBackStackEntry.arguments?.getString("code").orEmpty()
            BalanceSheetScreen(navController, code)
        }
        composable(NavigationItem.CompareStocksScreen.route) {
            CompareStocksScreen(navController)
        }
        composable(NavigationItem.CompareStocksDetailScreen.route) { currentBackStackEntry ->
            val stocksEncoded = currentBackStackEntry.arguments?.getString("stocks")
            val stocksDecoded = String(Base64.decode(stocksEncoded, Base64.DEFAULT))
            val stocks = Gson().fromJson(stocksDecoded, Array<StockFilter>::class.java).toList()
            CompareStocksDetailScreen(navController, stocks)
        }
        composable(NavigationItem.FavoriteScreen.route) {
            FavoriteScreen(navController)
        }
        composable(NavigationItem.AccountScreen.route) {
            AccountScreen(navController)
        }
    }

}