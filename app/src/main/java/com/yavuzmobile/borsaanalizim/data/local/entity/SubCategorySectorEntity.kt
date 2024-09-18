package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("sub_sector_table")
data class SubCategorySectorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mainCategory: String,
    val subCategory: String
)
