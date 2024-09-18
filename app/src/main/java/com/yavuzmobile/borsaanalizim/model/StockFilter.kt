package com.yavuzmobile.borsaanalizim.model

import android.os.Parcelable
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatiosEntity
import com.yavuzmobile.borsaanalizim.data.model.StockResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockFilter(
    val stock: StockResponse,
    val balanceSheetRatios: BalanceSheetRatiosEntity,
    val balanceSheetDate: BalanceSheetDateStockWithDates
) : Parcelable
