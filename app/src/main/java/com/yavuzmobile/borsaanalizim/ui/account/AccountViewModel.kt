package com.yavuzmobile.borsaanalizim.ui.account

import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
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
class AccountViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel() {

    private val _isCompletedLastPeriodBalanceSheets = MutableStateFlow(UiState<Boolean>())
    val isCompletedLastPeriodBalanceSheets: StateFlow<UiState<Boolean>> = _isCompletedLastPeriodBalanceSheets.asStateFlow()

    val lastPeriod = "${Constant.FIRST_PERIOD.year}/${Constant.FIRST_PERIOD.month}"

    fun downloadLastPeriodBalanceSheets() {
        viewModelScope.launch {
            handleResult(
                action = { remoteRepository.fetchBalanceSheetsByPeriod(lastPeriod) },
                onLoading = { updateUiState(_isCompletedLastPeriodBalanceSheets, isLoading = true) },
                onError = { error -> updateUiState(_isCompletedLastPeriodBalanceSheets, error = error.error) },
                onSuccess = { insertBalanceSheets(it) }
            )
        }
    }

    private suspend fun insertBalanceSheets(entities: List<BalanceSheetEntity>) {
        handleResult(
            action = { localRepository.insertBalanceSheetsByPeriod(entities) },
            onLoading = { updateUiState(_isCompletedLastPeriodBalanceSheets, isLoading = true) },
            onError = { error -> updateUiState(_isCompletedLastPeriodBalanceSheets, error = error.error) },
            onSuccess = { updateUiState(_isCompletedLastPeriodBalanceSheets, data = true) }
        )
    }
}