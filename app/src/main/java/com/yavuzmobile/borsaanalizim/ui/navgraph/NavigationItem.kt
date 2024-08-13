package com.yavuzmobile.borsaanalizim.ui.navgraph

sealed class NavigationItem(val route: String) {
    data object SplashScreen: NavigationItem("splash_screen")
    data object StocksScreen: NavigationItem("stocks_screen")
    data object BalanceSheetScreen: NavigationItem("balance_sheet_screen/{code}") {
        fun createRoute(code: String) = "balance_sheet_screen/$code"
    }
}