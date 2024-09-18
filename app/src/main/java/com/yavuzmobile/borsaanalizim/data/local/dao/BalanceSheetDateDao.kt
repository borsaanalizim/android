package com.yavuzmobile.borsaanalizim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates

@Dao
interface BalanceSheetDateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: BalanceSheetDateStockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDates(dates: List<DateEntity>)

    @Transaction
    @Query("""
        SELECT * FROM balance_sheet_date_stock_table balanceSheetDateStock
        INNER JOIN balance_sheet_date_table date ON balanceSheetDateStock.stockCode = balanceSheetDateStock.stockCode
        WHERE balanceSheetDateStock.stockCode = :stockCode AND date.period = :period
    """)
    suspend fun getStockWithDatesByStockAndPeriod(stockCode: String, period: String): BalanceSheetDateStockWithDates?

    @Transaction
    @Query("SELECT * FROM balance_sheet_date_table WHERE stockCode = :stockCode AND period = :period ORDER BY stockCode")
    suspend fun getDateByStockCodeAndPeriod(stockCode: String, period: String): DateEntity?

    @Transaction
    @Query("SELECT * FROM balance_sheet_date_stock_table WHERE stockCode = :stockCode")
    suspend fun getStockWithDates(stockCode: String): BalanceSheetDateStockWithDates?

    @Query("SELECT * FROM balance_sheet_date_table WHERE stockCode = :stockCode  ORDER BY period DESC")
    suspend fun getDatesByStockCode(stockCode: String): List<DateEntity>
}