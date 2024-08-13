package com.yavuzmobile.borsaanalizim.model

import com.yavuzmobile.borsaanalizim.data.model.FinancialStatementResponse

data class FinancialStatement(
    val financialStatementList: List<FinancialStatementResponse>?
)
