package com.yavuzmobile.borsaanalizim.data.repository.local

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDao
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDateDao
import com.yavuzmobile.borsaanalizim.data.local.dao.IndexDao
import com.yavuzmobile.borsaanalizim.data.local.dao.SectorDao
import com.yavuzmobile.borsaanalizim.data.local.dao.StockDao
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatiosEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockAndIndexAndSectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInIndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInSectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SubCategorySectorEntity
import com.yavuzmobile.borsaanalizim.data.model.BalanceSheetDate
import com.yavuzmobile.borsaanalizim.data.model.BalanceSheetDateResponse
import com.yavuzmobile.borsaanalizim.data.model.IndexResponse
import com.yavuzmobile.borsaanalizim.data.model.SectorResponse
import com.yavuzmobile.borsaanalizim.data.model.StockResponse
import com.yavuzmobile.borsaanalizim.model.BalanceSheetWithRatios
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    private val balanceSheetDateDao: BalanceSheetDateDao,
    private val balanceSheetDao: BalanceSheetDao,
    private val stockDao: StockDao,
    private val sectorDao: SectorDao,
    private val indexDao: IndexDao
) {
    suspend fun insertBalanceSheetDate(entities: List<BalanceSheetDateStockWithDates>): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
            entities.forEach { entity ->
                val localDates = balanceSheetDateDao.getDatesByStockCode(entity.stock.stockCode)
                val newPeriods = ArrayList<DateEntity>()
                entity.dates.forEach { newDate ->
                    val allReadyDate = localDates.find { newPeriod ->
                        newPeriod.stockCode == newDate.stockCode && newPeriod.period == newDate.period
                    }
                    if (allReadyDate == null) {
                        newPeriods.add(newDate)
                    }
                }
                balanceSheetDateDao.insertStock(entity.stock)
                balanceSheetDateDao.insertDates(newPeriods)
            }
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getLast12BalanceSheetWithRatiosList(stockCode: String): Flow<Result<BalanceSheetWithRatios>> = flow {
        emit(Result.Loading())
        try {
            val last12BalanceSheets = balanceSheetDao.getLast12BalanceSheetsOfStock(stockCode)
            val last12BalanceSheetRatios = balanceSheetDao.getLast12BalanceSheetRatiosOfStock(stockCode)
            emit(Result.Success(BalanceSheetWithRatios(last12BalanceSheets, last12BalanceSheetRatios)))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getBalanceSheetRatiosList(period: String): Flow<Result<List<BalanceSheetRatiosEntity>>> = flow {
        emit(Result.Loading())
        try {
            val stockWithDates = balanceSheetDao.getBalanceSheetRatiosEntities(period)
            emit(Result.Success(stockWithDates))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getLastFourBalanceSheetDateOfStock(stockCode: String): Flow<Result<BalanceSheetDateResponse>> = flow {
        emit(Result.Loading())
        try {
            val stockWithDates = balanceSheetDateDao.getStockWithDates(stockCode)
            val lastFourPeriods = stockWithDates?.dates?.sortedByDescending { it.period }?.take(4)
            val mappedDates = lastFourPeriods?.map { periodItem ->
                BalanceSheetDate(
                    periodItem.publishedAt,
                    periodItem.period,
                    periodItem.price
                )
            }
            emit(
                Result.Success(
                    BalanceSheetDateResponse(
                        stockWithDates?.stock?.stockCode,
                        stockWithDates?.stock?.lastPrice,
                        mappedDates
                    )
                )
            )
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getLastTwelveBalanceSheetDateOfStock(stockCode: String): Flow<Result<BalanceSheetDateResponse>> = flow {
        emit(Result.Loading())
        try {
            val stockWithDates = balanceSheetDateDao.getStockWithDates(stockCode)
            val lastTwelvePeriods = stockWithDates?.dates?.sortedByDescending { it.period }?.take(12)
            val mappedDates = lastTwelvePeriods?.map { periodItem ->
                BalanceSheetDate(
                    periodItem.publishedAt,
                    periodItem.period,
                    periodItem.price
                )
            }
            emit(
                Result.Success(
                    BalanceSheetDateResponse(
                        stockWithDates?.stock?.stockCode,
                        stockWithDates?.stock?.lastPrice,
                        mappedDates
                    )
                )
            )
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getBalanceSheetDateStockWithDatesByStockCodeAndPeriod(stockCode: String, period: String): Flow<Result<BalanceSheetDateStockWithDates>> = flow {
        emit(Result.Loading())
        try {
            balanceSheetDateDao.getStockWithDatesByStockAndPeriod(stockCode, period)?.let { dateByStockCodeAndPeriod ->
                emit(Result.Success(dateByStockCodeAndPeriod))
            } ?: kotlin.run {
                emit(Result.Error(404, "Hisse ve Period a ait veri bulunamadı"))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun insertStock(stockEntities: List<StockAndIndexAndSectorEntity>): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
            stockEntities.forEach {
                val localStockInIndexesEntity = stockDao.getStockInIndexesOfStockCode(it.stock.stockCode)
                val localStockInSectorsEntity = stockDao.getStockInSectorsOfStockCode(it.stock.stockCode)
                val newStockInIndexes = ArrayList<StockInIndexEntity>()
                val newStockInSectors = ArrayList<StockInSectorEntity>()
                it.indexes.forEach { newStockInIndex ->
                    val allReadyStockInIndex = localStockInIndexesEntity.find { newIndex ->
                        newIndex.category == newStockInIndex.category && newIndex.stockCode == newStockInIndex.stockCode
                    }
                    if (allReadyStockInIndex == null) {
                        newStockInIndexes.add(newStockInIndex)
                    }
                }
                it.sectors.forEach { newStockInSector ->
                    val allReadyStockInSector = localStockInSectorsEntity.find { newSector ->
                        newSector.category == newStockInSector.category && newSector.stockCode == newStockInSector.stockCode
                    }
                    if (allReadyStockInSector == null) {
                        newStockInSectors.add(newStockInSector)
                    }
                }
                stockDao.insertStock(it.stock)
                stockDao.insertStockInIndexes(newStockInIndexes)
                stockDao.insertStockInSectors(newStockInSectors)
            }
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getStocks(): Flow<Result<List<StockResponse>>> = flow {
        emit(Result.Loading())
        try {
            val stocks = stockDao.getAllStocks()
            val stockResponseList = ArrayList<StockResponse>()
            stocks.forEach { stock ->
                stockResponseList.add(
                    StockResponse(
                        code = stock.stock.stockCode,
                        name = stock.stock.stockName,
                        indexes = stock.indexes.map { it.category },
                        sectors = stock.sectors.map { it.category }
                    )
                )
            }
            emit(Result.Success(stockResponseList))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getStock(stockCode: String): Flow<Result<StockResponse>> = flow {
        emit(Result.Loading())
        try {
            stockDao.getStock(stockCode)?.let { stock ->
                val stockResponse = StockResponse(
                    stock.stock.stockCode,
                    stock.stock.stockName,
                    stock.indexes.map { it.category },
                    stock.sectors.map { it.category }
                )
                emit(Result.Success(stockResponse))
            } ?: kotlin.run {
                emit(Result.Error(404, "Hisse bulunamadı!"))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getStockByIndex(stockCode: String, index: String): Flow<Result<StockResponse>> = flow {
        emit(Result.Loading())
        try {
            stockDao.getStockByIndex(stockCode, index)?.let { stock ->
                val stockResponse = StockResponse(
                    stock.stock.stockCode,
                    stock.stock.stockName,
                    stock.indexes.map { it.category },
                    stock.sectors.map { it.category }
                )
                emit(Result.Success(stockResponse))
            } ?: kotlin.run {
                emit(Result.Error(404, "Hisse bulunamadı!"))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getStockBySector(stockCode: String, sector: String): Flow<Result<StockResponse>> = flow {
        emit(Result.Loading())
        try {
            stockDao.getStockBySector(stockCode, sector)?.let { stock ->
                val stockResponse = StockResponse(
                    stock.stock.stockCode,
                    stock.stock.stockName,
                    stock.indexes.map { it.category },
                    stock.sectors.map { it.category }
                )
                emit(Result.Success(stockResponse))
            } ?: kotlin.run {
                emit(Result.Error(404, "Hisse bulunamadı!"))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getStockByIndexAndSector(stockCode: String, index: String, sector: String): Flow<Result<StockResponse>> = flow {
        emit(Result.Loading())
        try {
            stockDao.getStockByIndexAndSector(stockCode, index, sector)?.let { stock ->
                val stockResponse = StockResponse(
                    stock.stock.stockCode,
                    stock.stock.stockName,
                    stock.indexes.map { it.category },
                    stock.sectors.map { it.category }
                )
                emit(Result.Success(stockResponse))
            } ?: kotlin.run {
                emit(Result.Error(404, "Hisse bulunamadı!"))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun insertIndexes(indexEntities: List<IndexEntity>): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
            indexEntities.forEach { indexDao.insertIndex(it) }
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun insertSectors(sectorEntities: List<SectorEntity>): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
            sectorEntities.forEach { sectorEntity ->
                val localSubCategorySectorEntities = sectorDao.getSubCategorySectorsOfMainCategory(sectorEntity.mainCategoryEntity.mainCategory)
                val newSubCategorySectorEntities = ArrayList<SubCategorySectorEntity>()
                sectorEntity.subCategoryEntities.forEach { newSubCategorySectorEntity ->
                    val allReadySubCategory = localSubCategorySectorEntities.find { localSubCategorySectorEntity ->
                        localSubCategorySectorEntity.subCategory == newSubCategorySectorEntity.subCategory
                    }
                    if (allReadySubCategory == null) {
                        newSubCategorySectorEntities.add(newSubCategorySectorEntity)
                    }
                }
                sectorDao.insertMainCategorySector(sectorEntity.mainCategoryEntity)
                sectorDao.insertSubCategorySector(newSubCategorySectorEntities)
            }
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getIndexes(): Flow<Result<List<IndexResponse>>> = flow {
        emit(Result.Loading())
        try {
            val indexes = indexDao.getIndexes().map { IndexResponse(it.code, it.name) }
            emit(Result.Success(indexes))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getSectors(): Flow<Result<List<SectorResponse>>> = flow {
        emit(Result.Loading())
        try {
            val sectors = sectorDao.getSectors().map {
                SectorResponse(
                    mainCategory = it.mainCategoryEntity.mainCategory,
                    subCategories = it.subCategoryEntities.map { map -> map.subCategory },
                )
            }
            emit(Result.Success(sectors))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }
}