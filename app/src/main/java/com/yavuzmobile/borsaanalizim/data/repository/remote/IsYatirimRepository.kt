package com.yavuzmobile.borsaanalizim.data.repository.remote

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.api.IsYatirimApi
import com.yavuzmobile.borsaanalizim.data.model.PriceHistory
import com.yavuzmobile.borsaanalizim.data.model.Stock
import com.yavuzmobile.borsaanalizim.model.FinancialStatement
import com.yavuzmobile.borsaanalizim.model.ShortFinancialStatement
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
            var fromYearMonthDayString = ""
            var toYearMonthDayString = ""
            DateUtil.getNowDate()?.let { fromDate ->
                DateUtil.getYearMonthDayDateString(fromDate)
                    .split("-")
                    .forEach { yearMonthDayString ->
                        toYearMonthDayString += yearMonthDayString
                    }
            }
            DateUtil.getFourYearDateAgo()?.let { fromDate ->
                DateUtil.getYearMonthDayDateString(fromDate)
                    .split("-")
                    .forEach { yearMonthDayString ->
                        fromYearMonthDayString += yearMonthDayString
                    }
            }
            val response = api.fetchPriceHistory(
                "1440",
                "${fromYearMonthDayString}235959",
                "${toYearMonthDayString}235959",
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

    suspend fun getFinancialStatement(
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

    suspend fun getShortFinancialStatement(stockCode: String): Flow<Result<ShortFinancialStatement>> = flow {
        emit(Result.Loading())
        try {
            val response = api.getShortFinancialStatement(
                stockCode,
                "TRY",
                "XI_29",
                "2024",
                "6",
                "2024",
                "3"
            )
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    if (responseBody.isSuccess == true) {
                        emit(Result.Success(ShortFinancialStatement(responseBody.value)))
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

}