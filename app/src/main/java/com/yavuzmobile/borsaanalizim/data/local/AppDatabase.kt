package com.yavuzmobile.borsaanalizim.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDao
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDateDao
import com.yavuzmobile.borsaanalizim.data.local.dao.IndexDao
import com.yavuzmobile.borsaanalizim.data.local.dao.SectorDao
import com.yavuzmobile.borsaanalizim.data.local.dao.StockDao
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatioEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetStockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInIndexEntity

@Database(entities = [IndexEntity::class, SectorEntity::class, BalanceSheetDateStockEntity::class, DateEntity::class, StockEntity::class, StockInIndexEntity::class, BalanceSheetStockEntity::class, BalanceSheetEntity::class, BalanceSheetRatioEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun indexDao(): IndexDao
    abstract fun sectorDao(): SectorDao
    abstract fun stockDao(): StockDao
    abstract fun balanceSheetDateDao(): BalanceSheetDateDao
    abstract fun balanceSheetDao(): BalanceSheetDao
}