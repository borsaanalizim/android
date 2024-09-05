package com.yavuzmobile.borsaanalizim.data.repository.remote

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.api.IsYatirimApi
import com.yavuzmobile.borsaanalizim.data.local.dao.FinancialStatementDao
import com.yavuzmobile.borsaanalizim.model.FinancialStatement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IsYatirimRepository @Inject constructor(private val api: IsYatirimApi, private val dao: FinancialStatementDao) {

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

}