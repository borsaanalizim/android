package com.yavuzmobile.borsaanalizim.data.model

data class PriceHistory(
    val data: List<List<Any>>?,
    val timestamp: String?
) {
    companion object {
        fun fromRawData(data: List<List<Any>>?, timestamp: String?): PriceHistory {
            return PriceHistory(data?.map {
                listOf(it[0], it[1])
            }, timestamp)
        }
    }
}
