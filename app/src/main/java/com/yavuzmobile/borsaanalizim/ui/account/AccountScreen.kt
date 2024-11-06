package com.yavuzmobile.borsaanalizim.ui.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yavuzmobile.borsaanalizim.ui.BaseHomeScreen

@Composable
fun AccountScreen(navController: NavController, viewModel: AccountViewModel = hiltViewModel()) {

    val completedAllDownloadsUiState by viewModel.isCompletedLastPeriodBalanceSheets.collectAsState()

    BaseHomeScreen(
        navController = navController,
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
    ) {

        if (completedAllDownloadsUiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            completedAllDownloadsUiState.error?.let {
                Text("Hata: $it", color = MaterialTheme.colorScheme.error)
            } ?: kotlin.run {
                if (completedAllDownloadsUiState.data == true) {
                    Text("İndirme tamamlandı")
                }
            }
        }

        Button(onClick = {
            viewModel.downloadLastPeriodBalanceSheets()
        }) {
            Text("${viewModel.lastPeriod} Dönemi İndir")
        }
    }
}