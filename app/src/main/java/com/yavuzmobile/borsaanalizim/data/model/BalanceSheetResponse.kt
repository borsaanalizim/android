package com.yavuzmobile.borsaanalizim.data.model

/**
 * @param currentAssets Dönen Varlıklar - 1A
 * @param longTermAssets Duran Varlıklar - 1AK
 * @param paidCapital Ödenmiş Sermaye - 2OA
 * @param equities Özkaynaklar - 2N
 * @param equitiesOfParentCompany Ana Ortaklığa Ait Özkaynaklar
 * @param financialDebtsLong Finansal Borçlar
 * @param financialDebtsShort Finansal Borçlar2
 * @param cashAndCashEquivalents Nakit ve Nakit Benzerleri - 1AA
 * @param financialInvestments Finansal Yatırımlar - 1BC
 * @param netOperatingProfitAndLoss Net Faaliyet Kar/Zararı - 3H
 * @param salesIncome Satış Gelirleri - 3C
 * @param grossProfitAndLoss BRÜT KAR (ZARAR) - 3CAB
 * @param previousYearsProfitAndLoss Geçmiş Yıllar Kar/Zararları - 2OCE
 * @param netProfitAndLossPeriod Dönem Net Kar/Zarar - 2OCF
 * @param operatingProfitAndLoss FAALİYET KARI (ZARARI) - 3DF
 * @param depreciationExpenses Amortisman Giderleri
 * @param otherExpenses Faiz, Ücret, Prim, Komisyon ve Diğer Giderler (-)
 * @param periodTaxIncomeAndExpense Dönem Vergi Geliri (Gideri)
 * @param generalAndAdministrativeExpenses Pazarlama, Satış ve Dağıtım Giderleri (-)
 * @param costOfSales Satışların Maliyeti (-)
 * @param marketingSalesAndDistributionExpenses Pazarlama, Satış ve Dağıtım Giderleri (-)
 * @param researchAndDevelopmentExpenses Araştırma ve Geliştirme Giderleri (-)
 * @param depreciationAndAmortization Amortisman & İtfa Payları
 * @param shortTermLiabilities Kısa Vadeli Yükümlülükler -2A
 * @param longTermLiabilities Uzun Vadeli Yükümlülükler - 2B
 */
data class BalanceSheetResponse(
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
    val depreciationExpenses: String?,
    val otherExpenses: String?,
    val periodTaxIncomeAndExpense: String?,
    val generalAndAdministrativeExpenses: String?,
    val costOfSales: String?,
    val marketingSalesAndDistributionExpenses: String?,
    val researchAndDevelopmentExpenses: String?,
    val depreciationAndAmortization: String?,
    val shortTermLiabilities: String?,
    val longTermLiabilities: String?,
)
