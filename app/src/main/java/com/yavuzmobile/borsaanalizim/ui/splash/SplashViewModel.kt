package com.yavuzmobile.borsaanalizim.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.StockAndIndexAndSectorEntity
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.RemoteRepository
import com.yavuzmobile.borsaanalizim.model.SplashUiState
import com.yavuzmobile.borsaanalizim.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            remoteRepository.getBalanceSheetDates().collect { resultRemote ->
                when (resultRemote) {
                    is Result.Loading -> _balanceSheetDatesUiState.update { state -> state.copy(isLoading = true) }
                    is Result.Error -> _balanceSheetDatesUiState.update { state -> state.copy(isLoading = false, error = resultRemote.error) }
                    is Result.Success -> {
                        _balanceSheetDatesUiState.update { state -> state.copy(false, data = resultRemote.data) }
                        localRepository.insertBalanceSheetDate(resultRemote.data).collect { resultLocal ->
                            when(resultLocal) {
                                is Result.Loading -> _completedUiState.update { state -> state.copy(isLoading = true) }
                                is Result.Error -> _completedUiState.update { state -> state.copy(isLoading = false, error = resultLocal.error) }
                                is Result.Success -> {
                                    _completedUiState.update { state -> state.copy(isLoading = false, data = SplashUiState(isCompletedBalanceSheetDates = true, isCompletedStocks = false)) }
                                    remoteRepository.getStocks().collect { resultRemote ->
                                        when (resultRemote) {
                                            is Result.Loading -> _stocksUiState.update { state -> state.copy(isLoading = true) }
                                            is Result.Error -> _stocksUiState.update { state -> state.copy(isLoading = false, error = resultRemote.error) }
                                            is Result.Success -> {
                                                _stocksUiState.update { state -> state.copy(false, data = resultRemote.data) }
                                                localRepository.insertStock(resultRemote.data).collect { resultLocal ->
                                                    when(resultLocal) {
                                                        is Result.Loading -> _completedUiState.update { state -> state.copy(isLoading = true) }
                                                        is Result.Error -> _completedUiState.update { state -> state.copy(isLoading = false, error = resultLocal.error) }
                                                        is Result.Success -> _completedUiState.update { state -> state.copy(false, data = SplashUiState(isCompletedBalanceSheetDates = true, isCompletedStocks = true)) }
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