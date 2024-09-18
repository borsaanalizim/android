package com.yavuzmobile.borsaanalizim.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "balance_sheet_ratios_table")
data class BalanceSheetRatiosEntity(
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
) : Parcelable
