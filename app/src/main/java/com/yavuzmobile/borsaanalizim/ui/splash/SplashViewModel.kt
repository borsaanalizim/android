package com.yavuzmobile.borsaanalizim.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.StockAndIndexAndSectorEntity
import com.yavuzmobile.borsaanalizim.data.model.StockResponse
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.RemoteRepository
import com.yavuzmobile.borsaanalizim.model.SplashUiState
import com.yavuzmobile.borsaanalizim.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val remoteRepository: RemoteRepository, private val localRepository: LocalRepository) : ViewModel() {

    private val _balanceSheetDatesUiState = MutableStateFlow(UiState<List<BalanceSheetDateStockWithDates>>())
    val balanceSheetDatesUiState: StateFlow<UiState<List<BalanceSheetDateStockWithDates>>> = _balanceSheetDatesUiState.asStateFlow()

    private val _stocksUiState = MutableStateFlow(UiState<List<StockAndIndexAndSectorEntity>>())
    val stocksUiState: StateFlow<UiState<List<StockAndIndexAndSectorEntity>>> = _stocksUiState.asStateFlow()

    private val _completedUiState = MutableStateFlow(UiState<SplashUiState>())
    val completedUiState: StateFlow<UiState<SplashUiState>> = _completedUiState.asStateFlow()

    fun fetchBalanceSheetDates() {
        viewModelScope.launch {
            when (val remoteResultBalanceSheetDate = remoteRepository.getBalanceSheetDates().last()) {
                is Result.Loading -> _balanceSheetDatesUiState.update { state -> state.copy(isLoading = true) }
                is Result.Error -> _balanceSheetDatesUiState.update { state -> state.copy(isLoading = false, error = remoteResultBalanceSheetDate.error) }
                is Result.Success<List<BalanceSheetDateStockWithDates>> -> {
                    _balanceSheetDatesUiState.update { state -> state.copy(false, data = remoteResultBalanceSheetDate.data) }
                    val localResultBalanceSheetDate = localRepository.insertBalanceSheetDate(remoteResultBalanceSheetDate.data).last()
                    when (localResultBalanceSheetDate) {
                        is Result.Loading -> _completedUiState.update { state -> state.copy(isLoading = true) }
                        is Result.Error -> _completedUiState.update { state -> state.copy(isLoading = false, error = localResultBalanceSheetDate.error) }
                        is Result.Success<Boolean> -> {
                            _completedUiState.update { state -> state.copy(isLoading = false, data = SplashUiState(isCompletedBalanceSheetDates = true, isCompletedStocks = false)) }
                            when (val localResultStocks = localRepository.getStocks().last()) {
                                is Result.Loading -> _completedUiState.update { state -> state.copy(isLoading = true) }
                                is Result.Error -> _completedUiState.update { state -> state.copy(isLoading = false, error = localResultStocks.error) }
                                is Result.Success<List<StockResponse>> -> {
                                    val localDataStocks = localResultStocks.data
                                    when {
                                        localDataStocks.isNotEmpty() -> _completedUiState.update { state -> state.copy(false, data = SplashUiState(isCompletedBalanceSheetDates = true, isCompletedStocks = true)) }
                                        else -> {
                                            when (val remoteResultStocks = remoteRepository.getStocks().last()) {
                                                is Result.Loading -> _completedUiState.update { state -> state.copy(isLoading = true) }
                                                is Result.Error -> _completedUiState.update { state -> state.copy(isLoading = false, error = remoteResultStocks.error) }
                                                is Result.Success<List<StockAndIndexAndSectorEntity>> -> {
                                                    _stocksUiState.update { state -> state.copy(false, data = remoteResultStocks.data) }
                                                    val localResultInsertStocks = localRepository.insertStock(remoteResultStocks.data).last()
                                                    when (localResultInsertStocks) {
                                                        is Result.Loading -> _completedUiState.update { state -> state.copy(isLoading = true) }
                                                        is Result.Error -> _completedUiState.update { state -> state.copy(isLoading = false, error = localResultInsertStocks.error) }
                                                        is Result.Success<Boolean> -> _completedUiState.update { state -> state.copy(false, data = SplashUiState(isCompletedBalanceSheetDates = true, isCompletedStocks = true)) }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}