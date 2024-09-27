package com.yavuzmobile.borsaanalizim.model

import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatioEntity
import com.yavuzmobile.borsaanalizim.data.model.StockResponse

data class StockFilter(
    val stock: StockResponse,
    val balanceSheetRatios: BalanceSheetRatioEntity,
    val balanceSheetDate: BalanceSheetDateStockWithDates
)
