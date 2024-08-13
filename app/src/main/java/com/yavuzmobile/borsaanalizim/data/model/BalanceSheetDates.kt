package com.yavuzmobile.borsaanalizim.data.model

import com.google.gson.annotations.SerializedName

data class BalanceSheetDates(
    @SerializedName("data")
    val balanceSheetDateList: List<BalanceSheetDate>?
)
