package com.yavuzmobile.borsaanalizim.data.model

import com.google.gson.annotations.SerializedName

data class GetStockPerformanceRequest(
    @SerializedName("endeksKodu")
    val stockCode: String?,
    @SerializedName("sektorKodu")
    val sectorCode: String?,
    @SerializedName("exchange")
    val exchange: String?,
)
