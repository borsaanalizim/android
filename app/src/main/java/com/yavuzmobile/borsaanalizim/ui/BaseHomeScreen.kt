package com.yavuzmobile.borsaanalizim.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.yavuzmobile.borsaanalizim.R
import com.yavuzmobile.borsaanalizim.ui.navgraph.BottomBarModel
import com.yavuzmobile.borsaanalizim.ui.navgraph.NavigationItem

@Composable
fun BaseHomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    floatActionButton: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {

    val bottomBars = listOf(
        BottomBarModel(
            NavigationItem.StocksScreen,
            ImageVector.vectorResource(R.drawable.ic_balance_sheet)
        ),
        BottomBarModel(
            NavigationItem.StocksCompareScreen,
            ImageVector.vectorResource(R.drawable.ic_compare)
        ),
        BottomBarModel(
            NavigationItem.FavoriteScreen,
            ImageVector.vectorResource(R.drawable.ic_favorite)
        ),
        BottomBarModel(
            NavigationItem.AccountScreen,
            ImageVector.vectorResource(R.drawable.ic_account)
        ),
    )

    Scaffold(
        floatingActionButton = floatActionButton,
        bottomBar = {
            NavigationBar {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomBars.forEach { bottomBarModel ->
                    Log.i("CURRENT_DESTINATION", currentDestination.toString())
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == bottomBarModel.screen.route } == true,
                        icon = {
                            Icon(
                                bottomBarModel.icon, "",
                                Modifier
                                    .width(24.dp)
                                    .height(24.dp)
                            )
                        },
                        onClick = {
                            navController.navigate(bottomBarModel.screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            Log.i("CURRENT_DESTINATION_2", currentDestination.toString())
                        },
                    )
                }
            }
        },
    ) {
        Column(modifier.padding(it)) {
            content(this)
        }
    }

}