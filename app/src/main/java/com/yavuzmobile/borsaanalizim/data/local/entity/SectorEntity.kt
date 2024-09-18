package com.yavuzmobile.borsaanalizim.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SectorEntity(
    @Embedded val mainCategoryEntity: MainCategorySectorEntity,
    @Relation(
        parentColumn = "mainCategory",
        entityColumn = "mainCategory"
    )
    val subCategoryEntities: List<SubCategorySectorEntity>
)
