package com.yavuzmobile.borsaanalizim.data.repository.remote

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.api.FinTablesApi
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinTablesRepository @Inject constructor(private val api: FinTablesApi) {

    fun getBalanceSheetDates(index: Int, period: String, filter: String): Flow<Result<List<BalanceSheetDateEntity>>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchBalanceSheetDates(period, filter)
            if (response.isSuccessful) {
                response.body()?.balanceSheetDateList?.let { data ->
                    val entities = data.mapNotNull {
                        if (it.period != null && it.stockCode != null && it.publishedAt != null) {
                            BalanceSheetDateEntity(period = it.period, stockCode = it.stockCode, publishedAt = it.publishedAt)
                        } else null
                    }
                    emit(Result.Success(entities))
                } ?: kotlin.run {
                    emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                }
            } else {
                if (response.code() == 403 && index == 0) {
                    try {
                        val lastResponse = api.fetchBalanceSheetDates("null", filter)
                        if (lastResponse.isSuccessful) {
                            lastResponse.body()?.balanceSheetDateList?.let { data ->
                                val entities = data.mapNotNull {
                                    if (it.period != null && it.stockCode != null && it.publishedAt != null && it.period == period) {
                                        BalanceSheetDateEntity(period = it.period, stockCode = it.stockCode, publishedAt = it.publishedAt)
                                    } else null
                                }
                                emit(Result.Success(entities))
                            } ?: kotlin.run {
                                emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                            }
                        } else {
                            emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                        }
                    } catch (e: Exception) {
                        emit(Result.Error(500, e.message.toString()))
                    }
                    return@flow
                }
                emit(Result.Error(response.code(), "Period: $period " + response.errorBody()?.string().toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }
}