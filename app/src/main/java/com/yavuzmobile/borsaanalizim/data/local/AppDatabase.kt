package com.yavuzmobile.borsaanalizim.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDao
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDateDao
import com.yavuzmobile.borsaanalizim.data.local.dao.IndexDao
import com.yavuzmobile.borsaanalizim.data.local.dao.SectorDao
import com.yavuzmobile.borsaanalizim.data.local.dao.StockDao
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatiosEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.MainCategorySectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInIndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInSectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SubCategorySectorEntity

@Database(entities = [IndexEntity::class, MainCategorySectorEntity::class, SubCategorySectorEntity::class, BalanceSheetDateStockEntity::class, DateEntity::class, StockEntity::class, StockInIndexEntity::class, StockInSectorEntity::class, BalanceSheetEntity::class, BalanceSheetRatiosEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun indexDao(): IndexDao
    abstract fun sectorDao(): SectorDao
    abstract fun stockDao(): StockDao
    abstract fun balanceSheetDateDao(): BalanceSheetDateDao
    abstract fun balanceSheetDao(): BalanceSheetDao
}