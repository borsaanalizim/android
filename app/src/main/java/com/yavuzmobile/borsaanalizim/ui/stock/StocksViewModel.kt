package com.yavuzmobile.borsaanalizim.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.repository.remote.IsYatirimRepository
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.model.Stock
import com.yavuzmobile.borsaanalizim.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StocksViewModel @Inject constructor(
    private val repository: IsYatirimRepository
) : ViewModel() {

    private val _stocksUiState = MutableStateFlow(UiState<Stock>())
    val stocksUiState: StateFlow<UiState<Stock>> = _stocksUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        viewModelScope.launch {
            _searchQuery.debounce(300).collectLatest { query ->
                filterStocks(query)
            }
        }
    }

    fun fetchStocks() {
        viewModelScope.launch {
            repository.fetchStocks().collect {
                when(it) {
                    is Result.Loading -> _stocksUiState.update { state -> state.copy(true) }
                    is Result.Error -> _stocksUiState.update { state -> state.copy(false, error = it.error) }
                    is Result.Success -> _stocksUiState.update { state -> state.copy(false, data = it.data) }
                }
            }
        }
    }

    fun refreshStocks() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.fetchStocks().collect {
                    when(it) {
                        is Result.Loading -> _stocksUiState.update { state -> state.copy(isLoading = true) }
                        is Result.Error -> _stocksUiState.update { state -> state.copy(isLoading = false, error = it.error) }
                        is Result.Success -> _stocksUiState.update { state -> state.copy(isLoading = false, data = it.data) }
                    }
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun filterStocks(query: String) {
        val filteredList = _stocksUiState.value.data?.stocks?.filter { stock ->
            stock.contains(query, ignoreCase = true)
        }
        _stocksUiState.update { state ->
            state.copy(data = state.data?.copy(filteredStocks = filteredList))
        }
    }
}