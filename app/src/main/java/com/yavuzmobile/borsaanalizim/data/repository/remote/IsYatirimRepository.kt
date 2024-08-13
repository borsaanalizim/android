package com.yavuzmobile.borsaanalizim.data.repository.remote

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.api.IsYatirimApi
import com.yavuzmobile.borsaanalizim.data.model.BalanceSheet
import com.yavuzmobile.borsaanalizim.data.model.CompanyCard
import com.yavuzmobile.borsaanalizim.data.model.PriceHistory
import com.yavuzmobile.borsaanalizim.data.model.Stock
import com.yavuzmobile.borsaanalizim.ext.cleanedNumberFormat
import com.yavuzmobile.borsaanalizim.model.FinancialStatement
import com.yavuzmobile.borsaanalizim.util.DateUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsYatirimRepository @Inject constructor(private val api: IsYatirimApi) {

    suspend fun fetchStocks(): Flow<Result<Stock>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchStocks()
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val stockCodes = ArrayList<String>()
                    val document = Jsoup.parse(responseBody.string())
                    val docElements = document.getElementById("allStockTable")
                        ?.select("tbody")
                        ?.firstOrNull()
                        ?.select("a")
                    docElements?.forEach { docElement ->
                        stockCodes.add(docElement.text())
                    }
                    emit(Result.Success(Stock(stockCodes, stockCodes)))
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

    suspend fun fetchPriceHistory(code: String): Flow<Result<PriceHistory>> = flow {
        emit(Result.Loading())
        try {
            var yearMonthDayString = ""
            val yearMonthDay = DateUtil.getYearMonthDayDateString(DateUtil.fromString(DateUtil.getNow())!!)?.split("-")
            yearMonthDay?.forEach {
                yearMonthDayString += it
            }
            val response = api.fetchPriceHistory(
                "1440",
                "20230601000000",
                "${yearMonthDayString}235959",
                "$code.E.BIST"
            )
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    emit(Result.Success(PriceHistory.fromRawData(responseBody.data, responseBody.timestamp)))
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

    suspend fun fetchBalanceSheet(stockCode: String): Flow<Result<CompanyCard>> = flow {
        emit(Result.Loading())
        try {
            val response = api.getBalanceSheet(stockCode)
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val periods: ArrayList<String> = ArrayList()
                    val periodOneData = mutableMapOf<String, String>()
                    val periodTwoData = mutableMapOf<String, String>()
                    val periodThreeData = mutableMapOf<String, String>()
                    val periodFourData = mutableMapOf<String, String>()

                    val document = Jsoup.parse(responseBody.string())
                    // Parse the document to extract balance sheet data
                    val balanceSheetTable = document.getElementById("tbodyMTablo")
                    val periodsOfDocument = document.getElementById("ctl00_ctl58_g_00f9bafa_387b_423c_b297_f8592de90819")
                            ?.select("table")?.firstOrNull()?.select("thead")?.firstOrNull()
                            ?.getElementsByClass("form-group")?.firstOrNull()

                    periodsOfDocument?.text()?.split(" ")?.take(4)?.forEach { item ->
                        periods.add(item)
                    }

                    balanceSheetTable?.select("tr")?.forEach { row ->
                        val columns = row.select("td")
                        if (columns.size >= 2) {
                            var label = columns[0].text()

                            val valueOne = columns[1].text()
                            val valueTwo = columns[2].text()
                            val valueThree = columns[3].text()
                            val valueFour = columns[4].text()

                            if (periodOneData.filter { it.key == label }.isNotEmpty()) {
                                label += "2"
                            }

                            periodOneData[label] = valueOne
                            periodTwoData[label] = valueTwo
                            periodThreeData[label] = valueThree
                            periodFourData[label] = valueFour
                        }
                    }

                    val balanceSheetOne = BalanceSheet(
                        paidCapital = periodOneData["Ödenmiş Sermaye"].cleanedNumberFormat(),
                        equities = periodOneData["Özkaynaklar"].cleanedNumberFormat(),
                        equitiesOfParentCompany = periodOneData["Ana Ortaklığa Ait Özkaynaklar"].cleanedNumberFormat(),
                        financialDebtsShort = periodOneData["Finansal Borçlar"].cleanedNumberFormat(),
                        financialDebtsLong = periodOneData["Finansal Borçlar2"].cleanedNumberFormat(),
                        cashAndCashEquivalents = periodOneData["Nakit ve Nakit Benzerleri"].cleanedNumberFormat(),
                        financialInvestments = periodOneData["Finansal Yatırımlar"].cleanedNumberFormat(),
                        netOperatingProfitAndLoss = periodOneData["Net Faaliyet Kar/Zararı"].cleanedNumberFormat(),
                        salesIncome = periodOneData["Satış Gelirleri"].cleanedNumberFormat(),
                        grossProfitAndLoss = periodOneData["BRÜT KAR (ZARAR)"].cleanedNumberFormat(),
                        previousYearsProfitAndLoss = periodOneData["Geçmiş Yıllar Kar/Zararları"].cleanedNumberFormat(),
                        netProfitAndLossPeriod = periodOneData["Dönem Net Kar/Zararı"].cleanedNumberFormat(),
                        operatingProfitAndLoss = periodOneData["FAALİYET KARI (ZARARI)"].cleanedNumberFormat(),
                        depreciationExpenses = periodOneData["Amortisman Giderleri"].cleanedNumberFormat(),
                        otherExpenses = periodOneData["Faiz, Ücret, Prim, Komisyon ve Diğer Giderler (-)"].cleanedNumberFormat(),
                        periodTaxIncomeAndExpense = periodOneData["Dönem Vergi Geliri (Gideri)"].cleanedNumberFormat(),
                        generalAndAdministrativeExpenses = periodOneData["Genel Yönetim Giderleri (-)"].cleanedNumberFormat(),
                        costOfSales = periodOneData["Satışların Maliyeti (-)"].cleanedNumberFormat(),
                        marketingSalesAndDistributionExpenses = periodOneData["Pazarlama, Satış ve Dağıtım Giderleri (-)"].cleanedNumberFormat(),
                        researchAndDevelopmentExpenses = periodOneData["Araştırma ve Geliştirme Giderleri (-)"].cleanedNumberFormat(),
                        depreciationAndAmortization = periodOneData["Amortisman & İtfa Payları"].cleanedNumberFormat(),
                    )


                    val balanceSheetTwo = BalanceSheet(
                        paidCapital = periodTwoData["Ödenmiş Sermaye"].cleanedNumberFormat(),
                        equities = periodTwoData["Özkaynaklar"].cleanedNumberFormat(),
                        equitiesOfParentCompany = periodTwoData["Ana Ortaklığa Ait Özkaynaklar"].cleanedNumberFormat(),
                        financialDebtsShort = periodTwoData["Finansal Borçlar"].cleanedNumberFormat(),
                        financialDebtsLong = periodTwoData["Finansal Borçlar2"].cleanedNumberFormat(),
                        cashAndCashEquivalents = periodTwoData["Nakit ve Nakit Benzerleri"].cleanedNumberFormat(),
                        financialInvestments = periodTwoData["Finansal Yatırımlar"].cleanedNumberFormat(),
                        netOperatingProfitAndLoss = periodTwoData["Net Faaliyet Kar/Zararı"].cleanedNumberFormat(),
                        salesIncome = periodTwoData["Satış Gelirleri"].cleanedNumberFormat(),
                        grossProfitAndLoss = periodTwoData["BRÜT KAR (ZARAR)"].cleanedNumberFormat(),
                        previousYearsProfitAndLoss = periodTwoData["Geçmiş Yıllar Kar/Zararları"].cleanedNumberFormat(),
                        netProfitAndLossPeriod = periodTwoData["Dönem Net Kar/Zararı"].cleanedNumberFormat(),
                        operatingProfitAndLoss = periodTwoData["FAALİYET KARI (ZARARI)"].cleanedNumberFormat(),
                        depreciationExpenses = periodTwoData["Amortisman Giderleri"].cleanedNumberFormat(),
                        otherExpenses = periodTwoData["Faiz, Ücret, Prim, Komisyon ve Diğer Giderler (-)"].cleanedNumberFormat(),
                        periodTaxIncomeAndExpense = periodTwoData["Dönem Vergi Geliri (Gideri)"].cleanedNumberFormat(),
                        generalAndAdministrativeExpenses = periodTwoData["Genel Yönetim Giderleri (-)"].cleanedNumberFormat(),
                        costOfSales = periodTwoData["Satışların Maliyeti (-)"].cleanedNumberFormat(),
                        marketingSalesAndDistributionExpenses = periodTwoData["Pazarlama, Satış ve Dağıtım Giderleri (-)"].cleanedNumberFormat(),
                        researchAndDevelopmentExpenses = periodTwoData["Araştırma ve Geliştirme Giderleri (-)"].cleanedNumberFormat(),
                        depreciationAndAmortization = periodTwoData["Amortisman & İtfa Payları"].cleanedNumberFormat(),
                    )

                    val balanceSheetThree = BalanceSheet(
                        paidCapital = periodThreeData["Ödenmiş Sermaye"].cleanedNumberFormat(),
                        equities = periodThreeData["Özkaynaklar"].cleanedNumberFormat(),
                        equitiesOfParentCompany = periodThreeData["Ana Ortaklığa Ait Özkaynaklar"].cleanedNumberFormat(),
                        financialDebtsShort = periodThreeData["Finansal Borçlar"].cleanedNumberFormat(),
                        financialDebtsLong = periodThreeData["Finansal Borçlar2"].cleanedNumberFormat(),
                        cashAndCashEquivalents = periodThreeData["Nakit ve Nakit Benzerleri"].cleanedNumberFormat(),
                        financialInvestments = periodThreeData["Finansal Yatırımlar"].cleanedNumberFormat(),
                        netOperatingProfitAndLoss = periodThreeData["Net Faaliyet Kar/Zararı"].cleanedNumberFormat(),
                        salesIncome = periodThreeData["Satış Gelirleri"].cleanedNumberFormat(),
                        grossProfitAndLoss = periodThreeData["BRÜT KAR (ZARAR)"].cleanedNumberFormat(),
                        previousYearsProfitAndLoss = periodThreeData["Geçmiş Yıllar Kar/Zararları"].cleanedNumberFormat(),
                        netProfitAndLossPeriod = periodThreeData["Dönem Net Kar/Zararı"].cleanedNumberFormat(),
                        operatingProfitAndLoss = periodThreeData["FAALİYET KARI (ZARARI)"].cleanedNumberFormat(),
                        depreciationExpenses = periodThreeData["Amortisman Giderleri"].cleanedNumberFormat(),
                        otherExpenses = periodThreeData["Faiz, Ücret, Prim, Komisyon ve Diğer Giderler (-)"].cleanedNumberFormat(),
                        periodTaxIncomeAndExpense = periodThreeData["Dönem Vergi Geliri (Gideri)"].cleanedNumberFormat(),
                        generalAndAdministrativeExpenses = periodThreeData["Genel Yönetim Giderleri (-)"].cleanedNumberFormat(),
                        costOfSales = periodThreeData["Satışların Maliyeti (-)"].cleanedNumberFormat(),
                        marketingSalesAndDistributionExpenses = periodThreeData["Pazarlama, Satış ve Dağıtım Giderleri (-)"].cleanedNumberFormat(),
                        researchAndDevelopmentExpenses = periodThreeData["Araştırma ve Geliştirme Giderleri (-)"].cleanedNumberFormat(),
                        depreciationAndAmortization = periodThreeData["Amortisman & İtfa Payları"].cleanedNumberFormat(),
                    )

                    val balanceSheetFour = BalanceSheet(
                        paidCapital = periodFourData["Ödenmiş Sermaye"].cleanedNumberFormat(),
                        equities = periodFourData["Özkaynaklar"].cleanedNumberFormat(),
                        equitiesOfParentCompany = periodFourData["Ana Ortaklığa Ait Özkaynaklar"].cleanedNumberFormat(),
                        financialDebtsShort = periodFourData["Finansal Borçlar"].cleanedNumberFormat(),
                        financialDebtsLong = periodFourData["Finansal Borçlar2"].cleanedNumberFormat(),
                        cashAndCashEquivalents = periodFourData["Nakit ve Nakit Benzerleri"].cleanedNumberFormat(),
                        financialInvestments = periodFourData["Finansal Yatırımlar"].cleanedNumberFormat(),
                        netOperatingProfitAndLoss = periodFourData["Net Faaliyet Kar/Zararı"].cleanedNumberFormat(),
                        salesIncome = periodFourData["Satış Gelirleri"].cleanedNumberFormat(),
                        grossProfitAndLoss = periodFourData["BRÜT KAR (ZARAR)"].cleanedNumberFormat(),
                        previousYearsProfitAndLoss = periodFourData["Geçmiş Yıllar Kar/Zararları"].cleanedNumberFormat(),
                        netProfitAndLossPeriod = periodFourData["Dönem Net Kar/Zararı"].cleanedNumberFormat(),
                        operatingProfitAndLoss = periodFourData["FAALİYET KARI (ZARARI)"].cleanedNumberFormat(),
                        depreciationExpenses = periodFourData["Amortisman Giderleri"].cleanedNumberFormat(),
                        otherExpenses = periodFourData["Faiz, Ücret, Prim, Komisyon ve Diğer Giderler (-)"].cleanedNumberFormat(),
                        periodTaxIncomeAndExpense = periodFourData["Dönem Vergi Geliri (Gideri)"].cleanedNumberFormat(),
                        generalAndAdministrativeExpenses = periodFourData["Genel Yönetim Giderleri (-)"].cleanedNumberFormat(),
                        costOfSales = periodFourData["Satışların Maliyeti (-)"].cleanedNumberFormat(),
                        marketingSalesAndDistributionExpenses = periodFourData["Pazarlama, Satış ve Dağıtım Giderleri (-)"].cleanedNumberFormat(),
                        researchAndDevelopmentExpenses = periodFourData["Araştırma ve Geliştirme Giderleri (-)"].cleanedNumberFormat(),
                        depreciationAndAmortization = periodFourData["Amortisman & İtfa Payları"].cleanedNumberFormat(),
                    )

                    emit(Result.Success(
                        CompanyCard(
                            stockCode,
                            periods,
                            listOf(balanceSheetOne, balanceSheetTwo, balanceSheetThree, balanceSheetFour)
                        )
                    ))
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

    suspend fun fetchBalanceSheetJson(stockCode: String): Flow<Result<FinancialStatement>> = flow {
        emit(Result.Loading())
        try {
            val response = api.getBalanceSheetJson(
                stockCode,
                "TRY",
                "UFRS_K",
                "2024", "6",
                "2024", "3",
                "2023",
                "12",
                "2023",
                "9"
            )
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    if (responseBody.isSuccess == true) {
                        emit(Result.Success(FinancialStatement(responseBody.value)))
                    } else {
                        emit(Result.Error(responseBody.errorCode.toString().toInt(), responseBody.errorDescription.toString()))
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
    
}