package com.yavuzmobile.borsaanalizim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.yavuzmobile.borsaanalizim.data.local.entity.MainCategorySectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SubCategorySectorEntity

@Dao
interface SectorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMainCategorySector(mainCategory: MainCategorySectorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubCategorySector(subCategoryList: List<SubCategorySectorEntity>)

    @Transaction
    @Query("SELECT * FROM main_sector_table")
    suspend fun getSectors(): List<SectorEntity>

    @Query("SELECT * FROM sub_sector_table WHERE mainCategory= :mainCategory ORDER BY id")
    suspend fun getSubCategorySectorsOfMainCategory(mainCategory: String): List<SubCategorySectorEntity>

}