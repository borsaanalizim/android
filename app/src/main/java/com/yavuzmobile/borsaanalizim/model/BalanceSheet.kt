package com.yavuzmobile.borsaanalizim.model

data class BalanceSheet(
    val period: String,
    val price: Double,
    val marketBookAndBookValue: String,
    val priceAndEarning: String,
    val companyValueAndEbitda: String,
    val marketValueAndNetOperatingProfit: String,
    val companyValueAndNetSales: String,
    val netOperatingProfitAndMarketValue: String
)