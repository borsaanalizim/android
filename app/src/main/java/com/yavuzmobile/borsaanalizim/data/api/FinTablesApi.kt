package com.yavuzmobile.borsaanalizim.data.api

import com.yavuzmobile.borsaanalizim.data.model.BalanceSheetDates
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FinTablesApi {
    // screener/?period=2024/3&filter=published_at||!period||!net_kar||!yillik_net_kar_degisimi||
    // @Header("Authorization") authHeader: String
    @GET("screener/")
    suspend fun fetchBalanceSheetDates(@Query("period") period: String, @Query("filter") filter: String): Response<BalanceSheetDates>
}