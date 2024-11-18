package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @param ebitda FAVÖK
 * @param marketBookAndBookValue PD/DD
 * @param priceAndEarning F/K
 * @param companyValueAndEbitda FD/FAVÖK
 * @param marketValueAndNetOperatingProfit PD/NFK
 * @param companyValueAndNetSales FD/NS
 * @param netOperatingProfitAndMarketValue NFK/PD %
 * @param netDebtAndEquities Net Borç/Özkaynaklar(Özsermaye)
 * @param salesGrowthRate Satış Gelirleri Büyüme Oranı
 * @param ebitdaGrowthRate FAVÖK Büyüme Oranı
 * @param netProfitGrowthRate Dönem Net Kar Büyüme Oranı
 * @param operatingProfitMargin Faaliyet Kar Marjı
 * @param equityProfitability Özkaynak Karlılığı(Özsermaye Karlılığı) - ROE
 */
@Entity(tableName = "balance_sheet_ratio_table")
data class BalanceSheetRatioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val stockCode: String,
    val period: String,
    val price: String,
    val ebitda: String,
    val marketBookAndBookValue: String,
    val priceAndEarning: String,
    val companyValueAndEbitda: String,
    val marketValueAndNetOperatingProfit: String,
    val companyValueAndNetSales: String,
    val netOperatingProfitAndMarketValue: String,
    val netDebtAndEquities: String,
    val salesGrowthRate: String,
    val ebitdaGrowthRate: String,
    val netProfitGrowthRate: String,
    val operatingProfitMargin: String,
    val equityProfitability: String
)