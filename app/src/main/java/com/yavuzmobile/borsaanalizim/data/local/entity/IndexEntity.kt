package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("index_table")
data class IndexEntity(
    @PrimaryKey
    val code: String,
    val name: String
)
