package com.yavuzmobile.borsaanalizim.model

data class StocksFilter(
    val defaultList: List<StockFilter>,
    val filteredList: List<StockFilter>
)
