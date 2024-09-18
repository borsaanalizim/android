package com.yavuzmobile.borsaanalizim.ui.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SectorEntity
import com.yavuzmobile.borsaanalizim.data.model.IndexResponse
import com.yavuzmobile.borsaanalizim.data.model.SectorResponse
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.RemoteRepository
import com.yavuzmobile.borsaanalizim.model.Stock
import com.yavuzmobile.borsaanalizim.model.UiState
import com.yavuzmobile.borsaanalizim.util.Constant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class StocksViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : ViewModel() {

    private val _stocksUiState = MutableStateFlow(UiState<Stock>())
    val stocksUiState: StateFlow<UiState<Stock>> = _stocksUiState.asStateFlow()

    private val _indexesUiState = MutableStateFlow(UiState<List<IndexResponse>>())
    val indexesUiState: StateFlow<UiState<List<IndexResponse>>> = _indexesUiState.asStateFlow()

    private val _sectorsUiState = MutableStateFlow(UiState<List<SectorResponse>>())
    val sectorsUiState: StateFlow<UiState<List<SectorResponse>>> = _sectorsUiState.asStateFlow()

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
            localRepository.getStocks().collect {
                when(it) {
                    is Result.Loading -> _stocksUiState.update { state -> state.copy(true) }
                    is Result.Error -> _stocksUiState.update { state -> state.copy(false, error = it.error) }
                    is Result.Success -> _stocksUiState.update { state -> state.copy(false, data = Stock(it.data, it.data)) }
                }
            }
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
        val filteredList = _stocksUiState.value.data?.stocks?.filter { stock ->
            stock.code?.contains(query, ignoreCase = true) == true
        } ?: emptyList()
        _stocksUiState.update { state -> state.copy(isLoading = false, data = state.data?.copy(filteredStocks = filteredList)) }
    }

    fun fetchIndexes() {
        viewModelScope.launch {
            val remoteResult = remoteRepository.getIndexes().last()
            if (remoteResult is Result.Loading) {
                _indexesUiState.update { state -> state.copy(isLoading = true) }
            }
            if (remoteResult is Result.Error) {
                _indexesUiState.update { state -> state.copy(isLoading = false, error = remoteResult.error) }
            }
            if (remoteResult is Result.Success<List<IndexEntity>>) {
                val insertLocalResult = localRepository.insertIndexes(remoteResult.data).last()
                if (insertLocalResult is Result.Loading) {
                    _indexesUiState.update { state -> state.copy(isLoading = true) }
                }
                if (insertLocalResult is Result.Error) {
                    _indexesUiState.update { state -> state.copy(isLoading = false, error = insertLocalResult.error) }
                }
                if (insertLocalResult is Result.Success<Boolean>) {
                    val getLocalResult = localRepository.getIndexes().last()
                    if (getLocalResult is Result.Loading) {
                        _indexesUiState.update { state -> state.copy(isLoading = true) }
                    }
                    if (getLocalResult is Result.Error) {
                        _indexesUiState.update { state -> state.copy(isLoading = false, error = getLocalResult.error) }
                    }
                    if (getLocalResult is Result.Success<List<IndexResponse>>) {
                        _indexesUiState.update { state -> state.copy(isLoading = false, data = getLocalResult.data) }
                        fetchSectors()
                    }
                }
            }
        }
    }

    private fun fetchSectors() {
        viewModelScope.launch {
            val remoteResult = remoteRepository.getSectors().last()
            if (remoteResult is Result.Loading) {
                _sectorsUiState.update { state -> state.copy(isLoading = true) }
            }
            if (remoteResult is Result.Error) {
                _sectorsUiState.update { state -> state.copy(isLoading = false, error = remoteResult.error) }
            }
            if (remoteResult is Result.Success<List<SectorEntity>>) {
                val insertLocalResult = localRepository.insertSectors(remoteResult.data).last()
                if (insertLocalResult is Result.Loading) {
                    _sectorsUiState.update { state -> state.copy(isLoading = true) }
                }
                if (insertLocalResult is Result.Error) {
                    _sectorsUiState.update { state -> state.copy(isLoading = false, error = insertLocalResult.error) }
                }
                if (insertLocalResult is Result.Success<Boolean>) {
                    val getLocalResult = localRepository.getSectors().last()
                    if (getLocalResult is Result.Loading) {
                        _sectorsUiState.update { state -> state.copy(isLoading = true) }
                    }
                    if (getLocalResult is Result.Error) {
                        _sectorsUiState.update { state -> state.copy(isLoading = false, error = getLocalResult.error) }
                    }
                    if (getLocalResult is Result.Success<List<SectorResponse>>) {
                        _sectorsUiState.update { state -> state.copy(isLoading = false, data = getLocalResult.data) }
                        _showFilterBottomSheetState.update { true }
                    }
                }
            }
        }
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
        val filteredStocks = _stocksUiState.value.data?.stocks?.filter { it.indexes?.find { index -> index == selectedIndexName } != null || it.sectors?.find { sector -> sector == selectedSectorName } != null }?.let {
            it
        } ?: run {
            emptyList()
        }
        _stocksUiState.update { state -> state.copy(data = state.data?.copy(filteredStocks = filteredStocks)) }

    }
}