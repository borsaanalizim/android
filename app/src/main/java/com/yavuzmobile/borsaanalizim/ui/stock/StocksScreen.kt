package com.yavuzmobile.borsaanalizim.ui.stock

import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.yavuzmobile.borsaanalizim.R
import com.yavuzmobile.borsaanalizim.ext.findActivity
import com.yavuzmobile.borsaanalizim.ui.BaseHomeScreen
import com.yavuzmobile.borsaanalizim.ui.navgraph.NavigationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StocksScreen(
    navController: NavController,
    viewModel: StocksViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context.findActivity()

    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    val stocksUiState by viewModel.stocksUiState.collectAsState()
    val indexesUiState by viewModel.indexesUiState.collectAsState()
    val sectorsUiState by viewModel.sectorsUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val showFilterBottomSheet by viewModel.showFilterBottomSheetState.collectAsState()

    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        viewModel.onSearchQueryChange("")
        viewModel.fetchStocks()
    }

    BaseHomeScreen(
        navController = navController,
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                label = { Text("Hisse Ara") },
                modifier = Modifier
                    .weight(1f)  // Kalan genişliği kaplar
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(4.dp))

            IconButton(
                onClick = {
                    if (sectorsUiState.data == null) {
                        viewModel.fetchIndexes()
                    } else {
                        viewModel.setShowFilterBottomSheetState(true)
                    }
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_filter),
                    contentDescription = "Filter"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SwipeRefresh(
            state = SwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.refreshStocks() }
        ) {
            when (sectorsUiState.isLoading) {
                true -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                else -> {
                    if (stocksUiState.error != null) {
                        Text(
                            text = "Hata: ${stocksUiState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

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
                        items(stocksUiState.data?.filteredStocks ?: emptyList()) { stock ->
                            ListItem(
                                headlineContent = {
                                    Row {
                                        Text(
                                            text = stock.code.orEmpty(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .align(Alignment.CenterVertically)
                                                .width(60.dp),
                                            textAlign = TextAlign.Left
                                        )
                                        Text("-", Modifier.padding(end = 4.dp))
                                        Text(
                                            text = stock.name.orEmpty(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1,
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .align(Alignment.CenterVertically),
                                        )
                                    }
                                    HorizontalDivider()
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        navController.navigate(NavigationItem.BalanceSheetScreen.createRoute(stock.code.orEmpty()))
                                    },
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }


        if (showFilterBottomSheet && indexesUiState.data != null && sectorsUiState.data != null) {
            StockFilterBottomSheetScreen(viewModel = viewModel, indexes = indexesUiState.data!!, sectors = sectorsUiState.data!!, sheetState = sheetState) {
                viewModel.setShowFilterBottomSheetState(false)
            }
        }
    }
}