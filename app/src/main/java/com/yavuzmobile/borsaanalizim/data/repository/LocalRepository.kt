package com.yavuzmobile.borsaanalizim.data.repository

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDao
import com.yavuzmobile.borsaanalizim.data.local.dao.BalanceSheetDateDao
import com.yavuzmobile.borsaanalizim.data.local.dao.IndexDao
import com.yavuzmobile.borsaanalizim.data.local.dao.SectorDao
import com.yavuzmobile.borsaanalizim.data.local.dao.StockDao
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatioEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetStockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetWithRatios
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockAndIndexAndSectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInIndexEntity
import com.yavuzmobile.borsaanalizim.data.model.IndexResponse
import com.yavuzmobile.borsaanalizim.data.model.SectorResponse
import com.yavuzmobile.borsaanalizim.data.model.StockResponse
import com.yavuzmobile.borsaanalizim.ext.cleanedNumberFormat
import com.yavuzmobile.borsaanalizim.ext.isComparePeriod
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import com.yavuzmobile.borsaanalizim.ext.totalNumber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale
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

    suspend fun insertBalanceSheetDates(entities: List<BalanceSheetDateStockWithDates>): Flow<Result<Boolean>> = flow {
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

    suspend fun insertBalanceSheetDate(entity: BalanceSheetDateStockWithDates): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
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
            emit(Result.Success(true))
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
                val newStockInIndexes = ArrayList<StockInIndexEntity>()
                it.indexes.forEach { newStockInIndex ->
                    val allReadyStockInIndex = localStockInIndexesEntity.find { newIndex ->
                        newIndex.category == newStockInIndex.category && newIndex.stockCode == newStockInIndex.stockCode
                    }
                    if (allReadyStockInIndex == null) {
                        newStockInIndexes.add(newStockInIndex)
                    }
                }
                stockDao.insertStock(it.stock)
                stockDao.insertStockInIndexes(newStockInIndexes)
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
                        stock.stock.stockCode,
                        stock.stock.stockName,
                        stock.stock.financialGroup,
                        stock.stock.mkkMemberOid,
                        stock.stock.sector,
                        stock.indexes.map { it.category }
                    )
                )
            }
            emit(Result.Success(stockResponseList))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getStock(stockCode: String): Flow<Result<StockEntity>> = flow {
        emit(Result.Loading())
        try {
            stockDao.getStock(stockCode)?.let {
                emit(Result.Success(it))
            } ?: kotlin.run {
                emit(Result.Error(404, "Hisse bulunamadı!"))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getStockAndIndexAndSector(stockCode: String): Flow<Result<StockResponse>> = flow {
        emit(Result.Loading())
        try {
            stockDao.getStockAndIndexAndSector(stockCode)?.let { stock ->
                val stockResponse = StockResponse(
                    stock.stock.stockCode,
                    stock.stock.stockName,
                    stock.stock.financialGroup,
                    stock.stock.mkkMemberOid,
                    stock.stock.sector,
                    stock.indexes.map { it.category }
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
                    stock.stock.financialGroup,
                    stock.stock.mkkMemberOid,
                    stock.stock.sector,
                    stock.indexes.map { it.category }
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
                    stock.stock.financialGroup,
                    stock.stock.mkkMemberOid,
                    stock.stock.sector,
                    stock.indexes.map { it.category }
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
                    stock.stock.financialGroup,
                    stock.stock.mkkMemberOid,
                    stock.stock.sector,
                    stock.indexes.map { it.category }
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

    suspend fun insertSectors(sectorEntity: SectorEntity): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
            sectorDao.insertSectors(sectorEntity)
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

    suspend fun getSectors(): Flow<Result<SectorResponse>> = flow {
        emit(Result.Loading())
        try {
            val sectors = sectorDao.getSectors()
            val sectorResponse = SectorResponse(sectors?.sectors)
            emit(Result.Success(sectorResponse))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun insertBalanceSheetsByStock(balanceSheetEntities: List<BalanceSheetEntity>): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
            balanceSheetEntities.forEachIndexed { index, balanceSheetEntity ->
                val period = balanceSheetEntity.period
                val localData = balanceSheetDao.getBalanceSheetWithRatios(balanceSheetEntity.stockCode)?.balanceSheets?.find { it.period == period }
                if (localData != null) return@forEachIndexed
                val balanceSheetDate = balanceSheetDateDao.getDateByStockCodeAndPeriod(balanceSheetEntity.stockCode, balanceSheetEntity.period)
                val periodPrice = balanceSheetDate?.price ?: return@forEachIndexed
                val equities = balanceSheetEntity.equities.toDoubleOrDefault()
                val currentNetProfitAndLossForPeriod = balanceSheetEntity.netProfitAndLossPeriod.toDoubleOrDefault()
                val previousNetProfitAndLossForPeriod = if (index != balanceSheetEntities.lastIndex) balanceSheetEntities[index + 1].netProfitAndLossPeriod.toDoubleOrDefault() else 0.0
                val marketValue = balanceSheetEntity.paidCapital.toDoubleOrDefault() * periodPrice
                val bookValue = balanceSheetEntity.equitiesOfParentCompany.toDoubleOrDefault()

                val currentEPS = currentNetProfitAndLossForPeriod / balanceSheetEntity.paidCapital.toDoubleOrDefault()
                val previousEPS = if (index != balanceSheetEntities.lastIndex) balanceSheetEntities[index + 1].netOperatingProfitAndLoss.toDoubleOrDefault() / balanceSheetEntities[index + 1].paidCapital.toDoubleOrDefault() else 0.0
                val eps = currentEPS + previousEPS
                val netDebt = (balanceSheetEntity.shortTermLiabilities.toDoubleOrDefault() + balanceSheetEntity.longTermLiabilities.toDoubleOrDefault()) - (balanceSheetEntity.cashAndCashEquivalents.toDoubleOrDefault() + balanceSheetEntity.financialInvestments.toDoubleOrDefault())
                val companyValue = marketValue - netDebt
                val currentEbitda = balanceSheetEntity.grossProfitAndLoss.toDoubleOrDefault() + balanceSheetEntity.generalAndAdministrativeExpenses.toDoubleOrDefault() + balanceSheetEntity.marketingSalesAndDistributionExpenses.toDoubleOrDefault() + balanceSheetEntity.depreciationAndAmortization.toDoubleOrDefault()
                val previousEbitda = if (index != balanceSheetEntities.lastIndex) balanceSheetEntities[index + 1].grossProfitAndLoss.toDoubleOrDefault() + balanceSheetEntities[index + 1].generalAndAdministrativeExpenses.toDoubleOrDefault() + balanceSheetEntities[index + 1].marketingSalesAndDistributionExpenses.toDoubleOrDefault() + balanceSheetEntities[index + 1].depreciationAndAmortization.toDoubleOrDefault() else 0.0
                val netOperatingProfitAndLoss = balanceSheetEntity.netOperatingProfitAndLoss.toDoubleOrDefault()
                val currentSales = balanceSheetEntity.salesIncome.toDoubleOrDefault()
                val previousSales = if (index != balanceSheetEntities.lastIndex)balanceSheetEntities[index + 1].salesIncome.toDoubleOrDefault() else 0.0

                // RATIOS
                val marketBookAndBookValue = (marketValue / bookValue)
                val priceAndEarning = (periodPrice / eps)
                val companyValueAndEbitda = (companyValue / currentEbitda)
                val marketValueAndNetOperatingProfit = (marketValue / netOperatingProfitAndLoss)
                val companyValueAndNetSales = (companyValue / currentSales)
                val netOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / marketValue) * 100
                val netDebtAndEquities = netDebt / equities
                val salesGrowthRate = if (index != balanceSheetEntities.lastIndex) ((currentSales - previousSales) / previousSales) * 100 else 0.0
                val ebitdaGrowthRate = if (index != balanceSheetEntities.lastIndex) ((currentEbitda - previousEbitda) / previousEbitda) * 100 else 0.0
                val netProfitGrowthRate = if (index != balanceSheetEntities.lastIndex) ((currentNetProfitAndLossForPeriod - previousNetProfitAndLossForPeriod) / previousNetProfitAndLossForPeriod) * 100 else 0.0
                val operatingProfitMargin = netOperatingProfitAndLoss / currentSales * 100
                val equityProfitability = currentNetProfitAndLossForPeriod / equities * 100

                balanceSheetDao.insertBalanceSheetStock(BalanceSheetStockEntity(balanceSheetEntity.stockCode))
                balanceSheetDao.insertBalanceSheet(balanceSheetEntity)
                balanceSheetDao.insertBalanceSheetRatios(
                    BalanceSheetRatioEntity(
                        stockCode = balanceSheetEntity.stockCode,
                        period = period,
                        price = String.format(Locale.getDefault(), "%.2f", periodPrice),
                        ebitda = String.format(Locale.getDefault(), "%.2f", currentEbitda),
                        marketBookAndBookValue = String.format(Locale.getDefault(), "%.2f", marketBookAndBookValue),
                        priceAndEarning = String.format(Locale.getDefault(), "%.2f", priceAndEarning),
                        companyValueAndEbitda = String.format(Locale.getDefault(), "%.2f", companyValueAndEbitda),
                        marketValueAndNetOperatingProfit = String.format(Locale.getDefault(), "%.2f", marketValueAndNetOperatingProfit),
                        companyValueAndNetSales = String.format(Locale.getDefault(), "%.2f", companyValueAndNetSales),
                        netOperatingProfitAndMarketValue = String.format(Locale.getDefault(), "%.2f", netOperatingProfitAndMarketValue),
                        netDebtAndEquities = String.format(Locale.getDefault(), "%.2f", netDebtAndEquities),
                        salesGrowthRate = String.format(Locale.getDefault(), "%.2f", salesGrowthRate),
                        ebitdaGrowthRate = String.format(Locale.getDefault(), "%.2f", ebitdaGrowthRate),
                        netProfitGrowthRate = String.format(Locale.getDefault(), "%.2f", netProfitGrowthRate),
                        operatingProfitMargin = String.format(Locale.getDefault(), "%.2f", operatingProfitMargin),
                        equityProfitability = String.format(Locale.getDefault(), "%.2f", equityProfitability)
                    )
                )
            }
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun insertBalanceSheetsByPeriod(balanceSheetEntities: List<BalanceSheetEntity>): Flow<Result<Boolean>> = flow {
        emit(Result.Loading())
        try {
            balanceSheetEntities.forEachIndexed { index, balanceSheetEntity ->
                val period = balanceSheetEntity.period
                val localData = balanceSheetDao.getBalanceSheetWithRatios(balanceSheetEntity.stockCode)?.balanceSheets?.find { it.period == period }
                if (localData != null) return@forEachIndexed
                val balanceSheetDate = balanceSheetDateDao.getDateByStockCodeAndPeriod(balanceSheetEntity.stockCode, balanceSheetEntity.period)
                val periodPrice = balanceSheetDate?.price ?: return@forEachIndexed
                val equities = balanceSheetEntity.equities.toDoubleOrDefault()
                val currentNetProfitAndLossForPeriod = balanceSheetEntity.netProfitAndLossPeriod.toDoubleOrDefault()
                val previousNetProfitAndLossForPeriod = if (index != balanceSheetEntities.lastIndex) balanceSheetEntities[index + 1].netProfitAndLossPeriod.toDoubleOrDefault() else 0.0
                val marketValue = balanceSheetEntity.paidCapital.toDoubleOrDefault() * periodPrice
                val bookValue = balanceSheetEntity.equitiesOfParentCompany.toDoubleOrDefault()

                val currentEPS = currentNetProfitAndLossForPeriod / balanceSheetEntity.paidCapital.toDoubleOrDefault()
                val previousEPS = if (index != balanceSheetEntities.lastIndex) balanceSheetEntities[index + 1].netOperatingProfitAndLoss.toDoubleOrDefault() / balanceSheetEntities[index + 1].paidCapital.toDoubleOrDefault() else 0.0
                val eps = currentEPS + previousEPS
                val netDebt = (balanceSheetEntity.shortTermLiabilities.toDoubleOrDefault() + balanceSheetEntity.longTermLiabilities.toDoubleOrDefault()) - (balanceSheetEntity.cashAndCashEquivalents.toDoubleOrDefault() + balanceSheetEntity.financialInvestments.toDoubleOrDefault())
                val companyValue = marketValue - netDebt
                val currentEbitda = balanceSheetEntity.grossProfitAndLoss.toDoubleOrDefault() + balanceSheetEntity.generalAndAdministrativeExpenses.toDoubleOrDefault() + balanceSheetEntity.marketingSalesAndDistributionExpenses.toDoubleOrDefault() + balanceSheetEntity.depreciationAndAmortization.toDoubleOrDefault()
                val previousEbitda = if (index != balanceSheetEntities.lastIndex) balanceSheetEntities[index + 1].grossProfitAndLoss.toDoubleOrDefault() + balanceSheetEntities[index + 1].generalAndAdministrativeExpenses.toDoubleOrDefault() + balanceSheetEntities[index + 1].marketingSalesAndDistributionExpenses.toDoubleOrDefault() + balanceSheetEntities[index + 1].depreciationAndAmortization.toDoubleOrDefault() else 0.0
                val netOperatingProfitAndLoss = balanceSheetEntity.netOperatingProfitAndLoss.toDoubleOrDefault()
                val currentSales = balanceSheetEntity.salesIncome.toDoubleOrDefault()
                val previousSales = if (index != balanceSheetEntities.lastIndex)balanceSheetEntities[index + 1].salesIncome.toDoubleOrDefault() else 0.0

                val marketBookAndBookValue = (marketValue / bookValue)
                val priceAndEarning = (periodPrice / eps)
                val companyValueAndEbitda = (companyValue / currentEbitda)
                val marketValueAndNetOperatingProfit = (marketValue / netOperatingProfitAndLoss)
                val companyValueAndNetSales = (companyValue / currentSales)
                val netOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / marketValue) * 100
                val netDebtAndEquities = netDebt / equities
                val salesGrowthRate = if (index != balanceSheetEntities.lastIndex) ((currentSales - previousSales) / previousSales) * 100 else 0.0
                val ebitdaGrowthRate = if (index != balanceSheetEntities.lastIndex) ((currentEbitda - previousEbitda) / previousEbitda) * 100 else 0.0
                val netProfitGrowthRate = if (index != balanceSheetEntities.lastIndex) ((currentNetProfitAndLossForPeriod - previousNetProfitAndLossForPeriod) / previousNetProfitAndLossForPeriod) * 100 else 0.0
                val operatingProfitMargin = netOperatingProfitAndLoss / currentSales * 100
                val equityProfitability = currentNetProfitAndLossForPeriod / equities * 100

                balanceSheetDao.insertBalanceSheetStock(BalanceSheetStockEntity(balanceSheetEntity.stockCode))
                balanceSheetDao.insertBalanceSheet(balanceSheetEntity)
                balanceSheetDao.insertBalanceSheetRatios(
                    BalanceSheetRatioEntity(
                        stockCode = balanceSheetEntity.stockCode,
                        period = period,
                        price = String.format(Locale.getDefault(), "%.2f", periodPrice),
                        ebitda = String.format(Locale.getDefault(), "%.2f", currentEbitda),
                        marketBookAndBookValue = String.format(Locale.getDefault(), "%.2f", marketBookAndBookValue),
                        priceAndEarning = String.format(Locale.getDefault(), "%.2f", priceAndEarning),
                        companyValueAndEbitda = String.format(Locale.getDefault(), "%.2f", companyValueAndEbitda),
                        marketValueAndNetOperatingProfit = String.format(Locale.getDefault(), "%.2f", marketValueAndNetOperatingProfit),
                        companyValueAndNetSales = String.format(Locale.getDefault(), "%.2f", companyValueAndNetSales),
                        netOperatingProfitAndMarketValue = String.format(Locale.getDefault(), "%.2f", netOperatingProfitAndMarketValue),
                        netDebtAndEquities = String.format(Locale.getDefault(), "%.2f", netDebtAndEquities),
                        salesGrowthRate = String.format(Locale.getDefault(), "%.2f", salesGrowthRate),
                        ebitdaGrowthRate = String.format(Locale.getDefault(), "%.2f", ebitdaGrowthRate),
                        netProfitGrowthRate = String.format(Locale.getDefault(), "%.2f", netProfitGrowthRate),
                        operatingProfitMargin = String.format(Locale.getDefault(), "%.2f", operatingProfitMargin),
                        equityProfitability = String.format(Locale.getDefault(), "%.2f", equityProfitability)
                    )
                )
            }
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getBalanceSheetWithRatios(stockCode: String): Flow<Result<BalanceSheetWithRatios>> = flow {
        emit(Result.Loading())
        try {
            balanceSheetDao.getBalanceSheetWithRatios(stockCode)?.let { balanceSheetWithRatios ->
                val sortedBalanceSheetEntities = balanceSheetWithRatios.balanceSheets.sortedByDescending { entity ->
                    entity.period.totalNumber()
                }.filter { it.period.isComparePeriod() }
                val ratios = ArrayList<BalanceSheetRatioEntity>()
                val sortedBalanceSheetRatioEntities = balanceSheetWithRatios.ratios.sortedByDescending { entity ->
                    entity.period.totalNumber()
                }.filter { it.period.isComparePeriod() }
                val lastPeriodBalanceSheetEntity = sortedBalanceSheetEntities.first()
                balanceSheetDateDao.getStockLastPrice(lastPeriodBalanceSheetEntity.stockCode)?.let { stockLastPrice ->
                    val lastPrice = stockLastPrice.lastPrice
                    val marketValue = lastPeriodBalanceSheetEntity.paidCapital.toDoubleOrDefault() * lastPrice
                    val bookValue = lastPeriodBalanceSheetEntity.equitiesOfParentCompany.toDoubleOrDefault()
                    val eps = lastPeriodBalanceSheetEntity.periodProfitAndLoss.toDoubleOrDefault() / lastPeriodBalanceSheetEntity.paidCapital.toDoubleOrDefault()
                    val netDebt = (lastPeriodBalanceSheetEntity.financialDebtsShort.toDoubleOrDefault() + lastPeriodBalanceSheetEntity.financialDebtsLong.toDoubleOrDefault()) - (lastPeriodBalanceSheetEntity.cashAndCashEquivalents.toDoubleOrDefault() + lastPeriodBalanceSheetEntity.financialInvestments.toDoubleOrDefault())
                    val companyValue = marketValue - netDebt
                    val ebitda = lastPeriodBalanceSheetEntity.grossProfitAndLoss.toDoubleOrDefault() + lastPeriodBalanceSheetEntity.generalAndAdministrativeExpenses.toDoubleOrDefault() + lastPeriodBalanceSheetEntity.marketingSalesAndDistributionExpenses.toDoubleOrDefault() + lastPeriodBalanceSheetEntity.depreciationAndAmortization.toDoubleOrDefault()
                    val netOperatingProfitAndLoss = lastPeriodBalanceSheetEntity.netOperatingProfitAndLoss.toDoubleOrDefault()
                    val netSales = lastPeriodBalanceSheetEntity.salesIncome.toDoubleOrDefault()

                    val marketBookAndBookValue = marketValue / bookValue
                    val priceAndEarning = lastPrice / eps
                    val companyValueAndEbitda = companyValue / ebitda
                    val marketValueAndNetOperatingProfit = marketValue / netOperatingProfitAndLoss
                    val companyValueAndNetSales = companyValue / netSales
                    val netOperatingProfitAndMarketValue = (netOperatingProfitAndLoss / marketValue) * 100
                    val netDebtAndEquities = sortedBalanceSheetRatioEntities.first().netDebtAndEquities.cleanedNumberFormat().toDoubleOrDefault()
                    val salesGrowthRate = sortedBalanceSheetRatioEntities.first().salesGrowthRate.cleanedNumberFormat().toDoubleOrDefault()
                    val ebitdaGrowthRate = sortedBalanceSheetRatioEntities.first().ebitdaGrowthRate.cleanedNumberFormat().toDoubleOrDefault()
                    val netProfitGrowthRate = sortedBalanceSheetRatioEntities.first().netProfitGrowthRate.cleanedNumberFormat().toDoubleOrDefault()
                    val operatingProfitMargin = sortedBalanceSheetRatioEntities.first().operatingProfitMargin.cleanedNumberFormat().toDoubleOrDefault()
                    val equityProfitability = sortedBalanceSheetRatioEntities.first().equityProfitability.cleanedNumberFormat().toDoubleOrDefault()

                    val lastRatioEntity = BalanceSheetRatioEntity(
                        stockCode = stockCode,
                        period = "Bugün",
                        price = String.format(Locale.getDefault(), "%.2f", lastPrice),
                        ebitda = String.format(Locale.getDefault(), "%.2f", ebitda),
                        marketBookAndBookValue = String.format(Locale.getDefault(), "%.2f", marketBookAndBookValue),
                        priceAndEarning = String.format(Locale.getDefault(), "%.2f", priceAndEarning),
                        companyValueAndEbitda = String.format(Locale.getDefault(), "%.2f", companyValueAndEbitda),
                        marketValueAndNetOperatingProfit = String.format(Locale.getDefault(), "%.2f", marketValueAndNetOperatingProfit),
                        companyValueAndNetSales = String.format(Locale.getDefault(), "%.2f", companyValueAndNetSales),
                        netOperatingProfitAndMarketValue = String.format(Locale.getDefault(), "%.2f", netOperatingProfitAndMarketValue),
                        netDebtAndEquities = String.format(Locale.getDefault(), "%.2f", netDebtAndEquities),
                        salesGrowthRate = String.format(Locale.getDefault(), "%.2f", salesGrowthRate),
                        ebitdaGrowthRate = String.format(Locale.getDefault(), "%.2f", ebitdaGrowthRate),
                        netProfitGrowthRate = String.format(Locale.getDefault(), "%.2f", netProfitGrowthRate),
                        operatingProfitMargin = String.format(Locale.getDefault(), "%.2f", operatingProfitMargin),
                        equityProfitability = String.format(Locale.getDefault(), "%.2f", equityProfitability)
                    )
                    ratios.add(lastRatioEntity)
                }
                ratios.addAll(sortedBalanceSheetRatioEntities)
                emit(Result.Success(BalanceSheetWithRatios(balanceSheetWithRatios.stock, sortedBalanceSheetEntities, ratios)))
            } ?: kotlin.run {
                emit(Result.Error(404, "Hisse bulunamadı!"))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun getBalanceSheetRatiosList(period: String): Flow<Result<List<BalanceSheetRatioEntity>>> = flow {
        emit(Result.Loading())
        try {
            val stockWithDates = balanceSheetDao.getBalanceSheetRatiosEntities(period)
            emit(Result.Success(stockWithDates))
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }
}