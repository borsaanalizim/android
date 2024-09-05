package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "date_table")
data class DateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Otomatik olarak artan birincil anahtar
    val period: String,
    val publishedAt: String,
    val price: Double,
    val stockCode: String // Ana tablo ile ilişkilendirme için gerekli
)
