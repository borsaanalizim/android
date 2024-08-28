package com.yavuzmobile.borsaanalizim.ui.stock

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.yavuzmobile.borsaanalizim.R
import com.yavuzmobile.borsaanalizim.ui.BaseHomeScreen
import com.yavuzmobile.borsaanalizim.ui.navgraph.NavigationItem

@Composable
fun StocksScreen(
    navController: NavController,
    isMultiple: Boolean = false,
    viewModel: StocksViewModel = hiltViewModel()
) {

    val stocksUiState by viewModel.stocksUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val selectedItems = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        selectedItems.clear()
        viewModel.onSearchQueryChange("")
        viewModel.fetchStocks()
    }

    BaseHomeScreen(
        navController = navController,
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (isMultiple) {
                OutlinedButton(modifier = Modifier.align(Alignment.CenterEnd), onClick = { navController.navigate(NavigationItem.CompareScreen.createRoute(selectedItems.toList())) }, enabled = selectedItems.size > 1) {
                    Text("Devam")
                }
            }
        }
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
                            val isSelected = selectedItems.contains(stock)
                            if (isMultiple) {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = stock,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                        )
                                        HorizontalDivider()
                                    },
                                    leadingContent = {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.ic_circle_checked),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.ic_circle_unchecked),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            if (isSelected) {
                                                selectedItems.remove(stock)
                                            } else {
                                                selectedItems.add(stock)
                                            }
                                        },
                                )
                            } else {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = stock,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier
                                                .fillMaxSize(),
                                        )
                                        HorizontalDivider()
                                    },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable {
                                            navController.navigate(NavigationItem.BalanceSheetScreen.createRoute(stock))
                                        },
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

}