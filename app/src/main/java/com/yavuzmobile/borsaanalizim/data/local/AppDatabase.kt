package com.yavuzmobile.borsaanalizim.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDateDao
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateEntity

@Database(entities = [BalanceSheetDateEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun balanceSheetDateDao(): BalanceSheetDateDao
}