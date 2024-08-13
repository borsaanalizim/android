package com.yavuzmobile.borsaanalizim.data.model

import com.google.gson.annotations.SerializedName

data class BalanceSheetDate(
    @SerializedName("0")
    val stockCode: String?,
    @SerializedName("published_at")
    val publishedAt: String?,
    @SerializedName("period")
    val period: String?,
)
