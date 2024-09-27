package com.yavuzmobile.borsaanalizim.data.model

data class BalanceSheetResponse(
    val stockCode: String?,
    val balanceSheets: List<BalanceSheetItemResponse>?
)
