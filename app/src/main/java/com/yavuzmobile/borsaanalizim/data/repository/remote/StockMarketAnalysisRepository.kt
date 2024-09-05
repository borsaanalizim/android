package com.yavuzmobile.borsaanalizim.data.repository.remote

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.api.Api
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockWithDates
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockMarketAnalysisRepository @Inject constructor(private val api: Api) {

    fun getBalanceSheetDates(): Flow<Result<List<StockWithDates>>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchBalanceSheetDates()
            if (response.isSuccessful) {
                response.body()?.balanceSheetDateResponseList?.let { data ->
                    val entities = data.mapNotNull {
                        if (!it.stockCode.isNullOrEmpty() && it.lastPrice != null && !it.lastPrice.isNaN() && !it.dates.isNullOrEmpty()) {
                            StockWithDates(StockEntity(it.stockCode, it.lastPrice), it.dates.map { mappedDate -> DateEntity(period = mappedDate.period.orEmpty(), publishedAt = mappedDate.publishedAt.orEmpty(), price = mappedDate.price.toString().toDoubleOrDefault(), stockCode = it.stockCode) })
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
    }
}