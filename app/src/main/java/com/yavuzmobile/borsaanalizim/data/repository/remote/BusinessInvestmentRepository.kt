package com.yavuzmobile.borsaanalizim.data.repository.remote

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.api.BusinessInvestmentApi
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDao
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatiosEntity
import com.yavuzmobile.borsaanalizim.data.model.BalanceSheetDateResponse
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import com.yavuzmobile.borsaanalizim.model.FinancialStatement
import com.yavuzmobile.borsaanalizim.model.YearMonth
import com.yavuzmobile.borsaanalizim.util.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessInvestmentRepository @Inject constructor(private val api: BusinessInvestmentApi, private val balanceSheetDao: BalanceSheetDao) {

    private suspend fun getFinancialStatement(
        stockCode: String,
        financialGroup: String = "XI_29",
        year1: String,
        period1: String,
        year2: String,
        period2: String,
        year3: String,
        period3: String,
        year4: String,
        period4: String
    ): Flow<Result<FinancialStatement>> = flow {
        emit(Result.Loading())
        try {
            val response = api.getFinancialStatement(
                stockCode,
                "TRY",
                financialGroup,
                year1,
                period1,
                year2,
                period2,
                year3,
                period3,
                year4,
                period4
            )
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    if (responseBody.isSuccess == true) {
                        if (responseBody.value.isNullOrEmpty()) {
                            val responseFinance = api.getFinancialStatement(
                                stockCode,
                                "TRY",
                                "UFRS_K",
                                year1,
                                period1,
                                year2,
                                period2,
                                year3,
                                period3,
                                year4,
                                period4
                            )
                            if (responseFinance.isSuccessful) {
                                responseFinance.body()?.let { responseFinanceBody ->
                                    emit(Result.Success(FinancialStatement(responseFinanceBody.value)))
                                } ?: kotlin.run {
                                    emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                                }
                            } else {
                                emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                            }
                        } else {
                            emit(Result.Success(FinancialStatement(responseBody.value)))
                        }
                    } else {
                        emit(
                            Result.Error(
                                responseBody.errorCode.toString().toInt(),
                                responseBody.errorDescription.toString()
                            )
                        )
                    }
                } ?: kotlin.run {
                    emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                }
            } else {
                emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getFourPeriodFinancialStatementList(code: String, balanceSheetDateResponse: BalanceSheetDateResponse): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        val balanceSheetPeriods: List<YearMonth> = DateUtil.getLastTwelvePeriods()
        try {
            getFinancialStatement(
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
                    is Result.Loading -> emit(Result.Loading())
                    is Result.Error -> emit(Result.Error(firsResult.code, firsResult.error))
                    is Result.Success -> {

                        val periodItemDescTrList = mutableMapOf<String, String>()
                        val periodItemDescEngList = mutableMapOf<String, String>()
                        val allPeriods = ArrayList<Map<String, String>>()
                        val period1ValueList = mutableMapOf<String, String>()
                        val period2ValueList = mutableMapOf<String, String>()
                        val period3ValueList = mutableMapOf<String, String>()
                        val period4ValueList = mutableMapOf<String, String>()

                        firsResult.data.financialStatementList?.forEach { financialStatement ->
                            periodItemDescTrList[financialStatement.itemCode.orEmpty()] = financialStatement.itemDescTr.orEmpty()
                            periodItemDescEngList[financialStatement.itemCode.orEmpty()] = financialStatement.itemDescEng.orEmpty()
                            period1ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value1.orEmpty()
                            period2ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value2.orEmpty()
                            period3ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value3.orEmpty()
                            period4ValueList[financialStatement.itemCode.orEmpty()] = financialStatement.value4.orEmpty()
                        }

                        allPeriods.add(period1ValueList)
                        allPeriods.add(period2ValueList)
                        allPeriods.add(period3ValueList)
                        allPeriods.add(period4ValueList)

                        val mappedBalanceSheetPeriods = balanceSheetPeriods.map { mappedPeriod -> "${mappedPeriod.year}/${mappedPeriod.month}" }

                        val balanceSheetResponseList = allPeriods.mapIndexed { index, period ->
                            BalanceSheetEntity(
                                stockCode = code,
                                period = mappedBalanceSheetPeriods[index],
                                currentAssets = period["1A"].orEmpty(),
                                longTermAssets = period["1AK"].orEmpty(),
                                paidCapital = period["2OA"].orEmpty(),
                                equities = period["2N"].orEmpty(),
                                equitiesOfParentCompany = period["2O"].orEmpty(),
                                financialDebtsLong = period["2BA"].orEmpty(),
                                financialDebtsShort = period["2AA"].orEmpty(),
                                cashAndCashEquivalents = period["1AA"].orEmpty(),
                                financialInvestments = period["1BC"].orEmpty(),
                                netOperatingProfitAndLoss = period["3H"].orEmpty(),
                                salesIncome = period["3C"].orEmpty(),
                                grossProfitAndLoss = period["3D"].orEmpty(),
                                previousYearsProfitAndLoss = period["2OCE"].orEmpty(),
                                netProfitAndLossPeriod = period["2OCF"].orEmpty(),
                                operatingProfitAndLoss = period["3DF"].orEmpty(),
                                depreciationExpenses = period["4B"].orEmpty(),
                                otherExpenses = period["3CAD"].orEmpty(),
                                periodTaxIncomeAndExpense = period["3IB"].orEmpty(),
                                generalAndAdministrativeExpenses = period["3DA"].orEmpty(),
                                costOfSales = period["3CA"].orEmpty(),
                                marketingSalesAndDistributionExpenses = period["3DA"].orEmpty(),
                                researchAndDevelopmentExpenses = period["3DC"].orEmpty(),
                                depreciationAndAmortization = period["4CAB"].orEmpty(),
                                shortTermLiabilities = period["2A"].orEmpty(),
                                longTermLiabilities = period["2B"].orEmpty()
                            )
                        }
                        val balanceSheetRatiosList = ArrayList<BalanceSheetRatiosEntity>()
                        balanceSheetResponseList.forEachIndexed { index, balanceSheet ->
                            val period = balanceSheet.period
                            val periodPrice = balanceSheetDateResponse.dates?.find { datePeriod -> datePeriod.period == period }?.let { datePeriodNotNull -> datePeriodNotNull.price.toString().toDoubleOrDefault() } ?: kotlin.run { if (index == 0) balanceSheetDateResponse.lastPrice.toString().toDoubleOrDefault() else null}
                            if (periodPrice == null) return@forEachIndexed
                            val marketValue = balanceSheet.paidCapital.toDoubleOrDefault() * periodPrice
                            val bookValue = balanceSheet.equitiesOfParentCompany.toDoubleOrDefault()
                            val eps = balanceSheet.previousYearsProfitAndLoss.toDoubleOrDefault() / balanceSheet.paidCapital.toDoubleOrDefault()
                            val netDebt = (balanceSheet.financialDebtsShort.toDoubleOrDefault() + balanceSheet.financialDebtsLong.toDoubleOrDefault()) - (balanceSheet.cashAndCashEquivalents.toDoubleOrDefault() + balanceSheet.financialInvestments.toDoubleOrDefault())
                            val companyValue = marketValue - netDebt
                            val ebitda = balanceSheet.grossProfitAndLoss.toDoubleOrDefault() + balanceSheet.generalAndAdministrativeExpenses.toDoubleOrDefault() + balanceSheet.marketingSalesAndDistributionExpenses.toDoubleOrDefault() + balanceSheet.depreciationAndAmortization.toDoubleOrDefault()
                            val netOperatingProfitAndLoss = balanceSheet.netOperatingProfitAndLoss.toDoubleOrDefault()
                            val netSales = balanceSheet.salesIncome.toDoubleOrDefault()

                            val isNan = balanceSheet.paidCapital.isEmpty() || balanceSheet.paidCapital == "0"

                            if (index == 0 && isNan) {
                                return@forEachIndexed
                            }

                            if (index == 0 || (index == 1 && !isNan && balanceSheetRatiosList.find { it.period == "Bugün" } == null)) {
                                val todayPeriod = "Bugün"
                                val todayPeriodPrice = balanceSheetDateResponse.lastPrice.toString().toDoubleOrDefault()
                                val todayMarketValue = balanceSheet.paidCapital.toDoubleOrDefault() * todayPeriodPrice
                                val todayCompanyValue = todayMarketValue - netDebt

                                val todayMarketBookAndBookValue = (todayMarketValue / bookValue)
                                val todayPriceAndEarning = (todayPeriodPrice / eps)
                                val todayCompanyValueAndEbitda = (todayCompanyValue / ebitda)
                                val todayMarketValueAndNetOperatingProfit = (todayMarketValue / netOperatingProfitAndLoss)
                                val todayCompanyValueAndNetSales = (todayCompanyValue / netSales)
                                val todayNetOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / todayMarketValue) * 100

                                balanceSheetRatiosList.add(
                                    BalanceSheetRatiosEntity(
                                        stockCode = code,
                                        period = todayPeriod,
                                        price = String.format(Locale.getDefault(), "%.2f", todayPeriodPrice),
                                        marketBookAndBookValue = String.format(Locale.getDefault(), "%.2f", todayMarketBookAndBookValue),
                                        priceAndEarning = String.format(Locale.getDefault(), "%.2f", todayPriceAndEarning),
                                        companyValueAndEbitda = String.format(Locale.getDefault(), "%.2f", todayCompanyValueAndEbitda),
                                        marketValueAndNetOperatingProfit =String.format(Locale.getDefault(), "%.2f", todayMarketValueAndNetOperatingProfit),
                                        companyValueAndNetSales = String.format(Locale.getDefault(), "%.2f", todayCompanyValueAndNetSales),
                                        netOperatingProfitAndMarketValue = String.format(Locale.getDefault(), "%.2f", todayNetOperatingProfitAndMarketValue),
                                    )
                                )
                            }

                            val marketBookAndBookValue = (marketValue / bookValue)
                            val priceAndEarning = (periodPrice / eps)
                            val companyValueAndEbitda = (companyValue / ebitda)
                            val marketValueAndNetOperatingProfit = (marketValue / netOperatingProfitAndLoss)
                            val companyValueAndNetSales = (companyValue / netSales)
                            val netOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / marketValue) * 100

                            balanceSheetRatiosList.add(
                                BalanceSheetRatiosEntity(
                                    stockCode = code,
                                    period = period,
                                    price = String.format(Locale.getDefault(), "%.2f", periodPrice),
                                    marketBookAndBookValue = String.format(Locale.getDefault(), "%.2f", marketBookAndBookValue),
                                    priceAndEarning = String.format(Locale.getDefault(), "%.2f", priceAndEarning),
                                    companyValueAndEbitda = String.format(Locale.getDefault(), "%.2f", companyValueAndEbitda),
                                    marketValueAndNetOperatingProfit = String.format(Locale.getDefault(), "%.2f", marketValueAndNetOperatingProfit),
                                    companyValueAndNetSales = String.format(Locale.getDefault(), "%.2f", companyValueAndNetSales),
                                    netOperatingProfitAndMarketValue = String.format(Locale.getDefault(), "%.2f", netOperatingProfitAndMarketValue)
                                )
                            )
                        }
                        try {
                            balanceSheetResponseList.forEach { balanceSheetResponse ->
                                val balanceSheetOfStockList = balanceSheetDao.getAllBalanceSheetsOfStock(balanceSheetResponse.stockCode).find { it.stockCode == balanceSheetResponse.stockCode && it.period == balanceSheetResponse.period }
                                if (balanceSheetOfStockList != null) return@forEach
                                balanceSheetDao.insertBalanceSheet(balanceSheetResponse)
                            }
                            balanceSheetRatiosList.forEach { balanceSheetRatios ->
                                val balanceSheetRatiosOfStockList = balanceSheetDao.getBalanceSheetRatiosListOfStock(balanceSheetRatios.stockCode).find { it.stockCode == balanceSheetRatios.stockCode && it.period == balanceSheetRatios.period }
                                if (balanceSheetRatiosOfStockList != null) return@forEach
                                balanceSheetDao.insertBalanceSheetRatios(balanceSheetRatios)
                            }
                            emit(Result.Success(true))
                        } catch (e: Exception) {
                            emit(Result.Error(500, e.message.toString()))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }

    }

    suspend fun getTwelvePeriodFinancialStatementList(code: String, balanceSheetDateResponse: BalanceSheetDateResponse): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        val balanceSheetPeriods: List<YearMonth> = DateUtil.getLastTwelvePeriods()
        try {
            getFinancialStatement(
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
                    is Result.Loading -> emit(Result.Loading())
                    is Result.Error -> emit(Result.Error(firsResult.code, firsResult.error))
                    is Result.Success -> {
                        getFinancialStatement(
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
                                is Result.Loading -> emit(Result.Loading())
                                is Result.Error -> emit(Result.Error(secondResult.code, secondResult.error))
                                is Result.Success -> {
                                    getFinancialStatement(
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
                                            is Result.Loading -> emit(Result.Loading())
                                            is Result.Error -> emit(Result.Error(thirdResult.code, thirdResult.error))
                                            is Result.Success -> {
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

                                                val mappedBalanceSheetPeriods = balanceSheetPeriods.map { mappedPeriod -> "${mappedPeriod.year}/${mappedPeriod.month}" }

                                                val balanceSheetResponseList = allPeriods.mapIndexed { index, period ->
                                                    BalanceSheetEntity(
                                                        stockCode = code,
                                                        period = mappedBalanceSheetPeriods[index],
                                                        currentAssets = period["1A"].orEmpty(),
                                                        longTermAssets = period["1AK"].orEmpty(),
                                                        paidCapital = period["2OA"].orEmpty(),
                                                        equities = period["2N"].orEmpty(),
                                                        equitiesOfParentCompany = period["2O"].orEmpty(),
                                                        financialDebtsLong = period["2BA"].orEmpty(),
                                                        financialDebtsShort = period["2AA"].orEmpty(),
                                                        cashAndCashEquivalents = period["1AA"].orEmpty(),
                                                        financialInvestments = period["1BC"].orEmpty(),
                                                        netOperatingProfitAndLoss = period["3H"].orEmpty(),
                                                        salesIncome = period["3C"].orEmpty(),
                                                        grossProfitAndLoss = period["3D"].orEmpty(),
                                                        previousYearsProfitAndLoss = period["2OCE"].orEmpty(),
                                                        netProfitAndLossPeriod = period["2OCF"].orEmpty(),
                                                        operatingProfitAndLoss = period["3DF"].orEmpty(),
                                                        depreciationExpenses = period["4B"].orEmpty(),
                                                        otherExpenses = period["3CAD"].orEmpty(),
                                                        periodTaxIncomeAndExpense = period["3IB"].orEmpty(),
                                                        generalAndAdministrativeExpenses = period["3DA"].orEmpty(),
                                                        costOfSales = period["3CA"].orEmpty(),
                                                        marketingSalesAndDistributionExpenses = period["3DA"].orEmpty(),
                                                        researchAndDevelopmentExpenses = period["3DC"].orEmpty(),
                                                        depreciationAndAmortization = period["4CAB"].orEmpty(),
                                                        shortTermLiabilities = period["2A"].orEmpty(),
                                                        longTermLiabilities = period["2B"].orEmpty()
                                                    )
                                                }
                                                val balanceSheetRatiosList = ArrayList<BalanceSheetRatiosEntity>()
                                                balanceSheetResponseList.forEachIndexed { index, balanceSheet ->
                                                    val period = balanceSheet.period
                                                    val periodPrice = balanceSheetDateResponse.dates?.find { datePeriod -> datePeriod.period == period }?.let { datePeriodNotNull -> datePeriodNotNull.price.toString().toDoubleOrDefault() } ?: kotlin.run { if (index == 0) balanceSheetDateResponse.lastPrice.toString().toDoubleOrDefault() else null}
                                                    if (periodPrice == null) return@forEachIndexed
                                                    val marketValue = balanceSheet.paidCapital.toDoubleOrDefault() * periodPrice
                                                    val bookValue = balanceSheet.equitiesOfParentCompany.toDoubleOrDefault()
                                                    val eps = balanceSheet.previousYearsProfitAndLoss.toDoubleOrDefault() / balanceSheet.paidCapital.toDoubleOrDefault()
                                                    val netDebt = (balanceSheet.financialDebtsShort.toDoubleOrDefault() + balanceSheet.financialDebtsLong.toDoubleOrDefault()) - (balanceSheet.cashAndCashEquivalents.toDoubleOrDefault() + balanceSheet.financialInvestments.toDoubleOrDefault())
                                                    val companyValue = marketValue - netDebt
                                                    val ebitda = balanceSheet.grossProfitAndLoss.toDoubleOrDefault() + balanceSheet.generalAndAdministrativeExpenses.toDoubleOrDefault() + balanceSheet.marketingSalesAndDistributionExpenses.toDoubleOrDefault() + balanceSheet.depreciationAndAmortization.toDoubleOrDefault()
                                                    val netOperatingProfitAndLoss = balanceSheet.netOperatingProfitAndLoss.toDoubleOrDefault()
                                                    val netSales = balanceSheet.salesIncome.toDoubleOrDefault()

                                                    val isNan = balanceSheet.paidCapital.isEmpty() || balanceSheet.paidCapital == "0"

                                                    if (index == 0 && isNan) {
                                                        return@forEachIndexed
                                                    }

                                                    if (index == 0 || (index == 1 && !isNan && balanceSheetRatiosList.find { it.period == "Bugün" } == null)) {
                                                        val todayPeriod = "Bugün"
                                                        val todayPeriodPrice = balanceSheetDateResponse.lastPrice.toString().toDoubleOrDefault()
                                                        val todayMarketValue = balanceSheet.paidCapital.toDoubleOrDefault() * todayPeriodPrice
                                                        val todayCompanyValue = todayMarketValue - netDebt

                                                        val todayMarketBookAndBookValue = (todayMarketValue / bookValue)
                                                        val todayPriceAndEarning = (todayPeriodPrice / eps)
                                                        val todayCompanyValueAndEbitda = (todayCompanyValue / ebitda)
                                                        val todayMarketValueAndNetOperatingProfit = (todayMarketValue / netOperatingProfitAndLoss)
                                                        val todayCompanyValueAndNetSales = (todayCompanyValue / netSales)
                                                        val todayNetOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / todayMarketValue) * 100

                                                        balanceSheetRatiosList.add(
                                                            BalanceSheetRatiosEntity(
                                                                stockCode = code,
                                                                period = todayPeriod,
                                                                price = String.format(Locale.getDefault(), "%.2f", todayPeriodPrice),
                                                                marketBookAndBookValue = String.format(Locale.getDefault(), "%.2f", todayMarketBookAndBookValue),
                                                                priceAndEarning = String.format(Locale.getDefault(), "%.2f", todayPriceAndEarning),
                                                                companyValueAndEbitda = String.format(Locale.getDefault(), "%.2f", todayCompanyValueAndEbitda),
                                                                marketValueAndNetOperatingProfit =String.format(Locale.getDefault(), "%.2f", todayMarketValueAndNetOperatingProfit),
                                                                companyValueAndNetSales = String.format(Locale.getDefault(), "%.2f", todayCompanyValueAndNetSales),
                                                                netOperatingProfitAndMarketValue = String.format(Locale.getDefault(), "%.2f", todayNetOperatingProfitAndMarketValue),
                                                            )
                                                        )
                                                    }

                                                    val marketBookAndBookValue = (marketValue / bookValue)
                                                    val priceAndEarning = (periodPrice / eps)
                                                    val companyValueAndEbitda = (companyValue / ebitda)
                                                    val marketValueAndNetOperatingProfit = (marketValue / netOperatingProfitAndLoss)
                                                    val companyValueAndNetSales = (companyValue / netSales)
                                                    val netOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / marketValue) * 100

                                                    balanceSheetRatiosList.add(
                                                        BalanceSheetRatiosEntity(
                                                            stockCode = code,
                                                            period = period,
                                                            price = String.format(Locale.getDefault(), "%.2f", periodPrice),
                                                            marketBookAndBookValue = String.format(Locale.getDefault(), "%.2f", marketBookAndBookValue),
                                                            priceAndEarning = String.format(Locale.getDefault(), "%.2f", priceAndEarning),
                                                            companyValueAndEbitda = String.format(Locale.getDefault(), "%.2f", companyValueAndEbitda),
                                                            marketValueAndNetOperatingProfit = String.format(Locale.getDefault(), "%.2f", marketValueAndNetOperatingProfit),
                                                            companyValueAndNetSales = String.format(Locale.getDefault(), "%.2f", companyValueAndNetSales),
                                                            netOperatingProfitAndMarketValue = String.format(Locale.getDefault(), "%.2f", netOperatingProfitAndMarketValue)
                                                        )
                                                    )
                                                }
                                                try {
                                                    balanceSheetResponseList.forEach { balanceSheetResponse ->
                                                        val balanceSheetOfStockList = balanceSheetDao.getAllBalanceSheetsOfStock(balanceSheetResponse.stockCode).find { it.stockCode == balanceSheetResponse.stockCode && it.period == balanceSheetResponse.period }
                                                        if (balanceSheetOfStockList != null) return@forEach
                                                        balanceSheetDao.insertBalanceSheet(balanceSheetResponse)
                                                    }
                                                    balanceSheetRatiosList.forEach { balanceSheetRatios ->
                                                        val balanceSheetRatiosOfStockList = balanceSheetDao.getBalanceSheetRatiosListOfStock(balanceSheetRatios.stockCode).find { it.stockCode == balanceSheetRatios.stockCode && it.period == balanceSheetRatios.period }
                                                        if (balanceSheetRatiosOfStockList != null) return@forEach
                                                        balanceSheetDao.insertBalanceSheetRatios(balanceSheetRatios)
                                                    }
                                                    emit(Result.Success(true))
                                                } catch (e: Exception) {
                                                    emit(Result.Error(500, e.message.toString()))
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
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }

    }

}