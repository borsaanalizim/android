package com.yavuzmobile.borsaanalizim.data.model

data class BalanceSheetItemResponse(
    val period: String?,
    val currentAssets: String?,
    val longTermAssets: String?,
    val paidCapital : String?,
    val equities: String?,
    val equitiesOfParentCompany: String?,
    val financialDebtsLong: String?,
    val financialDebtsShort: String?,
    val cashAndCashEquivalents: String?,
    val financialInvestments: String?,
    val netOperatingProfitAndLoss: String?,
    val salesIncome: String?,
    val grossProfitAndLoss: String?,
    val previousYearsProfitAndLoss: String?,
    val netProfitAndLossPeriod: String?,
    val operatingProfitAndLoss: String?,
    val periodProfitAndLoss: String?,
    val depreciationExpenses: String?,
    val otherExpenses: String?,
    val periodTaxIncomeAndExpense: String?,
    val generalAndAdministrativeExpenses: String?,
    val costOfSales: String?,
    val marketingSalesAndDistributionExpenses: String?,
    val researchAndDevelopmentExpenses: String?,
    val depreciationAndAmortization: String?,
    val shortTermLiabilities: String?,
    val longTermLiabilities: String?
)
