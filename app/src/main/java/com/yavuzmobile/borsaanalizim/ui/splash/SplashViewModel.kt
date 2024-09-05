package com.yavuzmobile.borsaanalizim.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.entity.StockWithDates
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.StockMarketAnalysisRepository
import com.yavuzmobile.borsaanalizim.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val remoteRepository: StockMarketAnalysisRepository, private val localRepository: LocalRepository) : ViewModel() {

    private val _balanceSheetDatesUiState = MutableStateFlow(UiState<List<StockWithDates>>())
    val balanceSheetDatesUiState: StateFlow<UiState<List<StockWithDates>>> = _balanceSheetDatesUiState.asStateFlow()

    private val _completedUiState = MutableStateFlow(UiState<Boolean>())
    val completedUiState: StateFlow<UiState<Boolean>> = _completedUiState.asStateFlow()

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
                                is Result.Success -> _completedUiState.update { state -> state.copy(false, data = resultLocal.data) }
                            }
                        }
                    }
                }
            }
        }
    }

}