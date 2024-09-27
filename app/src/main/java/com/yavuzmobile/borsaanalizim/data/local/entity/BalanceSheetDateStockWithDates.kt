package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class BalanceSheetDateStockWithDates(
    @Embedded val stock: BalanceSheetDateStockEntity,
    @Relation(
        parentColumn = "stockCode",
        entityColumn = "stockCode"
    )
    val dates: List<DateEntity>
)