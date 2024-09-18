package com.yavuzmobile.borsaanalizim.data.repository.remote

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.api.Api
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.MainCategorySectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockAndIndexAndSectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInIndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInSectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SubCategorySectorEntity
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRepository @Inject constructor(private val api: Api) {

    fun getBalanceSheetDates(): Flow<Result<List<BalanceSheetDateStockWithDates>>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchBalanceSheetDates()
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    val entities = data.mapNotNull {
                        if (!it.stockCode.isNullOrEmpty() && it.lastPrice != null && !it.lastPrice.isNaN() && !it.dates.isNullOrEmpty()) {
                            BalanceSheetDateStockWithDates(
                                BalanceSheetDateStockEntity(it.stockCode, it.lastPrice),
                                it.dates.map { mappedDate ->
                                    DateEntity(
                                        period = mappedDate.period.orEmpty(),
                                        publishedAt = mappedDate.publishedAt.orEmpty(),
                                        price = mappedDate.price.toString().toDoubleOrDefault(),
                                        stockCode = it.stockCode
                                    )
                                })
                        } else null
                    }
                    emit(Result.Success(entities))
                } ?: kotlin.run {
                    emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                }
            } else {
                emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    fun getStocks(): Flow<Result<List<StockAndIndexAndSectorEntity>>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchStocks()
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    val entities = data.mapNotNull {
                        if (!it.code.isNullOrEmpty() && !it.name.isNullOrEmpty() && !it.indexes.isNullOrEmpty() && !it.sectors.isNullOrEmpty()) {
                            StockAndIndexAndSectorEntity(
                                stock = StockEntity(it.code, it.name),
                                sectors = it.sectors.map { sectorMap ->
                                    StockInSectorEntity(
                                        category = sectorMap,
                                        stockCode = it.code,
                                        stockName = it.name
                                    )
                                },
                                indexes = it.indexes.map { indexMap ->
                                    StockInIndexEntity(
                                        category = indexMap,
                                        stockCode = it.code,
                                        stockName = it.name
                                    )
                                })
                        } else null
                    }
                    emit(Result.Success(entities))
                } ?: kotlin.run {
                    emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                }
            } else {
                emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    fun getIndexes(): Flow<Result<List<IndexEntity>>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchIndexes()
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    val entities = data.mapNotNull {
                        if (!it.code.isNullOrEmpty() && !it.name.isNullOrEmpty()) {
                            IndexEntity(it.code, it.name)
                        } else null
                    }
                    emit(Result.Success(entities))
                } ?: kotlin.run {
                    emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                }
            } else {
                emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    fun getSectors(): Flow<Result<List<SectorEntity>>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchSectors()
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    val entities = data.mapNotNull {
                        if (!it.mainCategory.isNullOrEmpty() && !it.subCategories.isNullOrEmpty()) {
                            SectorEntity(
                                MainCategorySectorEntity(it.mainCategory),
                                it.subCategories.map { subCategoryMap ->
                                    SubCategorySectorEntity(
                                        mainCategory = it.mainCategory,
                                        subCategory = subCategoryMap
                                    )
                                })
                        } else null
                    }
                    emit(Result.Success(entities))
                } ?: kotlin.run {
                    emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
                }
            } else {
                emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }
}