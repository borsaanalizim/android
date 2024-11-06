package com.yavuzmobile.borsaanalizim.ui.stock

import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SectorEntity
import com.yavuzmobile.borsaanalizim.data.model.IndexResponse
import com.yavuzmobile.borsaanalizim.data.model.SectorResponse
import com.yavuzmobile.borsaanalizim.data.repository.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.RemoteRepository
import com.yavuzmobile.borsaanalizim.model.Stock
import com.yavuzmobile.borsaanalizim.model.UiState
import com.yavuzmobile.borsaanalizim.ui.BaseViewModel
import com.yavuzmobile.borsaanalizim.util.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class StocksViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel() {

    private val _stocksUiState = MutableStateFlow(UiState<Stock>())
    val stocksUiState: StateFlow<UiState<Stock>> = _stocksUiState.asStateFlow()

    private val _indexesUiState = MutableStateFlow(UiState<List<IndexResponse>>())
    val indexesUiState: StateFlow<UiState<List<IndexResponse>>> = _indexesUiState.asStateFlow()

    private val _sectorsUiState = MutableStateFlow(UiState<SectorResponse>())
    val sectorsUiState: StateFlow<UiState<SectorResponse>> = _sectorsUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _showFilterBottomSheetState = MutableStateFlow(false)
    val showFilterBottomSheetState: StateFlow<Boolean> = _showFilterBottomSheetState.asStateFlow()

    private val _selectedIndexStockFilterState = MutableStateFlow(Constant.ALL)
    val selectedIndexStockFilterState: StateFlow<String> = _selectedIndexStockFilterState.asStateFlow()

    private val _selectedSectorStockFilterState = MutableStateFlow(Constant.ALL)
    val selectedSectorStockFilterState: StateFlow<String> = _selectedSectorStockFilterState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery.debounce(300).collectLatest { query ->
                filterQuery(query)
            }
        }
    }

    fun fetchStocks() {
        viewModelScope.launch {
            handleResult(
                action =  { localRepository.getStocks() },
                onSuccess =  {
                    updateUiState(_stocksUiState, data = Stock(it, it))
                },
                onLoading = {
                    updateUiState(_stocksUiState, isLoading = true)
                },
                onError = { error ->
                    updateUiState(_stocksUiState, error = error.error)
                }
            )
        }
    }

    fun refreshStocks() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchStocks()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun filterQuery(query: String) {
        _stocksUiState.value.data?.stocks?.let { stocksNotNull ->
            val filteredList = stocksNotNull.filter { stock -> stock.code?.contains(query, ignoreCase = true) == true }
            updateUiState(_stocksUiState, data = Stock(stocks = stocksNotNull, filteredStocks = filteredList))
        }
    }

    fun fetchIndexes() {
        viewModelScope.launch {
            handleResult(
                action = { remoteRepository.getIndexes() },
                onSuccess = {
                    insertIndexes(it)
                },
                onLoading = { updateUiState(_indexesUiState, isLoading = true) },
                onError = { error -> updateUiState(_indexesUiState, error = error.error) }
            )
        }
    }

    private suspend fun insertIndexes(entities: List<IndexEntity>) {
        handleResult(
            action = { localRepository.insertIndexes(entities) },
            onSuccess = {
                getIndexes()
            },
            onLoading = { updateUiState(_indexesUiState, isLoading = true) },
            onError = { error -> updateUiState(_indexesUiState, error = error.error) }
        )
    }

    private suspend fun getIndexes() {
        handleResult(
            action = { localRepository.getIndexes() },
            onSuccess = {
                updateUiState(_indexesUiState, data = it)
                fetchSectors()
            },
            onLoading = { updateUiState(_indexesUiState, isLoading = true) },
            onError = { error -> updateUiState(_indexesUiState, error = error.error) }
        )
    }

    private suspend fun fetchSectors() {
        viewModelScope.launch {
            handleResult(
                action = { remoteRepository.getSectors() },
                onSuccess = {
                    insertSectors(it)
                },
                onLoading = { updateUiState(_sectorsUiState, isLoading = true) },
                onError = { error -> updateUiState(_sectorsUiState, error = error.error) }
            )
        }
    }

    private suspend fun insertSectors(entity: SectorEntity) {
        handleResult(
            action = { localRepository.insertSectors(entity) },
            onSuccess = {
                getSectors()
            },
            onLoading = { updateUiState(_sectorsUiState, isLoading = true) },
            onError = { error -> updateUiState(_sectorsUiState, error = error.error) }
        )
    }

    private suspend fun getSectors() {
        handleResult(
            action = { localRepository.getSectors() },
            onSuccess = {
                updateUiState(_sectorsUiState, data = it)
                _showFilterBottomSheetState.update { true }
            },
            onLoading = { updateUiState(_sectorsUiState, isLoading = true) },
            onError = { error -> updateUiState(_sectorsUiState, error = error.error) }
        )
    }

    fun setShowFilterBottomSheetState(value: Boolean) {
        _showFilterBottomSheetState.update { value }
    }

    fun onClickFilterButton(selectedIndexName: String, selectedSectorName: String) {
        _showFilterBottomSheetState.update { false }
        _selectedIndexStockFilterState.update { selectedIndexName }
        _selectedSectorStockFilterState.update { selectedSectorName }

        if (selectedIndexName == Constant.ALL && selectedSectorName == Constant.ALL) {
            _stocksUiState.update { state -> state.copy(data = state.data?.copy(filteredStocks = _stocksUiState.value.data?.stocks!!)) }
            return
        }
        val filteredStocks = _stocksUiState.value.data?.stocks?.filter {
            it.indexes?.find { index -> index == selectedIndexName } != null || it.sector == selectedSectorName
        } ?: run {
            emptyList()
        }
        _stocksUiState.update { state -> state.copy(data = state.data?.copy(filteredStocks = filteredStocks)) }

    }
}