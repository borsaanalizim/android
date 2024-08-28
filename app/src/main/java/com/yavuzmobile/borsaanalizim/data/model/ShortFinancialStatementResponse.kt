package com.yavuzmobile.borsaanalizim.data.model

import com.google.gson.annotations.SerializedName

data class ShortFinancialStatementResponse(
    @SerializedName("KT_KODU")
    val ktCode: String?,
    @SerializedName("KT_TANIMI")
    val ktDefinition: String?,
    @SerializedName("KT_TANIMI_YD")
    val ktDefinitionForeign: String?,
    @SerializedName("value1")
    val value1: String?,
)
