package com.yavuzmobile.borsaanalizim.ui.comparestocks

import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.yavuzmobile.borsaanalizim.R
import com.yavuzmobile.borsaanalizim.ext.findActivity
import com.yavuzmobile.borsaanalizim.model.StockFilter
import com.yavuzmobile.borsaanalizim.ui.BaseHomeScreen
import com.yavuzmobile.borsaanalizim.ui.navgraph.NavigationItem
import com.yavuzmobile.borsaanalizim.util.NetworkUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareStocksScreen(
    navController: NavController,
    viewModel: CompareStocksViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    val stocksFilterUiState by viewModel.stocksFilterUiState.collectAsState()
    val completedAllDownloadsUiState by viewModel.completedAllDownloadsUiState.collectAsState()
    val indexesUiState by viewModel.indexesUiState.collectAsState()
    val sectorsUiState by viewModel.sectorsUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val showFilterBottomSheet by viewModel.showFilterBottomSheetState.collectAsState()

    val selectedItems = remember { mutableStateListOf<StockFilter>() }

    val sheetState = rememberModalBottomSheetState()

    var showAlert by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        selectedItems.clear()
        viewModel.onSearchQueryChange("")
    }

    BaseHomeScreen(
        navController = navController,
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {
        if (showAlert) {
            AlertDialog(
                onDismissRequest = { showAlert = false },
                confirmButton = {
                    Button(
                        onClick = {
                            showAlert = false
                            viewModel.downloadAllBalanceSheets()
                        }
                    ) {
                        Text(text = "Devam Et")
                    }
                },
                text = {
                    Text("Mobil veri ile büyük miktarda veri indirmek üzeresiniz. Devam etmek istiyor musunuz?")
                }
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .weight(9.4f)) {
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

            stocksFilterUiState.data?.filteredList?.let { filteredListNotNull ->
                if (filteredListNotNull.isNotEmpty() && selectedItems.size >= 2) {
                    Button(onClick = {
                        when (selectedItems.size) {
                            0 -> selectedItems.addAll(filteredListNotNull)
                            filteredListNotNull.size -> selectedItems.clear()
                            else -> {
                                selectedItems.clear()
                                selectedItems.addAll(filteredListNotNull)
                            }
                        }
                    }) {
                        Text(if (filteredListNotNull.size == selectedItems.size) "Temizle" else "Tümünü Seç")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SwipeRefresh(
                state = SwipeRefreshState(isRefreshing),
                onRefresh = { viewModel.refreshFilterBalanceSheetStocks() }
            ) {
                when (sectorsUiState.isLoading) {
                    true -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    else -> {
                        if (sectorsUiState.error != null) {
                            Text(
                                text = "Hata: ${sectorsUiState.error}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                when(stocksFilterUiState.isLoading || completedAllDownloadsUiState.isLoading) {
                    true -> {
                        if (stocksFilterUiState.isLoading) {
                            Column(Modifier.fillMaxWidth()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        }
                        if (completedAllDownloadsUiState.isLoading) {
                            Column(Modifier.fillMaxWidth()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        }
                    }
                    else -> {
                        Column(Modifier.fillMaxWidth()) {
                            if (stocksFilterUiState.error != null) {
                                Text(
                                    text = "Hata: ${stocksFilterUiState.error}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            stocksFilterUiState.data?.defaultList?.let { defaultList ->
                                if (defaultList.isEmpty() && !completedAllDownloadsUiState.isLoading) {
                                    Column {
                                        Button(onClick = {
                                            if (NetworkUtil.isMobileDataConnected(context)) {
                                                showAlert = true
                                            } else {
                                                viewModel.downloadAllBalanceSheets()
                                            }
                                        }) {
                                            Text("Tüm Hisse Verilerini İndir")
                                        }
                                    }
                                }
                            } ?: kotlin.run {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        }

                        LazyColumn(Modifier.fillMaxWidth()) {
                            itemsIndexed(stocksFilterUiState.data?.filteredList ?: emptyList()) { index, stock ->
                                val isSelected = selectedItems.contains(stock)
                                ListItem(
                                    headlineContent = {
                                        Row {
                                            Text(
                                                text = stock.stock.code.orEmpty(),
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .width(60.dp)
                                                    .align(Alignment.CenterVertically),
                                            )
                                            Text("-", Modifier.padding(end = 4.dp))
                                            Text(
                                                text = stock.stock.name.orEmpty(),
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
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isSelected) {
                                                selectedItems.remove(stock)
                                            } else {
                                                selectedItems.add(stock)
                                            }
                                        },
                                )
                                HorizontalDivider()
                                if (index + 1 == stocksFilterUiState.data?.filteredList?.size) {
                                    Button(onClick = {
                                        if (NetworkUtil.isMobileDataConnected(context)) {
                                            showAlert = true
                                        } else {
                                            viewModel.downloadAllBalanceSheets()
                                        }
                                    }, modifier = Modifier.padding(4.dp)) {
                                        Text("Hisse Verilerini Güncelle")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .weight(0.6f)) {
            ElevatedButton(
                onClick = {
                    viewModel.setSelectedStocks(selectedItems)
                    navController.navigate(NavigationItem.CompareStocksDetailScreen.route)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(),
                enabled = selectedItems.size >= 2
            ) {
                Text("Devam")
            }
        }

        if (showFilterBottomSheet && indexesUiState.data != null && sectorsUiState.data != null) {
            CompareStocksFilterBottomSheet(viewModel = viewModel, indexes = indexesUiState.data!!, sectors = sectorsUiState.data!!, sheetState = sheetState) {
                viewModel.setShowFilterBottomSheetState(false)
            }
        }
    }
}