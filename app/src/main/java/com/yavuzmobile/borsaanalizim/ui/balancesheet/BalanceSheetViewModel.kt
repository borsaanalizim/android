package com.yavuzmobile.borsaanalizim.ui.balancesheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.BusinessInvestmentRepository
import com.yavuzmobile.borsaanalizim.model.BalanceSheetWithRatios
import com.yavuzmobile.borsaanalizim.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceSheetViewModel @Inject constructor(
    private val businessInvestmentRepository: BusinessInvestmentRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _balanceSheetWithRatiosState = MutableStateFlow(UiState<BalanceSheetWithRatios>())
    val balanceSheetWithRatiosState: StateFlow<UiState<BalanceSheetWithRatios>> = _balanceSheetWithRatiosState.asStateFlow()

    fun fetchData(code: String) {
        viewModelScope.launch {
            localRepository.getLastTwelveBalanceSheetDateOfStock(code).collect { resultBalanceSheetDate ->
                when (resultBalanceSheetDate) {
                    is Result.Loading -> _balanceSheetWithRatiosState.update { state -> state.copy(true) }
                    is Result.Error -> _balanceSheetWithRatiosState.update { state -> state.copy(false, error = resultBalanceSheetDate.error) }
                    is Result.Success -> {
                        businessInvestmentRepository.getFinancialStatementList(code, resultBalanceSheetDate.data).collect { resultRemote ->
                            when (resultRemote) {
                                is Result.Loading -> _balanceSheetWithRatiosState.update { state -> state.copy(true) }
                                is Result.Error -> _balanceSheetWithRatiosState.update { state -> state.copy(false, error = resultRemote.error) }
                                is Result.Success -> {
                                    localRepository.getLast12BalanceSheetWithRatiosList(code).collect { resultLocal ->
                                        when(resultLocal) {
                                            is Result.Loading -> _balanceSheetWithRatiosState.update { state -> state.copy(true) }
                                            is Result.Error -> _balanceSheetWithRatiosState.update { state -> state.copy(false, error = resultLocal.error) }
                                            is Result.Success -> _balanceSheetWithRatiosState.update { state -> state.copy(false, data = resultLocal.data) }
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