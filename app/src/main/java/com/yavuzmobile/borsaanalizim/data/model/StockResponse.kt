package com.yavuzmobile.borsaanalizim.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockResponse(
    val code: String?,
    val name: String?,
    val indexes: List<String>?,
    val sectors: List<String>?
) : Parcelable
