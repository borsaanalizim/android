package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_table")
data class StockEntity(
    @PrimaryKey
    val stockCode: String, // stockCode birincil anahtar olarak kullanılıyor
    val lastPrice: Double
)
