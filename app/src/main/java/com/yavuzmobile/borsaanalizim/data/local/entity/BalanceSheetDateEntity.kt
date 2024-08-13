package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance_sheet_dates")
data class BalanceSheetDateEntity(
    @PrimaryKey(autoGenerate = true) var periodId: Int = 0,
    val period: String,
    val stockCode: String,
    val publishedAt: String
)