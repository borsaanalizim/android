package com.yavuzmobile.borsaanalizim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatioEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetStockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetWithRatios

@Dao
interface BalanceSheetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalanceSheetStock(stock: BalanceSheetStockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalanceSheet(balanceSheet: BalanceSheetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalanceSheetRatios(ratio: BalanceSheetRatioEntity)

    @Transaction
    @Query("SELECT * FROM balance_sheet_stock_table ORDER BY stockCode")
    suspend fun getAllBalanceSheetWithRatios(): List<BalanceSheetWithRatios>

    @Transaction
    @Query("SELECT * FROM balance_sheet_stock_table WHERE stockCode = :stockCode")
    suspend fun getBalanceSheetWithRatios(stockCode: String): BalanceSheetWithRatios?

    @Query("SELECT * FROM balance_sheet_ratio_table WHERE period = :period ORDER BY stockCode")
    suspend fun getBalanceSheetRatiosEntities(period: String): List<BalanceSheetRatioEntity>

}