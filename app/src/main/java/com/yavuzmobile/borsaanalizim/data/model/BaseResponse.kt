package com.yavuzmobile.borsaanalizim.data.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("ok")
    val isSuccess: Boolean?,
    @SerializedName("errorCode")
    val errorCode: String?,
    @SerializedName("errorDescription")
    val errorDescription: String?,
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("value")
    val value: T?,
)
