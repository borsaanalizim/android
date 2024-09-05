package com.yavuzmobile.borsaanalizim.data.api

import com.yavuzmobile.borsaanalizim.data.model.BaseResponse
import com.yavuzmobile.borsaanalizim.data.model.FinancialStatementResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BusinessInvestmentApi {

    // ?companyCode=THYAO&exchange=TRY&financialGroup=XI_29&year1=2024&period1=6&year2=2024&period2=3&year3=2023&period3=12&year4=2023&period4=3
    @GET("_layouts/15/IsYatirim.Website/Common/Data.aspx/MaliTablo")
    suspend fun getFinancialStatement(
        @Query("companyCode") companyCode: String, @Query("exchange") exchange: String, @Query("financialGroup") financialGroup: String,
        @Query("year1") year1: String, @Query("period1") period1: String, @Query("year2") year2: String, @Query("period2") period2: String,
        @Query("year3") year3: String, @Query("period3") period3: String, @Query("year4") year4: String, @Query("period4") period4: String
    ): Response<BaseResponse<List<FinancialStatementResponse>>>

}