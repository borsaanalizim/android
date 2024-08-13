package com.yavuzmobile.borsaanalizim.model

data class BalanceSheet(
    val period: String,
    val marketBookAndBookValue: String,
    val priceAndEarning: String,
    val companyValueAndEbitda: String,
    val marketValueAndNetOperatingProfit: String,
    val companyValueAndNetSales: String,
    val netOperatingProfitAndMarketValue: String
)