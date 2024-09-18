package com.yavuzmobile.borsaanalizim.data.api

import com.yavuzmobile.borsaanalizim.data.model.BalanceSheetDateResponse
import com.yavuzmobile.borsaanalizim.data.model.BaseResponse
import com.yavuzmobile.borsaanalizim.data.model.IndexResponse
import com.yavuzmobile.borsaanalizim.data.model.SectorResponse
import com.yavuzmobile.borsaanalizim.data.model.StockInIndexesResponse
import com.yavuzmobile.borsaanalizim.data.model.StockInSectorsResponse
import com.yavuzmobile.borsaanalizim.data.model.StockResponse
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("/api/balanceSheetDates")
    suspend fun fetchBalanceSheetDates(): Response<BaseResponse<List<BalanceSheetDateResponse>>>

    @GET("/api/stocks")
    suspend fun fetchStocks(): Response<BaseResponse<List<StockResponse>>>

    @GET("/api/indexes")
    suspend fun fetchIndexes(): Response<BaseResponse<List<IndexResponse>>>

    @GET("/api/sectors")
    suspend fun fetchSectors(): Response<BaseResponse<List<SectorResponse>>>

    @GET("/api/stocksInIndexes")
    suspend fun fetchStocksInIndexes(): Response<BaseResponse<List<StockInIndexesResponse>>>

    @GET("/api/stocksInSectors")
    suspend fun fetchStocksInSectors(): Response<BaseResponse<List<StockInSectorsResponse>>>
}