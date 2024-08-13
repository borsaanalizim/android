package com.yavuzmobile.borsaanalizim.ui.stock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.yavuzmobile.borsaanalizim.ui.BaseScreen
import com.yavuzmobile.borsaanalizim.ui.navgraph.NavigationItem

@Composable
fun StocksScreen(
    navController: NavController,
    viewModel: StocksViewModel = hiltViewModel()
) {

    val stocksUiState by viewModel.stocksUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onSearchQueryChange("")
        viewModel.fetchStocks()
    }

    BaseScreen(navController = navController) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            label = { Text("Hisse Ara") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.refreshStocks() }
        ) {
            when (stocksUiState.isLoading) {
                true -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                false -> {
                    if (stocksUiState.error != null) {
                        Text(
                            text = "Hata: ${stocksUiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        return@SwipeRefresh
                    }
                    LazyColumn {
                        items(stocksUiState.data?.filteredStocks?.sorted() ?: emptyList()) { stock ->
                            Text(
                                text = stock,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .clickable {
                                        navController.navigate(NavigationItem.BalanceSheetScreen.createRoute(stock))
                                    },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

}