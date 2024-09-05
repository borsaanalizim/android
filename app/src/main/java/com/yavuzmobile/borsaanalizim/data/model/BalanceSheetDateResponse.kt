package com.yavuzmobile.borsaanalizim.data.model

data class BalanceSheetDateResponse(
    val stockCode: String?,
    val lastPrice: Double?,
    val dates: List<BalanceSheetDate>?
)
