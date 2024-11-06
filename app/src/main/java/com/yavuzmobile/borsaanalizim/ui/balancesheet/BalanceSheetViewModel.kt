package com.yavuzmobile.borsaanalizim.ui.balancesheet

import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetWithRatios
import com.yavuzmobile.borsaanalizim.data.repository.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.RemoteRepository
import com.yavuzmobile.borsaanalizim.model.UiState
import com.yavuzmobile.borsaanalizim.ui.BaseViewModel
import com.yavuzmobile.borsaanalizim.util.Constant
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
                action = { localRepository.getBalanceSheetWithRatios(code) },
                onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
                onError = {
                    if (it.code == 404) {
                        fetchBalanceSheetsByStock(code)
                    } else {
                        updateUiState(_balanceSheetWithRatiosState, error = it.error)
                    }
                },
                onSuccess = {
                    val firstPeriodValue = "${Constant.FIRST_PERIOD.year}/${Constant.FIRST_PERIOD.month}"
                    if (it.balanceSheets.find { balanceSheet -> balanceSheet.period == firstPeriodValue } == null) {
                        fetchBalanceSheetsByStock(code)
                    } else {
                        updateUiState(_balanceSheetWithRatiosState, data = it)
                    }
                }
            )
        }
    }

    suspend fun fetchBalanceSheetsByStock(stockCode: String) {
        val firstPeriodValue = "${Constant.FIRST_PERIOD.year}/${Constant.FIRST_PERIOD.month}"
        handleResult(
            action = { remoteRepository.fetchBalanceSheetsByStock(stockCode) },
            onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
            onError = { updateUiState(_balanceSheetWithRatiosState, error = it.error) },
            onSuccess = {
                if (it.find { entity -> entity.period == firstPeriodValue } != null) {
                    checkBalanceSheetDatesLastPeriod(stockCode, firstPeriodValue, it)
                } else {
                    insertBalanceSheetsByStock(stockCode, it)
                }
            }
        )
    }

    private suspend fun checkBalanceSheetDatesLastPeriod(stockCode: String, lastPeriod: String, balanceSheets: List<BalanceSheetEntity>) {
        handleResult(
            action = { localRepository.getBalanceSheetDateStockWithDatesByStockCodeAndPeriod(stockCode, lastPeriod) },
            onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
            onError = {
                if (it.code == 404) {
                    getStock(stockCode)
                } else {
                    updateUiState(_balanceSheetWithRatiosState, error = it.error)
                }
            },
            onSuccess = { insertBalanceSheetsByStock(stockCode, balanceSheets) }
        )
    }

    private suspend fun getStock(stockCode: String) {
        handleResult(
            action = { localRepository.getStock(stockCode) },
            onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
            onError = { updateUiState(_balanceSheetWithRatiosState, error = it.error) },
            onSuccess = { getBalanceSheetDatesByStock(it.stockCode, it.mkkMemberOid) }
        )
    }

    private suspend fun insertBalanceSheetsByStock(code: String, entities: List<BalanceSheetEntity>) {
        handleResult(
            action = { localRepository.insertBalanceSheetsByStock(entities) },
            onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
            onError = { updateUiState(_balanceSheetWithRatiosState, error = it.error) },
            onSuccess = { getLocalBalanceSheetsByStock(code) }
        )
    }

    private suspend fun getBalanceSheetDatesByStock(code: String, mkkMemberOid: String) {
        handleResult(
            action = { remoteRepository.getBalanceSheetDatesByStock(code, mkkMemberOid) },
            onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
            onError = { updateUiState(_balanceSheetWithRatiosState, error = it.error) },
            onSuccess = { insertBalanceSheetDatesByStock(it) }
        )
    }

    private suspend fun insertBalanceSheetDatesByStock(response: BalanceSheetDateStockWithDates) {
        handleResult(
            action = { localRepository.insertBalanceSheetDate(response) },
            onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
            onError = { updateUiState(_balanceSheetWithRatiosState, error = it.error) },
            onSuccess = { getLocalBalanceSheetsByStock(response.stock.stockCode) },
        )
    }

    private suspend fun getLocalBalanceSheetsByStock(code: String) {
        handleResult(
            action = { localRepository.getBalanceSheetWithRatios(code) },
            onLoading = { updateUiState(_balanceSheetWithRatiosState, isLoading = true) },
            onError = { updateUiState(_balanceSheetWithRatiosState, error = it.error) },
            onSuccess = { updateUiState(_balanceSheetWithRatiosState, data = it) }
        )
    }
}