package com.yavuzmobile.borsaanalizim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yavuzmobile.borsaanalizim.data.local.entity.FinancialStatementEntity

@Dao
interface FinancialStatementDao {
    @Query("SELECT * FROM financial_statements WHERE stockCode = :stockCode ORDER BY period DESC LIMIT 1")
    suspend fun getLastFinancialStatement(stockCode: String): FinancialStatementEntity?

    @Query("SELECT * FROM financial_statements WHERE stockCode = :stockCode ORDER BY period DESC")
    suspend fun getFinancialStatementsOfStock(stockCode: String): List<FinancialStatementEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialStatement(statement: FinancialStatementEntity)
}