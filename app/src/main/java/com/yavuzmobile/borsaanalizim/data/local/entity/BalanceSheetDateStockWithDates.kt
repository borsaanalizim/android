package com.yavuzmobile.borsaanalizim.data.local.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
data class BalanceSheetDateStockWithDates(
    @Embedded val stock: BalanceSheetDateStockEntity,
    @Relation(
        parentColumn = "stockCode",
        entityColumn = "stockCode"
    )
    val dates: List<DateEntity>
): Parcelable
