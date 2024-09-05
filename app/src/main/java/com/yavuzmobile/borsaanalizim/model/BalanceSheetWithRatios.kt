package com.yavuzmobile.borsaanalizim.model

import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatiosEntity

data class BalanceSheetWithRatios(
    val balanceSheetList: List<BalanceSheetEntity>,
    val balanceSheetRatios: List<BalanceSheetRatiosEntity>
)
