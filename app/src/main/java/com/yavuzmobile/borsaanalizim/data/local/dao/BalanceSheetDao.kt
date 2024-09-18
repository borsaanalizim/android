package com.yavuzmobile.borsaanalizim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatiosEntity

@Dao
interface BalanceSheetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalanceSheet(stock: BalanceSheetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalanceSheetRatios(stock: BalanceSheetRatiosEntity)

    @Query("SELECT * FROM balance_sheet_ratios_table WHERE period = :period ORDER BY stockCode")
    suspend fun getBalanceSheetRatiosEntities(period: String): List<BalanceSheetRatiosEntity>

    @Query("SELECT * FROM balance_sheet_table WHERE stockCode = :stockCode  ORDER BY period DESC")
    suspend fun getAllBalanceSheetsOfStock(stockCode: String): List<BalanceSheetEntity>

    @Query("SELECT * FROM balance_sheet_ratios_table WHERE stockCode = :stockCode ORDER BY period DESC")
    suspend fun getBalanceSheetRatiosListOfStock(stockCode: String): List<BalanceSheetRatiosEntity>

    @Query("SELECT * FROM balance_sheet_table WHERE stockCode = :stockCode ORDER BY period DESC LIMIT 12")
    suspend fun getLast12BalanceSheetsOfStock(stockCode: String): List<BalanceSheetEntity>

    @Query("SELECT * FROM balance_sheet_ratios_table WHERE stockCode = :stockCode ORDER BY period DESC LIMIT 12")
    suspend fun getLast12BalanceSheetRatiosOfStock(stockCode: String): List<BalanceSheetRatiosEntity>
}