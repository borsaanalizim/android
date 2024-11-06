package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("sector_table")
data class SectorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sectors: List<String>
)
