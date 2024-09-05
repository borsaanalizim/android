package com.yavuzmobile.borsaanalizim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockWithDates

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDates(dates: List<DateEntity>)

    @Transaction
    @Query("SELECT * FROM stock_table")
    suspend fun getAllStockWithDates(): List<StockWithDates>

    @Query("SELECT * FROM stock_table")
    suspend fun getStocks(): List<StockEntity>

    @Transaction
    @Query("SELECT * FROM stock_table WHERE stockCode = :stockCode")
    suspend fun getStockWithDates(stockCode: String): StockWithDates?

    @Query("SELECT * FROM date_table WHERE stockCode = :stockCode")
    suspend fun getPeriodsOfStockCode(stockCode: String): List<DateEntity>

    @Transaction
    @Query("""SELECT * FROM date_table WHERE stockCode = (SELECT id FROM stock_table WHERE stockCode = :stockCode) ORDER BY period DESC LIMIT 12""")
    suspend fun getLast12Periods(stockCode: String): List<DateEntity>
}