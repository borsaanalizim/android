package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance_sheet_ratio_table")
data class BalanceSheetRatioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val stockCode: String,
    val period: String,
    val price: String,
    val marketBookAndBookValue: String,
    val priceAndEarning: String,
    val companyValueAndEbitda: String,
    val marketValueAndNetOperatingProfit: String,
    val companyValueAndNetSales: String,
    val netOperatingProfitAndMarketValue: String
)