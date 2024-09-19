package com.yavuzmobile.borsaanalizim.ui.navgraph

sealed class NavigationItem(val route: String) {
    data object SplashScreen: NavigationItem("splash_screen")
    data object StocksScreen: NavigationItem("stocks_screen")
    data object BalanceSheetScreen: NavigationItem("balance_sheet_screen/{code}") {
        fun createRoute(code: String) = "balance_sheet_screen/$code"
    }
    data object CompareStocksScreen: NavigationItem("compare_stocks_screen")
    data object CompareStocksDetailScreen: NavigationItem("compare_stocks_detail_screen")
    data object FavoriteScreen: NavigationItem("favorite_screen")
    data object AccountScreen: NavigationItem("account_screen")
}