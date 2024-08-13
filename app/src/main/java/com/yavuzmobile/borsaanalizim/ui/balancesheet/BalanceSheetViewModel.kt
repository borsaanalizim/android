package com.yavuzmobile.borsaanalizim.ui.balancesheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateEntity
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.IsYatirimRepository
import com.yavuzmobile.borsaanalizim.data.model.CompanyCard
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import com.yavuzmobile.borsaanalizim.model.BalanceSheet
import com.yavuzmobile.borsaanalizim.model.FinancialStatement
import com.yavuzmobile.borsaanalizim.model.PriceDate
import com.yavuzmobile.borsaanalizim.model.PriceDateHistory
import com.yavuzmobile.borsaanalizim.model.UiState
import com.yavuzmobile.borsaanalizim.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BalanceSheetViewModel @Inject constructor(
    private val isYatirimRepository: IsYatirimRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _balanceSheetDatesState = MutableStateFlow(UiState<List<BalanceSheetDateEntity>>())
    private val balanceSheetDatesState: StateFlow<UiState<List<BalanceSheetDateEntity>>> = _balanceSheetDatesState.asStateFlow()

    private val _priceDateHistoryState = MutableStateFlow(UiState<PriceDateHistory>())
    val priceDateHistoryState: StateFlow<UiState<PriceDateHistory>> = _priceDateHistoryState.asStateFlow()

    private val _companyCardState = MutableStateFlow(UiState<CompanyCard>())

    private val _balanceSheetUiState = MutableStateFlow(UiState<List<BalanceSheet>>())
    val balanceSheetUiState: StateFlow<UiState<List<BalanceSheet>>> = _balanceSheetUiState.asStateFlow()

    private val _financialStatementUiState = MutableStateFlow(UiState<FinancialStatement>())
    val financialStatementUiState: StateFlow<UiState<FinancialStatement>> = _financialStatementUiState.asStateFlow()

    val periodPrice = mutableMapOf<String, Double?>()

    fun fetchData(code: String) {
        viewModelScope.launch {
            async { localRepository.getBalanceSheetOfStockDate(code) }.await().collect {
                when(it) {
                    is Result.Loading -> _balanceSheetDatesState.update { state -> state.copy(true) }
                    is Result.Error -> _balanceSheetDatesState.update { state -> state.copy(false, error = it.error) }
                    is Result.Success -> _balanceSheetDatesState.update { state -> state.copy(false, data = it.data) }
                }
            }
            async { isYatirimRepository.fetchPriceHistory(code) }.await().collect {
                when(it) {
                    is Result.Loading -> _priceDateHistoryState.update { state -> state.copy(true) }
                    is Result.Error -> _priceDateHistoryState.update { state -> state.copy(false, error = it.error) }
                    is Result.Success -> {
                        val priceDateHistory = PriceDateHistory(it.data.data?.map { mappedData -> PriceDate((mappedData[0] as Double).toLong(), mappedData[1] as Double) }, it.data.timestamp)
                        _priceDateHistoryState.update { state -> state.copy(false, data = priceDateHistory) }
                    }
                }
            }

            async { isYatirimRepository.fetchBalanceSheet(code) }.await().collect {
                when(it) {
                    is Result.Loading -> _balanceSheetUiState.update { state -> state.copy(true) }
                    is Result.Error -> _balanceSheetUiState.update { state -> state.copy(false, error = it.error) }
                    is Result.Success -> {
                        periodPrice.clear()
                        it.data.period?.forEach { balanceSheetPeriod ->
                            balanceSheetDatesState.value.data?.find { balanceSheetDate -> balanceSheetDate.period == balanceSheetPeriod } ?.let { matchedBalanceSheetDate ->
                                DateUtil.fromString(matchedBalanceSheetDate.publishedAt) ?.let { requireDate ->
                                    priceDateHistoryState.value.data?.data?.find { priceDateHistory ->
                                        DateUtil.fromTimestamp(priceDateHistory.timestamp ?: 0L)?.let { timestampToDate ->
                                            DateUtil.getYearMonthDayDateString(timestampToDate).equals(DateUtil.getYearMonthDayDateString(requireDate))
                                        } ?: kotlin.run { false }
                                    }?.let { matched ->
                                        periodPrice[balanceSheetPeriod] = matched.price
                                    }
                                }
                            } ?: kotlin.run {
                                val periodSplit = balanceSheetPeriod.split("/")
                                val year = periodSplit.first().toInt()
                                val month = periodSplit.last().toInt()

                                val matchedYear = if (month == 12) "${year + 1}" else "$year"
                                val matchedMonth = if (month == 12) 1 else month + 1

                                var matched: PriceDate? = null
                                var day = 1

                                while (matched == null && day <= 31) {
                                    val dayString = if (day > 9) "$day" else "0$day"
                                    val monthString = if (matchedMonth > 9) "$matchedMonth" else "0$matchedMonth"
                                    val requireData = "$matchedYear-$monthString-${dayString}T00:00:00Z"

                                    matched = priceDateHistoryState.value.data?.data?.find { priceDateHistory ->
                                        DateUtil.fromTimestamp(priceDateHistory.timestamp ?: 0L)?.let { timestampToDate ->
                                            DateUtil.fromString(requireData)?.let { requireDate ->
                                                if (requireDate < timestampToDate) {
                                                    true
                                                } else {
                                                    DateUtil.getYearMonthDayDateString(timestampToDate).equals(DateUtil.getYearMonthDayDateString(requireDate))
                                                }
                                            }
                                        } ?: kotlin.run { false }
                                    }

                                    if (matched == null) {
                                        day++
                                    }
                                }

                                matched?.let { requireMatched ->
                                    periodPrice[balanceSheetPeriod] = requireMatched.price
                                }

                            }
                        }
                        _companyCardState.update { state -> state.copy(false, data = it.data) }

                    }
                }
            }
            balanceSheetCalculations(_companyCardState.value.data)
        }
    }

    fun fetchBalanceSheetJson(code: String) {
        viewModelScope.launch {
            isYatirimRepository.fetchBalanceSheetJson(code)
                .collect {
                    when(it) {
                        is Result.Loading -> { _financialStatementUiState.update { state -> state.copy(isLoading = true) } }
                        is Result.Error -> { _financialStatementUiState.update { state -> state.copy(isLoading = false, error = it.error) } }
                        is Result.Success -> { _financialStatementUiState.update { state -> state.copy(isLoading = false, it.data) } }
                    }
                }
        }
    }

    private suspend fun balanceSheetCalculations(companyCard: CompanyCard?) = withContext(Dispatchers.IO) {
        val balanceSheetList = ArrayList<BalanceSheet>()
        companyCard?.balanceSheets?.forEachIndexed { index, balanceSheet ->
            val period = companyCard.period!![index]
            val periodPrice = periodPrice[period].toString().toDoubleOrDefault()
            val marketValue = balanceSheet.paidCapital.toDoubleOrDefault() * periodPrice
            val bookValue = balanceSheet.equitiesOfParentCompany.toDoubleOrDefault()
            val eps = balanceSheet.previousYearsProfitAndLoss.toDoubleOrDefault() / balanceSheet.paidCapital.toDoubleOrDefault()
            val netDebt = (balanceSheet.financialDebtsShort.toDoubleOrDefault() + balanceSheet.financialDebtsLong.toDoubleOrDefault()) - (balanceSheet.cashAndCashEquivalents.toDoubleOrDefault() + balanceSheet.financialInvestments.toDoubleOrDefault())
            val companyValue = marketValue - netDebt
            val ebitda = balanceSheet.grossProfitAndLoss.toDoubleOrDefault() + balanceSheet.generalAndAdministrativeExpenses.toDoubleOrDefault() + balanceSheet.marketingSalesAndDistributionExpenses.toDoubleOrDefault() + balanceSheet.depreciationAndAmortization.toDoubleOrDefault()
            val netOperatingProfitAndLoss = balanceSheet.netOperatingProfitAndLoss.toDoubleOrDefault()
            val netSales = balanceSheet.salesIncome.toDoubleOrDefault()

            if (index == 0) {
                val todayPeriodPrice = priceDateHistoryState.value.data?.data?.lastOrNull()?.price.toString().toDoubleOrDefault()
                val todayMarketValue = balanceSheet.paidCapital.toDoubleOrDefault() * todayPeriodPrice
                val todayCompanyValue = todayMarketValue - netDebt

                val todayPeriod = "Bugün"
                val todayMarketBookAndBookValue = (todayMarketValue / bookValue)
                val todayPriceAndEarning = (todayPeriodPrice / eps)
                val todayCompanyValueAndEbitda = (todayCompanyValue / ebitda)
                val todayMarketValueAndNetOperatingProfit = (todayMarketValue / netOperatingProfitAndLoss)
                val todayCompanyValueAndNetSales = (todayCompanyValue / netSales)
                val todayNetOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / todayMarketValue) * 100

                balanceSheetList.add(
                    BalanceSheet(
                        todayPeriod,
                        String.format(Locale.getDefault(),"%.2f", todayMarketBookAndBookValue),
                        String.format(Locale.getDefault(),"%.2f", todayPriceAndEarning),
                        String.format(Locale.getDefault(),"%.2f", todayCompanyValueAndEbitda),
                        String.format(Locale.getDefault(),"%.2f", todayMarketValueAndNetOperatingProfit),
                        String.format(Locale.getDefault(),"%.2f", todayCompanyValueAndNetSales),
                        String.format(Locale.getDefault(),"%.2f", todayNetOperatingProfitAndMarketValue)
                    )
                )
            }

            val marketBookAndBookValue = (marketValue / bookValue)
            val priceAndEarning = (periodPrice / eps)
            val companyValueAndEbitda = (companyValue / ebitda)
            val marketValueAndNetOperatingProfit = (marketValue / netOperatingProfitAndLoss)
            val companyValueAndNetSales = (companyValue / netSales)
            val netOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / marketValue) * 100

            balanceSheetList.add(
                BalanceSheet(
                    period,
                    String.format(Locale.getDefault(),"%.2f", marketBookAndBookValue),
                    String.format(Locale.getDefault(),"%.2f", priceAndEarning),
                    String.format(Locale.getDefault(),"%.2f", companyValueAndEbitda),
                    String.format(Locale.getDefault(),"%.2f", marketValueAndNetOperatingProfit),
                    String.format(Locale.getDefault(),"%.2f", companyValueAndNetSales),
                    String.format(Locale.getDefault(),"%.2f", netOperatingProfitAndMarketValue)
                )
            )
        }
        _balanceSheetUiState.update { state -> state.copy(isLoading = false, data = balanceSheetList) }
    }
}