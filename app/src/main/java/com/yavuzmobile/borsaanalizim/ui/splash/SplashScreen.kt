package com.yavuzmobile.borsaanalizim.ui.splash

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yavuzmobile.borsaanalizim.ui.BaseScreen
import com.yavuzmobile.borsaanalizim.ui.navgraph.NavigationItem

@Composable
fun SplashScreen(navController: NavController, viewModel: SplashViewModel = hiltViewModel()) {

    val balanceSheetDatesUiState by viewModel.balanceSheetDatesUiState.collectAsState()
    val stocksUiState by viewModel.stocksUiState.collectAsState()
    val completedState by viewModel.completedUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBalanceSheetDates()
    }

    BaseScreen(navController = navController) {

        when {
            completedState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            completedState.data != null -> {
                if (completedState.data?.isCompletedBalanceSheetDates == true && completedState.data?.isCompletedStocks == true) {
                    navController.navigate(NavigationItem.StocksScreen.route)
                }
            }
        }

        when {
            balanceSheetDatesUiState.error != null -> {
                balanceSheetDatesUiState.error?.split("/n")?.forEach { error ->
                    if (error == "null") return@forEach
                    Text(text = "Hata -> $error", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
                }
            }
            balanceSheetDatesUiState.data != null -> {
                Text(text = "Hisse Fiyatları Yüklendi")
            }
        }
        when {
            stocksUiState.error != null -> {
                stocksUiState.error?.split("/n")?.forEach { error ->
                    if (error == "null") return@forEach
                    Text(text = "Hata -> $error", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
                }
            }
            stocksUiState.data != null -> {
                Text(text = "Hisseler Yüklendi")
            }
        }
    }
}