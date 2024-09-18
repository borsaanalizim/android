package com.yavuzmobile.borsaanalizim.model

import com.yavuzmobile.borsaanalizim.data.model.StockResponse

data class Stock(
    val stocks: List<StockResponse>,
    val filteredStocks: List<StockResponse>
)
