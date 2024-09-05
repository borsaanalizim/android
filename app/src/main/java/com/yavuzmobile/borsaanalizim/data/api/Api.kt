package com.yavuzmobile.borsaanalizim.data.api

import com.yavuzmobile.borsaanalizim.data.model.BalanceSheetDates
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("/api/balanceSheetDates")
    suspend fun fetchBalanceSheetDates(): Response<BalanceSheetDates>
}