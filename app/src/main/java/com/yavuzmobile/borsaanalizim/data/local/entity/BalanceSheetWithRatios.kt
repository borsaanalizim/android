package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BalanceSheetWithRatios(
    @Embedded val stock: BalanceSheetStockEntity,
    @Relation(
        parentColumn = "stockCode",
        entityColumn = "stockCode"
    )
    val balanceSheets: List<BalanceSheetEntity>,
    @Relation(
        parentColumn = "stockCode",
        entityColumn = "stockCode"
    )
    val ratios: List<BalanceSheetRatioEntity>
)