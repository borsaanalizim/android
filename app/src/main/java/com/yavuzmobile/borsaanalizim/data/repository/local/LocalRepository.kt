package com.yavuzmobile.borsaanalizim.data.repository.local

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.dao.StockDao
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockWithDates
import com.yavuzmobile.borsaanalizim.data.model.BalanceSheetDate
import com.yavuzmobile.borsaanalizim.data.model.BalanceSheetDateResponse
import com.yavuzmobile.borsaanalizim.data.model.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(private val stockDao: StockDao) {

    fun getLastTwelveBalanceSheetDateOfStock(stockCode: String): Flow<Result<BalanceSheetDateResponse>> = flow {
        emit(Result.Loading())
        try {
            val stockWithDates = stockDao.getStockWithDates(stockCode)
            val last12Periods = stockWithDates?.dates?.sortedByDescending { it.period }?.take(12)
            val mappedDates = last12Periods?.map { periodItem ->
                BalanceSheetDate(
                    periodItem.publishedAt,
                    periodItem.period,
                    periodItem.price
                )
            }
            emit(
                Result.Success(
                    BalanceSheetDateResponse(
                        stockWithDates?.stock?.stockCode,
                        stockWithDates?.stock?.lastPrice,
                        mappedDates
                    )
                )
            )
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun insertBalanceSheetDate(entities: List<StockWithDates>): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
            entities.forEach {
                val localDates = stockDao.getPeriodsOfStockCode(it.stock.stockCode)
                val newPeriods = ArrayList<DateEntity>()
                it.dates.forEach { newDate ->
                    val allReadyDate = localDates.find { newPeriod ->
                        newPeriod.stockCode == newDate.stockCode && newPeriod.period == newDate.period
                    }
                    if (allReadyDate == null) {
                        newPeriods.add(newDate)
                    }
                }
                stockDao.insertStock(it.stock)
                stockDao.insertDates(newPeriods)
            }
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    fun getStocks(): Flow<Result<Stock>> = flow {
        emit(Result.Loading())
        try {
            val stockList = ArrayList<String>()
            val stockEntities = stockDao.getStocks()
            stockEntities.forEach {
                stockList.add(it.stockCode)
            }
            emit(Result.Success(Stock(stockList, stockList)))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }
}