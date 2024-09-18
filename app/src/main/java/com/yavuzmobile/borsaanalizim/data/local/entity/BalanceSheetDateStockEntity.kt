package com.yavuzmobile.borsaanalizim.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "balance_sheet_date_stock_table")
data class BalanceSheetDateStockEntity(
    @PrimaryKey
    val stockCode: String,
    val lastPrice: Double
) : Parcelable
