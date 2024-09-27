package com.yavuzmobile.borsaanalizim.ui.splash

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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

    val completedState by viewModel.completedUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBalanceSheetDates()
    }

    BaseScreen(navController = navController, onClickAction = {}) {
        Column(Modifier.fillMaxSize()) {
            when (completedState.isLoading){
                true -> CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally))
                else -> {
                    completedState.error?.let {
                        Text("Hata: $it")
                    }
                    completedState.data?.let {
                        if (it.isCompletedBalanceSheetDates) {
                            Text("Bilanço tarih verileri indirildi")
                        }
                        if (it.isCompletedStocks) {
                            Text("Şirketlerin verileri indirildi")
                        }
                        if (it.isCompletedBalanceSheetDates && it.isCompletedStocks) {
                            Text("Tüm Veriler İndirildi")
                            navController.navigate(NavigationItem.StocksScreen.route)
                        }
                    }
                }
            }
        }
    }
}