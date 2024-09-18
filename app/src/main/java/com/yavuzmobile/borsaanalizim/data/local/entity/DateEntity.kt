package com.yavuzmobile.borsaanalizim.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "balance_sheet_date_table")
data class DateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val period: String,
    val publishedAt: String,
    val price: Double,
    val stockCode: String
) : Parcelable
