package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("main_sector_table")
data class MainCategorySectorEntity(
    @PrimaryKey
    val mainCategory: String
)
