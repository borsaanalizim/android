package com.yavuzmobile.borsaanalizim.ui.comparestocks

import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatioEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SectorEntity
import com.yavuzmobile.borsaanalizim.data.model.IndexResponse
import com.yavuzmobile.borsaanalizim.data.model.SectorResponse
import com.yavuzmobile.borsaanalizim.data.model.StockResponse
import com.yavuzmobile.borsaanalizim.data.repository.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.RemoteRepository
import com.yavuzmobile.borsaanalizim.model.StockFilter
import com.yavuzmobile.borsaanalizim.model.StocksFilter
import com.yavuzmobile.borsaanalizim.model.UiState
import com.yavuzmobile.borsaanalizim.model.YearMonth
import com.yavuzmobile.borsaanalizim.ui.BaseViewModel
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
class CompareStocksViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
): BaseViewModel() {

    private val _stocksFilterUiState = MutableStateFlow(UiState<StocksFilter>())
    val stocksFilterUiState: StateFlow<UiState<StocksFilter>> = _stocksFilterUiState.asStateFlow()

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

    private val _selectedPeriodFilterState = MutableStateFlow(Constant.FIRST_PERIOD)
    val selectedPeriodFilterState: StateFlow<YearMonth> = _selectedPeriodFilterState.asStateFlow()

    private val _selectedIndexStockFilterState = MutableStateFlow(Constant.ALL)
    val selectedIndexStockFilterState: StateFlow<String> = _selectedIndexStockFilterState.asStateFlow()

    private val _selectedSectorStockFilterState = MutableStateFlow(Constant.ALL)
    val selectedSectorStockFilterState: StateFlow<String> = _selectedSectorStockFilterState.asStateFlow()

    private val _completedAllDownloadsUiState = MutableStateFlow(UiState<Boolean>())
    val completedAllDownloadsUiState: StateFlow<UiState<Boolean>> = _completedAllDownloadsUiState.asStateFlow()

    private val _selectedStocksUiState = MutableStateFlow<List<StockFilter>>(emptyList())
    val selectedStocksUiState: StateFlow<List<StockFilter>> = _selectedStocksUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery.debounce(300).collectLatest { query ->
                filterQuery(query)
            }
        }
    }

    fun fetchFilterBalanceSheetStocks() {
        val selectedPeriod = "${_selectedPeriodFilterState.value.year}/${_selectedPeriodFilterState.value.month}"
        viewModelScope.launch {
            // BalanceSheetWithRatios
            val resultBalanceSheetRatiosList = localRepository.getBalanceSheetRatiosList(selectedPeriod).last()
            if (resultBalanceSheetRatiosList is Result.Loading) {
                _stocksFilterUiState.update { state -> state.copy(isLoading = true) }
            }
            if (resultBalanceSheetRatiosList is Result.Error) {
                _stocksFilterUiState.update { state -> state.copy(isLoading = false, error = resultBalanceSheetRatiosList.error) }
            }
            if (resultBalanceSheetRatiosList is Result.Success<List<BalanceSheetRatioEntity>>) {
                // Filter selected period
                val balanceSheetRatiosListData = resultBalanceSheetRatiosList.data.filter { balanceSheetRatiosData -> balanceSheetRatiosData.period == selectedPeriod }
                if (balanceSheetRatiosListData.isEmpty()) {
                    _stocksFilterUiState.update { state -> state.copy(isLoading = false, data = StocksFilter(emptyList(), emptyList())) }
                    return@launch
                }
                val stockFilterList = ArrayList<StockFilter>()
                balanceSheetRatiosListData.forEach { balanceSheetRatiosData ->
                    val stockCode = balanceSheetRatiosData.stockCode

                    // Filter stocks by index and sector
                    val resultStockByIndexAndSector = localRepository.getStock(stockCode).last()
                    if (resultStockByIndexAndSector is Result.Loading) {
                        _stocksFilterUiState.update { state -> state.copy(isLoading = true) }
                    }
                    if (resultStockByIndexAndSector is Result.Error && resultStockByIndexAndSector.code != 404) {
                        _stocksFilterUiState.update { state -> state.copy(isLoading = false, error = resultStockByIndexAndSector.error) }
                    }
                    if (resultStockByIndexAndSector is Result.Success<StockResponse>) {
                        val stockResponse = resultStockByIndexAndSector.data

                        // Filter dates by stock and period
                        val resultDateByStockCodeAndPeriod = localRepository.getBalanceSheetDateStockWithDatesByStockCodeAndPeriod(stockCode, selectedPeriod).last()
                        if (resultDateByStockCodeAndPeriod is Result.Loading) {
                            _stocksFilterUiState.update { state -> state.copy(isLoading = true) }
                        }
                        if (resultDateByStockCodeAndPeriod is Result.Error && resultDateByStockCodeAndPeriod.code != 404) {
                            _stocksFilterUiState.update { state -> state.copy(isLoading = false, error = resultDateByStockCodeAndPeriod.error) }
                        }
                        if (resultDateByStockCodeAndPeriod is Result.Success<BalanceSheetDateStockWithDates>) {
                            val balanceSheetDateData = resultDateByStockCodeAndPeriod.data
                            val balanceSheetDateFilterData = balanceSheetDateData.dates.find { it.period == selectedPeriod }!!
                            stockFilterList.add(StockFilter(stockResponse, balanceSheetRatiosData, BalanceSheetDateStockWithDates(balanceSheetDateData.stock, listOf(balanceSheetDateFilterData))))
                        }
                    }
                }
                if (stockFilterList.isEmpty()) {
                    _stocksFilterUiState.update { state -> state.copy(isLoading = false, data = StocksFilter(emptyList(), emptyList())) }
                    return@launch
                }
                _stocksFilterUiState.update { state -> state.copy(isLoading = false, data = StocksFilter(stockFilterList, stockFilterList)) }
                _selectedIndexStockFilterState.update { Constant.ALL }
                _selectedSectorStockFilterState.update { Constant.ALL }
            }
        }
    }

    fun refreshFilterBalanceSheetStocks() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchFilterBalanceSheetStocks()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun filterQuery(query: String) {
        val filteredList = _stocksFilterUiState.value.data?.defaultList?.filter { stockFilter ->
            stockFilter.stock.code?.contains(query, ignoreCase = true) == true
        } ?: emptyList()
        _stocksFilterUiState.update { state -> state.copy(data = state.data?.copy(filteredList = filteredList)) }
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

    fun onClickCompareFilterButton(period: YearMonth, selectedIndexName: String, selectedSectorName: String) {
        _showFilterBottomSheetState.update { false }
        _selectedPeriodFilterState.update { period }
        _selectedIndexStockFilterState.update { selectedIndexName }
        _selectedSectorStockFilterState.update { selectedSectorName }

        viewModelScope.launch {
            val selectedPeriod = "${period.year}/${period.month}"
            // BalanceSheetWithRatios
            val resultBalanceSheetRatiosList = localRepository.getBalanceSheetRatiosList(selectedPeriod).last()
            if (resultBalanceSheetRatiosList is Result.Loading) {
                _stocksFilterUiState.update { state -> state.copy(isLoading = true) }
            }
            if (resultBalanceSheetRatiosList is Result.Error) {
                _stocksFilterUiState.update { state -> state.copy(isLoading = false, error = resultBalanceSheetRatiosList.error) }
            }
            if (resultBalanceSheetRatiosList is Result.Success<List<BalanceSheetRatioEntity>>) {
                // Filter selected period
                val balanceSheetRatiosListData = resultBalanceSheetRatiosList.data.filter { balanceSheetRatiosData -> balanceSheetRatiosData.period == selectedPeriod }
                if (balanceSheetRatiosListData.isEmpty()) {
                    _stocksFilterUiState.update { state -> state.copy(isLoading = false, data = StocksFilter(emptyList(), emptyList())) }
                    return@launch
                }
                val stockFilterList = ArrayList<StockFilter>()
                balanceSheetRatiosListData.forEach { balanceSheetRatiosData ->
                    val stockCode = balanceSheetRatiosData.stockCode

                    // Filter stocks by index and sector
                    val resultStockByIndexAndSector = if (selectedIndexName == Constant.ALL && selectedSectorName == Constant.ALL) {
                        localRepository.getStock(stockCode).last()
                    } else if (selectedIndexName != Constant.ALL && selectedSectorName == Constant.ALL) {
                        localRepository.getStockByIndex(stockCode, selectedIndexName).last()
                    } else if (selectedIndexName == Constant.ALL && selectedSectorName != Constant.ALL) {
                        localRepository.getStockBySector(stockCode, selectedSectorName).last()
                    } else {
                        localRepository.getStockByIndexAndSector(stockCode, selectedIndexName, selectedSectorName).last()
                    }

                    if (resultStockByIndexAndSector is Result.Loading) {
                        _stocksFilterUiState.update { state -> state.copy(isLoading = true) }
                    }
                    if (resultStockByIndexAndSector is Result.Error && resultStockByIndexAndSector.code != 404) {
                        _stocksFilterUiState.update { state -> state.copy(isLoading = false, error = resultStockByIndexAndSector.error) }
                    }
                    if (resultStockByIndexAndSector is Result.Success<StockResponse>) {
                        val stockResponse = resultStockByIndexAndSector.data

                        // Filter dates by stock and period
                        val resultDateByStockCodeAndPeriod = localRepository.getBalanceSheetDateStockWithDatesByStockCodeAndPeriod(stockCode, selectedPeriod).last()
                        if (resultDateByStockCodeAndPeriod is Result.Loading) {
                            _stocksFilterUiState.update { state -> state.copy(isLoading = true) }
                        }
                        if (resultDateByStockCodeAndPeriod is Result.Error && resultDateByStockCodeAndPeriod.code != 404) {
                            _stocksFilterUiState.update { state -> state.copy(isLoading = false, error = resultDateByStockCodeAndPeriod.error) }
                        }
                        if (resultDateByStockCodeAndPeriod is Result.Success<BalanceSheetDateStockWithDates>) {
                            val balanceSheetDateData = resultDateByStockCodeAndPeriod.data
                            val balanceSheetDateByPeriod = balanceSheetDateData.dates.find { it.period == selectedPeriod }!!
                            stockFilterList.add(StockFilter(stockResponse, balanceSheetRatiosData, BalanceSheetDateStockWithDates(balanceSheetDateData.stock, listOf(balanceSheetDateByPeriod))))
                        }
                    }
                }
                if (stockFilterList.isEmpty()) {
                    _stocksFilterUiState.update { state -> state.copy(isLoading = false, data = StocksFilter(emptyList(), emptyList())) }
                    return@launch
                }
                _stocksFilterUiState.update { state -> state.copy(isLoading = false, data = StocksFilter(stockFilterList, stockFilterList)) }
            }
        }
    }

    fun setShowFilterBottomSheetState(value: Boolean) {
        _showFilterBottomSheetState.update { value }
    }

    fun downloadAllBalanceSheets() {
        val selectedPeriod = "${_selectedPeriodFilterState.value.year}/${_selectedPeriodFilterState.value.month}"
        viewModelScope.launch {
            handleResult(
                action = { remoteRepository.fetchBalanceSheetsByPeriod(selectedPeriod) },
                onLoading = { updateUiState(_completedAllDownloadsUiState, isLoading = true) },
                onError = { error -> updateUiState(_completedAllDownloadsUiState, error = error.error) },
                onSuccess = { insertBalanceSheets(it) }
            )
        }
    }

    private suspend fun insertBalanceSheets(entities: List<BalanceSheetEntity>) {
        val selectedPeriod = "${_selectedPeriodFilterState.value.year}/${_selectedPeriodFilterState.value.month}"
        handleResult(
            action = { localRepository.insertBalanceSheetsByPeriod(selectedPeriod, entities) },
            onLoading = { updateUiState(_completedAllDownloadsUiState, isLoading = true) },
            onError = { error -> updateUiState(_completedAllDownloadsUiState, error = error.error) },
            onSuccess = { updateUiState(_completedAllDownloadsUiState, data = true) }
        )
    }

    fun setSelectedStocks(value: List<StockFilter>) {
        viewModelScope.launch {
            _selectedStocksUiState.update { value }
        }
    }
}