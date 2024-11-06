package com.yavuzmobile.borsaanalizim.data.repository

import com.yavuzmobile.borsaanalizim.data.Result
import com.yavuzmobile.borsaanalizim.data.api.Api
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetDateStockWithDates
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.DateEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.IndexEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.SectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockAndIndexAndSectorEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockEntity
import com.yavuzmobile.borsaanalizim.data.local.entity.StockInIndexEntity
import com.yavuzmobile.borsaanalizim.ext.isComparePeriod
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import com.yavuzmobile.borsaanalizim.ext.totalNumber
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

    fun getBalanceSheetDatesByStock(stockCode: String, mkkMemberOid: String): Flow<Result<BalanceSheetDateStockWithDates>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchBalanceSheetDatesByStock(stockCode, mkkMemberOid)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    if (data.stockCode != null && data.lastPrice != null && data.dates != null) {
                        val balanceSheetDateStockWithDates = BalanceSheetDateStockWithDates(
                            BalanceSheetDateStockEntity(data.stockCode, data.lastPrice),
                            data.dates.map { mappedDate ->
                                DateEntity(
                                    period = mappedDate.period.orEmpty(),
                                    publishedAt = mappedDate.publishedAt.orEmpty(),
                                    price = mappedDate.price.toString().toDoubleOrDefault(),
                                    stockCode = data.stockCode
                                )
                            })
                        emit(Result.Success(balanceSheetDateStockWithDates))
                    } else {
                        emit(Result.Error(500, "response parameter is null"))
                    }
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
                        if (!it.code.isNullOrEmpty() && !it.name.isNullOrEmpty() && !it.financialGroup.isNullOrEmpty() && it.mkkMemberOid != null && !it.sector.isNullOrEmpty() && it.indexes != null) {
                            StockAndIndexAndSectorEntity(
                                stock = StockEntity(it.code, it.name, it.financialGroup, it.mkkMemberOid, it.sector),
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

    fun getSectors(): Flow<Result<SectorEntity>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchSectors()
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    val sectorEntity = SectorEntity(sectors = data.sectors.orEmpty())
                    emit(Result.Success(sectorEntity))
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

    suspend fun fetchBalanceSheets(): Flow<Result<List<BalanceSheetEntity>>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchBalanceSheets()
            if (response.isSuccessful) {
                val balanceSheetList = ArrayList<BalanceSheetEntity>()
                response.body()?.data?.forEach { data ->
                    data.balanceSheets?.forEach { balanceSheetItem ->
                        balanceSheetList.add(
                            BalanceSheetEntity(
                                stockCode = data.stockCode.orEmpty(),
                                period = balanceSheetItem.period.orEmpty(),
                                currentAssets = balanceSheetItem.currentAssets.orEmpty(),
                                longTermAssets = balanceSheetItem.longTermAssets.orEmpty(),
                                paidCapital = balanceSheetItem.paidCapital.orEmpty(),
                                equities = balanceSheetItem.equities.orEmpty(),
                                equitiesOfParentCompany = balanceSheetItem.equitiesOfParentCompany.orEmpty(),
                                financialDebtsLong = balanceSheetItem.financialDebtsLong.orEmpty(),
                                financialDebtsShort = balanceSheetItem.financialDebtsShort.orEmpty(),
                                cashAndCashEquivalents = balanceSheetItem.cashAndCashEquivalents.orEmpty(),
                                financialInvestments = balanceSheetItem.financialInvestments.orEmpty(),
                                netOperatingProfitAndLoss = balanceSheetItem.netOperatingProfitAndLoss.orEmpty(),
                                salesIncome = balanceSheetItem.salesIncome.orEmpty(),
                                grossProfitAndLoss = balanceSheetItem.grossProfitAndLoss.orEmpty(),
                                previousYearsProfitAndLoss = balanceSheetItem.previousYearsProfitAndLoss.orEmpty(),
                                netProfitAndLossPeriod = balanceSheetItem.netProfitAndLossPeriod.orEmpty(),
                                operatingProfitAndLoss = balanceSheetItem.operatingProfitAndLoss.orEmpty(),
                                periodProfitAndLoss = balanceSheetItem.periodProfitAndLoss.orEmpty(),
                                depreciationExpenses = balanceSheetItem.depreciationExpenses.orEmpty(),
                                otherExpenses = balanceSheetItem.otherExpenses.orEmpty(),
                                periodTaxIncomeAndExpense = balanceSheetItem.periodTaxIncomeAndExpense.orEmpty(),
                                generalAndAdministrativeExpenses = balanceSheetItem.generalAndAdministrativeExpenses.orEmpty(),
                                costOfSales = balanceSheetItem.costOfSales.orEmpty(),
                                marketingSalesAndDistributionExpenses = balanceSheetItem.marketingSalesAndDistributionExpenses.orEmpty(),
                                researchAndDevelopmentExpenses = balanceSheetItem.researchAndDevelopmentExpenses.orEmpty(),
                                depreciationAndAmortization = balanceSheetItem.depreciationAndAmortization.orEmpty(),
                                shortTermLiabilities = balanceSheetItem.shortTermLiabilities.orEmpty(),
                                longTermLiabilities = balanceSheetItem.longTermLiabilities.orEmpty(),
                            )
                        )
                    }
                }
                emit(Result.Success(balanceSheetList))
            } else {
                emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }

    suspend fun fetchBalanceSheetsByStock(stockCode: String): Flow<Result<List<BalanceSheetEntity>>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchBalanceSheetsByStock(stockCode)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    val balanceSheetList = ArrayList<BalanceSheetEntity>()
                    data.balanceSheets?.forEach { balanceSheetItem ->
                        balanceSheetList.add(
                            BalanceSheetEntity(
                                stockCode = data.stockCode.orEmpty(),
                                period = balanceSheetItem.period.orEmpty(),
                                currentAssets = balanceSheetItem.currentAssets.orEmpty(),
                                longTermAssets = balanceSheetItem.longTermAssets.orEmpty(),
                                paidCapital = balanceSheetItem.paidCapital.orEmpty(),
                                equities = balanceSheetItem.equities.orEmpty(),
                                equitiesOfParentCompany = balanceSheetItem.equitiesOfParentCompany.orEmpty(),
                                financialDebtsLong = balanceSheetItem.financialDebtsLong.orEmpty(),
                                financialDebtsShort = balanceSheetItem.financialDebtsShort.orEmpty(),
                                cashAndCashEquivalents = balanceSheetItem.cashAndCashEquivalents.orEmpty(),
                                financialInvestments = balanceSheetItem.financialInvestments.orEmpty(),
                                netOperatingProfitAndLoss = balanceSheetItem.netOperatingProfitAndLoss.orEmpty(),
                                salesIncome = balanceSheetItem.salesIncome.orEmpty(),
                                grossProfitAndLoss = balanceSheetItem.grossProfitAndLoss.orEmpty(),
                                previousYearsProfitAndLoss = balanceSheetItem.previousYearsProfitAndLoss.orEmpty(),
                                netProfitAndLossPeriod = balanceSheetItem.netProfitAndLossPeriod.orEmpty(),
                                operatingProfitAndLoss = balanceSheetItem.operatingProfitAndLoss.orEmpty(),
                                periodProfitAndLoss = balanceSheetItem.periodProfitAndLoss.orEmpty(),
                                depreciationExpenses = balanceSheetItem.depreciationExpenses.orEmpty(),
                                otherExpenses = balanceSheetItem.otherExpenses.orEmpty(),
                                periodTaxIncomeAndExpense = balanceSheetItem.periodTaxIncomeAndExpense.orEmpty(),
                                generalAndAdministrativeExpenses = balanceSheetItem.generalAndAdministrativeExpenses.orEmpty(),
                                costOfSales = balanceSheetItem.costOfSales.orEmpty(),
                                marketingSalesAndDistributionExpenses = balanceSheetItem.marketingSalesAndDistributionExpenses.orEmpty(),
                                researchAndDevelopmentExpenses = balanceSheetItem.researchAndDevelopmentExpenses.orEmpty(),
                                depreciationAndAmortization = balanceSheetItem.depreciationAndAmortization.orEmpty(),
                                shortTermLiabilities = balanceSheetItem.shortTermLiabilities.orEmpty(),
                                longTermLiabilities = balanceSheetItem.longTermLiabilities.orEmpty(),
                            )
                        )
                    }
                    val sortedList = balanceSheetList.sortedByDescending { it.period.totalNumber() }
                    emit(Result.Success(sortedList))
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

    suspend fun fetchBalanceSheetsByPeriod(period: String): Flow<Result<List<BalanceSheetEntity>>> = flow {
        emit(Result.Loading())
        try {
            val response = api.fetchBalanceSheetsByPeriod(period)
            if (response.isSuccessful) {
                val balanceSheetList = ArrayList<BalanceSheetEntity>()
                response.body()?.data?.forEach { data ->
                    data.balanceSheets?.forEach { balanceSheetItem ->
                        balanceSheetList.add(
                            BalanceSheetEntity(
                                stockCode = data.stockCode.orEmpty(),
                                period = balanceSheetItem.period.orEmpty(),
                                currentAssets = balanceSheetItem.currentAssets.orEmpty(),
                                longTermAssets = balanceSheetItem.longTermAssets.orEmpty(),
                                paidCapital = balanceSheetItem.paidCapital.orEmpty(),
                                equities = balanceSheetItem.equities.orEmpty(),
                                equitiesOfParentCompany = balanceSheetItem.equitiesOfParentCompany.orEmpty(),
                                financialDebtsLong = balanceSheetItem.financialDebtsLong.orEmpty(),
                                financialDebtsShort = balanceSheetItem.financialDebtsShort.orEmpty(),
                                cashAndCashEquivalents = balanceSheetItem.cashAndCashEquivalents.orEmpty(),
                                financialInvestments = balanceSheetItem.financialInvestments.orEmpty(),
                                netOperatingProfitAndLoss = balanceSheetItem.netOperatingProfitAndLoss.orEmpty(),
                                salesIncome = balanceSheetItem.salesIncome.orEmpty(),
                                grossProfitAndLoss = balanceSheetItem.grossProfitAndLoss.orEmpty(),
                                previousYearsProfitAndLoss = balanceSheetItem.previousYearsProfitAndLoss.orEmpty(),
                                netProfitAndLossPeriod = balanceSheetItem.netProfitAndLossPeriod.orEmpty(),
                                operatingProfitAndLoss = balanceSheetItem.operatingProfitAndLoss.orEmpty(),
                                periodProfitAndLoss = balanceSheetItem.periodProfitAndLoss.orEmpty(),
                                depreciationExpenses = balanceSheetItem.depreciationExpenses.orEmpty(),
                                otherExpenses = balanceSheetItem.otherExpenses.orEmpty(),
                                periodTaxIncomeAndExpense = balanceSheetItem.periodTaxIncomeAndExpense.orEmpty(),
                                generalAndAdministrativeExpenses = balanceSheetItem.generalAndAdministrativeExpenses.orEmpty(),
                                costOfSales = balanceSheetItem.costOfSales.orEmpty(),
                                marketingSalesAndDistributionExpenses = balanceSheetItem.marketingSalesAndDistributionExpenses.orEmpty(),
                                researchAndDevelopmentExpenses = balanceSheetItem.researchAndDevelopmentExpenses.orEmpty(),
                                depreciationAndAmortization = balanceSheetItem.depreciationAndAmortization.orEmpty(),
                                shortTermLiabilities = balanceSheetItem.shortTermLiabilities.orEmpty(),
                                longTermLiabilities = balanceSheetItem.longTermLiabilities.orEmpty(),
                            )
                        )
                    }
                }
                emit(Result.Success(balanceSheetList))
            } else {
                emit(Result.Error(response.code(), response.errorBody()?.string().toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(500, e.message.toString()))
        }
    }
}