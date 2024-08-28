package com.yavuzmobile.borsaanalizim.ui.navgraph

sealed class NavigationItem(val route: String) {
    data object SplashScreen: NavigationItem("splash_screen")
    data object StocksScreen: NavigationItem("stocks_screen")
    data object StocksCompareScreen: NavigationItem("stocks_compare_screen")
    data object BalanceSheetScreen: NavigationItem("balance_sheet_screen/{code}") {
        fun createRoute(code: String) = "balance_sheet_screen/$code"
    }
    data object CompareScreen: NavigationItem("compare_screen/{codes}") {
        fun createRoute(codes: List<String>) = "compare_screen/$codes"
    }
    data object FavoriteScreen: NavigationItem("favorite_screen")
    data object AccountScreen: NavigationItem("account_screen")
}