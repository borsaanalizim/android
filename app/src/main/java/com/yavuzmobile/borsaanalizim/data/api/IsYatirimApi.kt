package com.yavuzmobile.borsaanalizim.data.api

import com.yavuzmobile.borsaanalizim.data.model.BaseResponse
import com.yavuzmobile.borsaanalizim.data.model.FinancialStatementResponse
import com.yavuzmobile.borsaanalizim.data.model.GetStockPerformanceRequest
import com.yavuzmobile.borsaanalizim.data.model.PriceHistory
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IsYatirimApi {

    @GET("tr-tr/analiz/hisse/Sayfalar/default.aspx")
    suspend fun fetchStocks(): Response<ResponseBody>

    // ?period=60&from=20240729000000&to=20240805235959&endeks=THYAO.E.BIST
    @GET("_Layouts/15/IsYatirim.Website/Common/ChartData.aspx/IndexHistoricalAll")
    suspend fun fetchPriceHistory(@Query("period") period: String, @Query("from") from: String, @Query("to") to: String, @Query("endeks") endeks: String): Response<PriceHistory>

    @GET("tr-tr/analiz/hisse/Sayfalar/sirket-karti.aspx")
    suspend fun getBalanceSheet(@Query("hisse") stockSymbol: String): Response<ResponseBody>

    // ?companyCode=THYAO&exchange=TRY&financialGroup=XI_29&year1=2024&period1=6&year2=2024&period2=3&year3=2023&period3=12&year4=2023&period4=3
    @GET("_layouts/15/IsYatirim.Website/Common/Data.aspx/MaliTablo")
    suspend fun getBalanceSheetJson(
        @Query("companyCode") companyCode: String, @Query("exchange") exchange: String, @Query("financialGroup") financialGroup: String,
        @Query("year1") year1: String, @Query("period1") period1: String, @Query("year2") year2: String, @Query("period2") period2: String,
        @Query("year3") year3: String, @Query("period3") period3: String, @Query("year4") year4: String, @Query("period4") period4: String
    ): Response<BaseResponse<List<FinancialStatementResponse>>>

    @POST("_layouts/15/IsYatirim.Website/StockInfo/CompanyInfoAjax.aspx/GetHissePerformans")
    suspend fun getStockPerformance(@Body request: GetStockPerformanceRequest): Response<ResponseBody>
}