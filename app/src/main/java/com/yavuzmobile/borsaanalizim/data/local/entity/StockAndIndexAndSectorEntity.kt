package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class StockAndIndexAndSectorEntity(
    @Embedded val stock: StockEntity,
    @Relation(
        parentColumn = "stockCode",
        entityColumn = "stockCode"
    )
    val indexes: List<StockInIndexEntity>
)