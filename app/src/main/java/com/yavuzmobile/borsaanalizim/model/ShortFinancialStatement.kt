package com.yavuzmobile.borsaanalizim.model

import com.yavuzmobile.borsaanalizim.data.model.ShortFinancialStatementResponse

data class ShortFinancialStatement(
    val shortFinancialStatementList: List<ShortFinancialStatementResponse>?
)
