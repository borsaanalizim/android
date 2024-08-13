package com.yavuzmobile.borsaanalizim.data.model

/**
 * @param paidCapital Ödenmiş Sermaye
 * @param equities Özkaynaklar
 * @param financialDebtsLong Finansal Borçlar
 * @param financialDebtsShort Finansal Borçlar2
 * @param cashAndCashEquivalents Nakit ve Nakit Benzerleri
 * @param financialInvestments Finansal Yatırımlar
 * @param netOperatingProfitAndLoss Net Faaliyet Kar/Zarar
 * @param salesIncome Satış Gelirleri
 * @param grossProfitAndLoss Brüt Kar/Zarar
 * @param netProfitAndLossPeriod Dönem Net Kar/Zarar
 * @param operatingProfitAndLoss Faaliyet Kar/Zarar
 * @param depreciationExpenses Amortisman Giderleri
 * @param otherExpenses Faiz, Ücret, Prim, Komisyon ve Diğer Giderler (-)
 * @param periodTaxIncomeAndExpense Dönem Vergi Geliri (Gideri)
 * @param generalAndAdministrativeExpenses Pazarlama, Satış ve Dağıtım Giderleri (-)
 * @param costOfSales Satışların Maliyeti (-)
 * @param marketingSalesAndDistributionExpenses Pazarlama, Satış ve Dağıtım Giderleri (-)
 * @param researchAndDevelopmentExpenses Araştırma ve Geliştirme Giderleri (-)
 * @param depreciationAndAmortization Amortisman & İtfa Payları
 */
data class BalanceSheet(
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
    val depreciationExpenses: String?,
    val otherExpenses: String?,
    val periodTaxIncomeAndExpense: String?,
    val generalAndAdministrativeExpenses: String?,
    val costOfSales: String?,
    val marketingSalesAndDistributionExpenses: String?,
    val researchAndDevelopmentExpenses: String?,
    val depreciationAndAmortization: String?,
)
