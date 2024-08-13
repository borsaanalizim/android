package com.yavuzmobile.borsaanalizim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceSheetDateDao {
    @Query("SELECT * FROM balance_sheet_dates")
    fun getAllBalanceSheetDate(): Flow<List<BalanceSheetDateEntity>>

    @Query("SELECT * FROM balance_sheet_dates WHERE period = :period AND stockCode = :stockCode")
    fun getBalanceSheetDate(period: String, stockCode: String): Flow<BalanceSheetDateEntity?>

    @Query("SELECT * FROM balance_sheet_dates WHERE stockCode = :stockCode")
    fun getBalanceSheetOfStockDate(stockCode: String): Flow<List<BalanceSheetDateEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalanceSheetDate(vararg balanceSheetDate: BalanceSheetDateEntity)

    @Query("SELECT * FROM balance_sheet_dates ORDER BY period DESC LIMIT 5")
    fun getLastFiveBalanceSheetDates(): Flow<List<BalanceSheetDateEntity>>
}