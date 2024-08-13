package com.yavuzmobile.borsaanalizim.ui.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateEntity
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.FinTablesRepository
import com.yavuzmobile.borsaanalizim.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val remoteRepository: FinTablesRepository, private val localRepository: LocalRepository) : ViewModel() {

    private val _balanceSheetDatesUiState = MutableStateFlow(UiState<List<BalanceSheetDateEntity>>())
    val balanceSheetDatesUiState: StateFlow<UiState<List<BalanceSheetDateEntity>>> = _balanceSheetDatesUiState.asStateFlow()

    private val _completedUiState = MutableStateFlow(UiState<Boolean>())
    val completedUiState: StateFlow<UiState<Boolean>> = _completedUiState.asStateFlow()

    private val filterQuery = "published_at||!period||!net_kar||!yillik_net_kar_degisimi||"
    // private val authToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoyMTU0OTYxMzc3LCJpYXQiOjE3MjI5NjEzNzcsImp0aSI6IjI1OGQ2ZDg3M2EwMDRlMmE4NTdlNWNhZDBjMjY0ZDkwIiwidXNlcl9pZCI6OTAzNjl9.fGol3x0l7np3DGLJE-XAyNMe7f6nu61-WSN4r_6JfKo"

    fun fetchBalanceSheetDates() {
        viewModelScope.launch {
            _completedUiState.update { it.copy(data = false) }
            val periods = getLastFivePeriods()
            val deferredResults = periods.mapIndexed { index, period ->
                async {
                    localRepository.getLastFiveBalanceSheetDates().collect { resultLocal ->
                        when (resultLocal) {
                            is Result.Loading -> _balanceSheetDatesUiState.update { it.copy(isLoading = true) }
                            is Result.Error -> {
                                if (resultLocal.code == 404) {
                                    remoteRepository.getBalanceSheetDates(index, period, filterQuery).collect { resultRemote ->
                                        when (resultRemote) {
                                            is Result.Loading -> _balanceSheetDatesUiState.update { it.copy(isLoading = true) }
                                            is Result.Error -> _balanceSheetDatesUiState.update { it.copy(isLoading = false, error = it.error + "/n" + resultRemote.error) }
                                            is Result.Success -> {
                                                val insertResult = localRepository.insertBalanceSheetDate(resultRemote.data)
                                                if (insertResult is Result.Success) {
                                                    val updatedData = _balanceSheetDatesUiState.value.data.orEmpty() + resultRemote.data
                                                    _balanceSheetDatesUiState.update { it.copy(isLoading = false, data = updatedData) }
                                                } else {
                                                    _balanceSheetDatesUiState.update { it.copy(isLoading = false, error = (insertResult as Result.Error).error) }
                                                }
                                            }
                                        }
                                    }
                                    return@collect
                                }
                                _balanceSheetDatesUiState.update { it.copy(isLoading = false, error = resultLocal.error) }
                            }
                            is Result.Success -> {
                                val updatedData = _balanceSheetDatesUiState.value.data.orEmpty() + resultLocal.data
                                _balanceSheetDatesUiState.update { it.copy(isLoading = false, data = updatedData) }
                            }
                        }
                    }
                }
            }

            deferredResults.awaitAll()
            _completedUiState.update { it.copy(data = true) }
        }
    }

    private fun getLastFivePeriods(): List<String> {
        try {
            val today = LocalDate.now()
            val periods = mutableListOf<String>()
            val year = today.year
            val month = today.monthValue
            var periodYear = year
            var periodMonth = month


            for (i in 0 until 5) {
                when {
                    month in 1..2 -> {
                        periodYear -= 1
                        periodMonth = 12
                        periods.add("$periodYear/$periodMonth")
                    }
                    periodMonth % 3 == 0 -> {
                        periodMonth -= 3
                        if (periodMonth == 0) {
                            periodYear -= 1
                            periodMonth = 12
                        }
                        periods.add("$periodYear/$periodMonth")
                    }
                    else -> {
                        periodMonth -= periodMonth % 3
                        periods.add("$periodYear/$periodMonth")
                    }
                }
            }
            return periods
        } catch (e: Exception) {
            Log.e("DATE EXCEPTION", e.message.toString())
            return emptyList()
        }
    }
}