package com.yavuzmobile.borsaanalizim.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yavuzmobile.borsaanalizim.data.local.dao.FinancialStatementDao
import com.yavuzmobile.borsaanalizim.data.local.dao.StockDao
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.FinancialStatementEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockEntity

@Database(entities = [StockEntity::class, DateEntity::class, FinancialStatementEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stockDao(): StockDao
    abstract fun financialStatementDao(): FinancialStatementDao
}