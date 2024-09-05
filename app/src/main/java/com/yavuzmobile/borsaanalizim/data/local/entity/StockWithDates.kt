package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StockWithDates(
    @Embedded val stock: StockEntity,
    @Relation(
        parentColumn = "stockCode",
        entityColumn = "stockCode"
    )
    val dates: List<DateEntity>
)
