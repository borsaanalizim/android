package com.yavuzmobile.borsaanalizim.data.repository.local

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDateDao
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(private val balanceSheetDateDao: BalanceSheetDateDao) {

    fun getAllBalanceSheetDateFromLocal(): Flow<Result<List<BalanceSheetDateEntity>>> = flow {
        emit(Result.Loading())
        try {
            val allData = balanceSheetDateDao.getAllBalanceSheetDate().firstOrNull()
            if (!allData.isNullOrEmpty()) {
                emit(Result.Success(allData))
            } else {
                emit(Result.Error(404, "Veri Bulunamadı"))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    fun getLastFiveBalanceSheetDates(): Flow<Result<List<BalanceSheetDateEntity>>> = flow {
        emit(Result.Loading())
        try {
            val lastFiveBalanceSheetDates = balanceSheetDateDao.getLastFiveBalanceSheetDates().firstOrNull()
            if (!lastFiveBalanceSheetDates.isNullOrEmpty()) {
                emit(Result.Success(lastFiveBalanceSheetDates))
            } else {
                emit(Result.Error(404, "Veri Bulunamadı"))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    fun getBalanceSheetDate(period: String, stockCode: String): Flow<Result<BalanceSheetDateEntity?>> = flow {
        emit(Result.Loading())
        try {
            val data = balanceSheetDateDao.getBalanceSheetDate(period, stockCode).firstOrNull()
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    fun getBalanceSheetOfStockDate(stockCode: String): Flow<Result<List<BalanceSheetDateEntity>?>> = flow {
        emit(Result.Loading())
        try {
            val data = balanceSheetDateDao.getBalanceSheetOfStockDate(stockCode).firstOrNull()
            emit(Result.Success(data))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun insertBalanceSheetDate(entities: List<BalanceSheetDateEntity>): Result<Boolean> =
        try {
            entities.forEach { balanceSheetDateDao.insertBalanceSheetDate(it) }
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(500, e.message.toString())
        }

}