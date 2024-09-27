package com.yavuzmobile.borsaanalizim.ui.balancesheet

import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetWithRatios
import com.yavuzmobile.borsaanalizim.data.repository.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.RemoteRepository
import com.yavuzmobile.borsaanalizim.model.UiState
import com.yavuzmobile.borsaanalizim.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceSheetViewModel @Inject constructor(private val localRepository: LocalRepository, private val remoteRepository: RemoteRepository) : BaseViewModel() {

    private val _balanceSheetWithRatiosState = MutableStateFlow(UiState<BalanceSheetWithRatios>())
    val balanceSheetWithRatiosState: StateFlow<UiState<BalanceSheetWithRatios>> = _balanceSheetWithRatiosState.asStateFlow()

    private var currentCode: String? = null

    fun getBalanceSheetsByStock(code: String) {
        if (currentCode == code) return
        currentCode = code
        viewModelScope.launch {
            handleResult(
                action = { localRepository.getLastTwelveBalanceSheetWithRatios(code) },
                onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
                onError = {
                    if (it.code == 404) {
                        fetchBalanceSheetsByStock(code)
                    } else {
                        updateUiState(_balanceSheetWithRatiosState, error = it.error)
                    }
                },
                onSuccess = {
                    updateUiState(_balanceSheetWithRatiosState, data = it)
                }
            )
        }
    }

    private suspend fun fetchBalanceSheetsByStock(stockCode: String) {
        handleResult(
            action = { remoteRepository.fetchBalanceSheetsByStock(stockCode) },
            onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
            onError = { updateUiState(_balanceSheetWithRatiosState, error = it.error) },
            onSuccess = { insertBalanceSheetsByStock(stockCode, it) },
        )
    }

    private suspend fun insertBalanceSheetsByStock(code: String, entities: List<BalanceSheetEntity>) {
        handleResult(
            action = { localRepository.insertBalanceSheetsByStock(code, entities) },
            onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
            onError = { updateUiState(_balanceSheetWithRatiosState, error = it.error) },
            onSuccess = { updateUiState(_balanceSheetWithRatiosState, data = it) },
        )
    }
}