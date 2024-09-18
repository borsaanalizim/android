package com.yavuzmobile.borsaanalizim.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity

@Dao
interface IndexDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIndex(stock: IndexEntity)

    @Query("SELECT * FROM index_table")
    suspend fun getIndexes(): List<IndexEntity>
}