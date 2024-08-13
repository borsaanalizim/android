package com.yavuzmobile.borsaanalizim.data.model

data class CompanyCard(
    val code: String?,
    val period: List<String>?,
    val balanceSheets: List<BalanceSheet>?
)
