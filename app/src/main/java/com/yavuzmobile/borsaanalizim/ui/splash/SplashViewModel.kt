package com.yavuzmobile.borsaanalizim.ui.splash

import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.StockAndIndexAndSectorEntity
import com.yavuzmobile.borsaanalizim.data.repository.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.RemoteRepository
import com.yavuzmobile.borsaanalizim.model.SplashUiState
import com.yavuzmobile.borsaanalizim.model.UiState
import com.yavuzmobile.borsaanalizim.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository
) : BaseViewModel() {

    private val _completedUiState = MutableStateFlow(UiState<SplashUiState>())
    val completedUiState: StateFlow<UiState<SplashUiState>> = _completedUiState.asStateFlow()

    fun fetchBalanceSheetDates() {
        viewModelScope.launch {
            handleResult(
                action = { remoteRepository.getBalanceSheetDates() },
                onSuccess = { insertBalanceSheetDates(it) },
                onLoading = { updateUiState(_completedUiState, isLoading = true) },
                onError = { error -> updateUiState(_completedUiState, error = error.error) }
            )
        }
    }

    private suspend fun insertBalanceSheetDates(response: List<BalanceSheetDateStockWithDates>) {
        handleResult(
            action = { localRepository.insertBalanceSheetDate(response) },
            onSuccess = { getStocks() },
            onLoading = { updateUiState(_completedUiState, isLoading = true) },
            onError = { error -> updateUiState(_completedUiState, error = error.error) }
        )
    }

    private suspend fun getStocks() {
        handleResult(
            action = { localRepository.getStocks() },
            onSuccess = { stocks ->
                if (stocks.isNotEmpty()) {
                    updateUiState(_completedUiState, data = SplashUiState(isCompletedBalanceSheetDates = true, isCompletedStocks = true))
                } else {
                    fetchStocks()
                }
            },
            onLoading = { updateUiState(_completedUiState, isLoading = true) },
            onError = { error -> updateUiState(_completedUiState, error = error.error) }
        )
    }

    private suspend fun fetchStocks() {
        handleResult(
            action = { remoteRepository.getStocks() },
            onSuccess = { insertStocks(it) },
            onLoading = { updateUiState(_completedUiState, isLoading = true) },
            onError = { error -> updateUiState(_completedUiState, error = error.error) }
        )
    }

    private suspend fun insertStocks(entities: List<StockAndIndexAndSectorEntity>) {
        handleResult(
            action = { localRepository.insertStock(entities) },
            onSuccess = {
                updateUiState(_completedUiState, data = SplashUiState(isCompletedBalanceSheetDates = true, isCompletedStocks = true))
            },
            onLoading = { updateUiState(_completedUiState, isLoading = true) },
            onError = { error -> updateUiState(_completedUiState, error = error.error) }
        )
    }
}