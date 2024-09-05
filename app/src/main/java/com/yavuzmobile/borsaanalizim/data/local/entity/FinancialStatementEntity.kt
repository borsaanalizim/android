package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "financial_statements")
data class FinancialStatementEntity(
    @PrimaryKey val stockCode: String,
    val period: String,
    val data: String // JSON string
)
