package com.yavuzmobile.borsaanalizim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yavuzmobile.borsaanalizim.data.local.entity.StockAndIndexAndSectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInIndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInSectorEntity

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockInIndexes(stockInIndexes: List<StockInIndexEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockInSectors(stockInSectors: List<StockInSectorEntity>)

    @Transaction
    @Query("SELECT * FROM stock_table ORDER BY stockCode")
    suspend fun getAllStocks(): List<StockAndIndexAndSectorEntity>

    @Transaction
    @Query("SELECT * FROM stock_table WHERE stockCode = :stockCode")
    suspend fun getStock(stockCode: String): StockAndIndexAndSectorEntity?

    @Transaction
    @Query("""
        SELECT * FROM stock_table s
        INNER JOIN stock_in_index_table i ON s.stockCode = i.stockCode
        WHERE s.stockCode = :stockCode AND i.category = :index
    """)
    suspend fun getStockByIndex(stockCode: String, index: String): StockAndIndexAndSectorEntity?

    @Transaction
    @Query("""
        SELECT * FROM stock_table s
        INNER JOIN stock_in_sector_table se ON s.stockCode = se.stockCode
        WHERE s.stockCode = :stockCode AND se.category = :sector
    """)
    suspend fun getStockBySector(stockCode: String, sector: String): StockAndIndexAndSectorEntity?

    @Transaction
    @Query("""
        SELECT * FROM stock_table s
        INNER JOIN stock_in_index_table i ON s.stockCode = i.stockCode
        INNER JOIN stock_in_sector_table se ON s.stockCode = se.stockCode
        WHERE s.stockCode = :stockCode AND i.category = :index AND se.category = :sector
    """)
    suspend fun getStockByIndexAndSector(stockCode: String, index: String, sector: String): StockAndIndexAndSectorEntity?

    @Query("SELECT * FROM stock_in_sector_table WHERE stockCode= :stockCode ORDER BY stockCode")
    suspend fun getStockInSectorsOfStockCode(stockCode: String): List<StockInSectorEntity>

    @Query("SELECT * FROM stock_in_index_table WHERE stockCode= :stockCode ORDER BY stockCode")
    suspend fun getStockInIndexesOfStockCode(stockCode: String): List<StockInIndexEntity>

}