package com.yavuzmobile.borsaanalizim.ui.balancesheet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateEntity
import com.yavuzmobile.borsaanalizim.data.model.BalanceSheetResponse
import com.yavuzmobile.borsaanalizim.data.model.CompanyCard
import com.yavuzmobile.borsaanalizim.data.repository.local.LocalRepository
import com.yavuzmobile.borsaanalizim.data.repository.remote.IsYatirimRepository
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import com.yavuzmobile.borsaanalizim.model.BalanceSheet
import com.yavuzmobile.borsaanalizim.model.PriceDate
import com.yavuzmobile.borsaanalizim.model.PriceDateHistory
import com.yavuzmobile.borsaanalizim.model.UiState
import com.yavuzmobile.borsaanalizim.model.YearMonth
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
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BalanceSheetViewModel @Inject constructor(
    private val isYatirimRepository: IsYatirimRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _balanceSheetDatesState = MutableStateFlow(UiState<List<BalanceSheetDateEntity>>())
    val balanceSheetDatesState: StateFlow<UiState<List<BalanceSheetDateEntity>>> = _balanceSheetDatesState.asStateFlow()

    private val _priceDateHistoryState = MutableStateFlow(UiState<PriceDateHistory>())
    val priceDateHistoryState: StateFlow<UiState<PriceDateHistory>> = _priceDateHistoryState.asStateFlow()

    private val _companyCardState = MutableStateFlow(UiState<CompanyCard>())
    val companyCardState: StateFlow<UiState<CompanyCard>> = _companyCardState.asStateFlow()

    private val _balanceSheetUiState = MutableStateFlow(UiState<List<BalanceSheet>>())
    val balanceSheetUiState: StateFlow<UiState<List<BalanceSheet>>> = _balanceSheetUiState.asStateFlow()

    // priceDateHistoryState verisini Map<String, PriceDate> olarak önceden dönüştür.
    val priceDateMap = priceDateHistoryState.value.data?.data?.associateBy { priceDateHistory ->
        DateUtil.getYearMonthDayDateString(DateUtil.fromTimestamp(priceDateHistory.timestamp ?: 0L)!!)
    }

    val periodPrice = mutableMapOf<String, Double?>()

    val cache = mutableMapOf<String, Double?>()

    fun fetchData(code: String) {
        viewModelScope.launch {
            Log.i("FIRST_LOG", "TRUE")
            async { localRepository.getBalanceSheetOfStockDate(code) }.await().collect {
                when (it) {
                    is Result.Loading -> _balanceSheetDatesState.update { state -> state.copy(true) }
                    is Result.Error -> _balanceSheetDatesState.update { state -> state.copy(false, error = it.error) }
                    is Result.Success -> _balanceSheetDatesState.update { state -> state.copy(false, data = it.data) }
                }
            }
            Log.i("SECOND_LOG", "TRUE")
            async { isYatirimRepository.fetchPriceHistory(code) }.await().collect {
                when (it) {
                    is Result.Loading -> _priceDateHistoryState.update { state -> state.copy(true) }
                    is Result.Error -> _priceDateHistoryState.update { state -> state.copy(false, error = it.error) }
                    is Result.Success -> {
                        val priceDateHistory = PriceDateHistory(it.data.data?.map { mappedData ->
                            PriceDate(
                                (mappedData[0] as Double).toLong(),
                                mappedData[1] as Double
                            )
                        }, it.data.timestamp)
                        _priceDateHistoryState.update { state -> state.copy(false, data = priceDateHistory) }
                    }
                }
            }
            Log.i("THIRD_LOG", "TRUE")
            val balanceSheetPeriods: List<YearMonth> = DateUtil.getLastTwelvePeriods()
            isYatirimRepository.getFinancialStatement(
                stockCode = code,
                year1 = balanceSheetPeriods[0].year,
                period1 = balanceSheetPeriods[0].month,
                year2 = balanceSheetPeriods[1].year,
                period2 = balanceSheetPeriods[1].month,
                year3 = balanceSheetPeriods[2].year,
                period3 = balanceSheetPeriods[2].month,
                year4 = balanceSheetPeriods[3].year,
                period4 = balanceSheetPeriods[3].month
            ).collect { firsResult ->
                when(firsResult) {
                    is Result.Loading -> _balanceSheetUiState.update { state -> state.copy(isLoading = true) }
                    is Result.Error -> _balanceSheetUiState.update { state -> state.copy(isLoading = false, error = firsResult.error) }
                    is Result.Success -> {
                        isYatirimRepository.getFinancialStatement(
                            stockCode = code,
                            year1 = balanceSheetPeriods[4].year,
                            period1 = balanceSheetPeriods[4].month,
                            year2 = balanceSheetPeriods[5].year,
                            period2 = balanceSheetPeriods[5].month,
                            year3 = balanceSheetPeriods[6].year,
                            period3 = balanceSheetPeriods[6].month,
                            year4 = balanceSheetPeriods[7].year,
                            period4 = balanceSheetPeriods[7].month
                        ).collect { secondResult ->
                            when(secondResult) {
                                is Result.Loading -> _balanceSheetUiState.update { state -> state.copy(isLoading = true) }
                                is Result.Error -> _balanceSheetUiState.update { state -> state.copy(isLoading = false, error = secondResult.error) }
                                is Result.Success -> {
                                    isYatirimRepository.getFinancialStatement(
                                        stockCode = code,
                                        year1 = balanceSheetPeriods[8].year,
                                        period1 = balanceSheetPeriods[8].month,
                                        year2 = balanceSheetPeriods[9].year,
                                        period2 = balanceSheetPeriods[9].month,
                                        year3 = balanceSheetPeriods[10].year,
                                        period3 = balanceSheetPeriods[10].month,
                                        year4 = balanceSheetPeriods[11].year,
                                        period4 = balanceSheetPeriods[11].month
                                    ).collect { thirdResult ->
                                        when(thirdResult) {
                                            is Result.Loading -> _balanceSheetUiState.update { state -> state.copy(isLoading = true) }
                                            is Result.Error -> _balanceSheetUiState.update { state -> state.copy(isLoading = false, error = thirdResult.error) }
                                            is Result.Success -> {
                                                Log.i("FOURTH_LOG", "TRUE")
                                                balanceSheetPeriods.forEach { balanceSheetPeriod ->
                                                    val balanceSheetPeriodString = "${balanceSheetPeriod.year}/${balanceSheetPeriod.month}"

                                                    // Önce cache'de var mı kontrol et
                                                    periodPrice[balanceSheetPeriodString] = periodPrice[balanceSheetPeriodString] ?: run {
                                                        val year = balanceSheetPeriod.year.toInt()
                                                        val month = balanceSheetPeriod.month.toInt()

                                                        val matchedYear = if (month == 12) "${year + 1}" else "$year"
                                                        val matchedMonth = if (month == 12) 1 else month + 1

                                                        // Bu map işlemi gereksiz tekrarları engeller
                                                        val priceDateMap = priceDateHistoryState.value.data?.data?.associateBy {
                                                            DateUtil.fromTimestamp(it.timestamp ?: 0L)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                                                        }

                                                        var matchedPrice: Double? = null

                                                        // `generateSequence` kullanarak 31 gün boyunca tarihleri oluştur ve kontrol et
                                                        generateSequence(LocalDate.of(matchedYear.toInt(), matchedMonth, 1)) { it.plusDays(1) }
                                                            .take(31)
                                                            .firstOrNull { date ->
                                                                matchedPrice = priceDateMap?.get(date)?.price
                                                                matchedPrice != null
                                                            }

                                                        matchedPrice
                                                    }
                                                }
                                                Log.i("FIFTH_LOG", "TRUE")

                                                val periodItemDescTrList = mutableMapOf<String, String>()
                                                val periodItemDescEngList = mutableMapOf<String, String>()
                                                val allPeriods = ArrayList<Map<String, String>>()
                                                val period1ValueList = mutableMapOf<String, String>()
                                                val period2ValueList = mutableMapOf<String, String>()
                                                val period3ValueList = mutableMapOf<String, String>()
                                                val period4ValueList = mutableMapOf<String, String>()
                                                val period5ValueList = mutableMapOf<String, String>()
                                                val period6ValueList = mutableMapOf<String, String>()
                                                val period7ValueList = mutableMapOf<String, String>()
                                                val period8ValueList = mutableMapOf<String, String>()
                                                val period9ValueList = mutableMapOf<String, String>()
                                                val period10ValueList = mutableMapOf<String, String>()
                                                val period11ValueList = mutableMapOf<String, String>()
                                                val period12ValueList = mutableMapOf<String, String>()

                                                firsResult.data.financialStatementList?.forEach { financialStatement ->
                                                    periodItemDescTrList[financialStatement.itemCode.orEmpty()] = financialStatement.itemDescTr.orEmpty()
                                                    periodItemDescEngList[financialStatement.itemCode.orEmpty()] = financialStatement.itemDescEng.orEmpty()
                                                    period1ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value1.orEmpty()
                                                    period2ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value2.orEmpty()
                                                    period3ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value3.orEmpty()
                                                    period4ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value4.orEmpty()
                                                }

                                                secondResult.data.financialStatementList?.forEach { financialStatement ->
                                                    period5ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value1.orEmpty()
                                                    period6ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value2.orEmpty()
                                                    period7ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value3.orEmpty()
                                                    period8ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value4.orEmpty()
                                                }

                                                thirdResult.data.financialStatementList?.forEach { financialStatement ->
                                                    period9ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value1.orEmpty()
                                                    period10ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value2.orEmpty()
                                                    period11ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value3.orEmpty()
                                                    period12ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value4.orEmpty()
                                                }

                                                Log.i("SIXTH_LOG", "TRUE")

                                                allPeriods.add(period1ValueList)
                                                allPeriods.add(period2ValueList)
                                                allPeriods.add(period3ValueList)
                                                allPeriods.add(period4ValueList)
                                                allPeriods.add(period5ValueList)
                                                allPeriods.add(period6ValueList)
                                                allPeriods.add(period7ValueList)
                                                allPeriods.add(period8ValueList)
                                                allPeriods.add(period9ValueList)
                                                allPeriods.add(period10ValueList)
                                                allPeriods.add(period11ValueList)
                                                allPeriods.add(period12ValueList)

                                                Log.i("SEVENTH_LOG", "TRUE")

                                                val listData = allPeriods.map { period ->
                                                    BalanceSheetResponse(
                                                        period["1A"],
                                                        period["1AK"],
                                                        period["2OA"],
                                                        period["2N"],
                                                        period["2O"],
                                                        period["2BA"],
                                                        period["2AA"],
                                                        period["1AA"],
                                                        period["1BC"],
                                                        period["3H"],
                                                        period["3C"],
                                                        period["3D"],
                                                        period["2OCE"],
                                                        period["2OCF"],
                                                        period["3DF"],
                                                        period["4B"],
                                                        period["3CAD"],
                                                        period["3IB"],
                                                        period["3DA"],
                                                        period["3CA"],
                                                        period["3DA"],
                                                        period["3DC"],
                                                        period["4CAB"],
                                                        period["2A"],
                                                        period["2B"]
                                                    )
                                                }

                                                Log.i("EIGHTH_LOG", "TRUE")

                                                val mappedBalanceSheetPeriods = balanceSheetPeriods.map { mappedPeriod -> "${mappedPeriod.year}/${mappedPeriod.month}" }

                                                Log.i("NINTH_LOG", "TRUE")

                                                val companyCard = CompanyCard(code, mappedBalanceSheetPeriods, listData)
                                                _companyCardState.update { state -> state.copy(isLoading = false, data = companyCard) }
                                                balanceSheetCalculations(companyCard)

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

    private suspend fun balanceSheetCalculations(companyCard: CompanyCard?) = withContext(Dispatchers.IO) {
        val balanceSheetList = ArrayList<BalanceSheet>()
        companyCard?.balanceSheetResponses?.forEachIndexed { index, balanceSheet ->
            val period = companyCard.period!![index]
            if (periodPrice[period] == null) {
                return@forEachIndexed
            }
            val periodPrice = periodPrice[period].toString().toDoubleOrDefault()
            val marketValue = balanceSheet.paidCapital.toDoubleOrDefault() * periodPrice
            val bookValue = balanceSheet.equitiesOfParentCompany.toDoubleOrDefault()
            val eps = balanceSheet.previousYearsProfitAndLoss.toDoubleOrDefault() / balanceSheet.paidCapital.toDoubleOrDefault()
            val netDebt = (balanceSheet.financialDebtsShort.toDoubleOrDefault() + balanceSheet.financialDebtsLong.toDoubleOrDefault()) - (balanceSheet.cashAndCashEquivalents.toDoubleOrDefault() + balanceSheet.financialInvestments.toDoubleOrDefault())
            val companyValue = marketValue - netDebt
            val ebitda = balanceSheet.grossProfitAndLoss.toDoubleOrDefault() + balanceSheet.generalAndAdministrativeExpenses.toDoubleOrDefault() + balanceSheet.marketingSalesAndDistributionExpenses.toDoubleOrDefault() + balanceSheet.depreciationAndAmortization.toDoubleOrDefault()
            val netOperatingProfitAndLoss = balanceSheet.netOperatingProfitAndLoss.toDoubleOrDefault()
            val netSales = balanceSheet.salesIncome.toDoubleOrDefault()

            val isNan = balanceSheet.paidCapital.isNullOrEmpty() || balanceSheet.paidCapital == "0"

            if (index == 0 && isNan) {
                return@forEachIndexed
            }

            if (index == 0 || (index == 1 && !isNan && balanceSheetList.find { it.period == "Bugün" } == null)) {
                val todayPeriod = "Bugün"
                val todayPeriodPrice = priceDateHistoryState.value.data?.data?.lastOrNull()?.price.toString().toDoubleOrDefault()
                val todayMarketValue = balanceSheet.paidCapital.toDoubleOrDefault() * todayPeriodPrice
                val todayCompanyValue = todayMarketValue - netDebt

                val todayMarketBookAndBookValue = (todayMarketValue / bookValue)
                val todayPriceAndEarning = (todayPeriodPrice / eps)
                val todayCompanyValueAndEbitda = (todayCompanyValue / ebitda)
                val todayMarketValueAndNetOperatingProfit = (todayMarketValue / netOperatingProfitAndLoss)
                val todayCompanyValueAndNetSales = (todayCompanyValue / netSales)
                val todayNetOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / todayMarketValue) * 100

                balanceSheetList.add(
                    BalanceSheet(
                        todayPeriod,
                        todayPeriodPrice,
                        String.format(Locale.getDefault(), "%.2f", todayMarketBookAndBookValue),
                        String.format(Locale.getDefault(), "%.2f", todayPriceAndEarning),
                        String.format(Locale.getDefault(), "%.2f", todayCompanyValueAndEbitda),
                        String.format(Locale.getDefault(), "%.2f", todayMarketValueAndNetOperatingProfit),
                        String.format(Locale.getDefault(), "%.2f", todayCompanyValueAndNetSales),
                        String.format(Locale.getDefault(), "%.2f", todayNetOperatingProfitAndMarketValue)
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
                    periodPrice,
                    String.format(Locale.getDefault(), "%.2f", marketBookAndBookValue),
                    String.format(Locale.getDefault(), "%.2f", priceAndEarning),
                    String.format(Locale.getDefault(), "%.2f", companyValueAndEbitda),
                    String.format(Locale.getDefault(), "%.2f", marketValueAndNetOperatingProfit),
                    String.format(Locale.getDefault(), "%.2f", companyValueAndNetSales),
                    String.format(Locale.getDefault(), "%.2f", netOperatingProfitAndMarketValue)
                )
            )
        }
        Log.i("TENTH_LOG", "TRUE")
        _balanceSheetUiState.update { state -> state.copy(isLoading = false, data = balanceSheetList) }
    }

}