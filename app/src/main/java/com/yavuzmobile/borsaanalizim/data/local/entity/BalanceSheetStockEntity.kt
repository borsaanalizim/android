package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("balance_sheet_stock_table")
data class BalanceSheetStockEntity(
    @PrimaryKey
    val stockCode: String
)
