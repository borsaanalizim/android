package com.yavuzmobile.borsaanalizim.ui.balancesheet

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yavuzmobile.borsaanalizim.ext.cleanedNumberFormat
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import com.yavuzmobile.borsaanalizim.ui.BaseScreen
import com.yavuzmobile.borsaanalizim.ui.component.bargraphwizard.BarGraphWizard
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BalanceSheetScreen(
    navController: NavController,
    code: String,
    viewModel: BalanceSheetViewModel = hiltViewModel()
) {

    val companyCardState by viewModel.companyCardState.collectAsState()
    val balanceSheetUiState by viewModel.balanceSheetUiState.collectAsState()

    LaunchedEffect(code) {
        viewModel.fetchData(code)
    }

    BaseScreen(navController, Modifier.fillMaxSize(), code) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            when(companyCardState.data) {
                null -> {}
                else -> {
                    Column(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {

                        val currentAssetList = mutableListOf<Double>()
                        val longTermAssetList = mutableListOf<Double>()
                        val equitiesList = mutableListOf<Double>()
                        val salesIncomeList = mutableListOf<Double>()
                        val grossProfitAndLossList = mutableListOf<Double>()
                        val netOperatingProfitAndLossList= mutableListOf<Double>()
                        val netProfitAndLossPeriodList= mutableListOf<Double>()
                        val cashAndCashEquivalentsList = mutableListOf<Double>()
                        val financialInvestmentsList = mutableListOf<Double>()
                        val financialDebtsShortList = mutableListOf<Double>()
                        val financialDebtsLongList = mutableListOf<Double>()
                        val shortTermLiabilitiesList = mutableListOf<Double>()
                        val longTermLiabilitiesList = mutableListOf<Double>()

                        val currentAssetGraphBarDataList = mutableListOf<Float>()
                        val longTermAssetGraphBarDataList = mutableListOf<Float>()
                        val equitiesGraphBarDataList = mutableListOf<Float>()
                        val salesIncomeGraphBarDataList = mutableListOf<Float>()
                        val grossProfitAndLossGraphBarDataList = mutableListOf<Float>()
                        val netOperatingProfitAndLossGraphBarDataList = mutableListOf<Float>()
                        val netProfitAndLossPeriodGraphBarDataList = mutableListOf<Float>()
                        val cashAndCashEquivalentsGraphBarDataList = mutableListOf<Float>()
                        val financialInvestmentsGraphBarDataList = mutableListOf<Float>()
                        val financialDebtsShortGraphBarDataList = mutableListOf<Float>()
                        val financialDebtsLongGraphBarDataList = mutableListOf<Float>()
                        val shortTermLiabilitiesGraphBarDataList = mutableListOf<Float>()
                        val longTermLiabilitiesGraphBarDataList = mutableListOf<Float>()

                        val periodList = mutableListOf<String>()

                        companyCardState.data?.let { companyCard ->
                            companyCard.balanceSheetResponses?.forEachIndexed { index, balanceSheetResponse ->
                                if (balanceSheetResponse.currentAssets.isNullOrEmpty()) return@forEachIndexed
                                currentAssetList.add(balanceSheetResponse.currentAssets.toDoubleOrDefault())
                                longTermAssetList.add(balanceSheetResponse.longTermAssets.toDoubleOrDefault())
                                equitiesList.add(balanceSheetResponse.equities.toDoubleOrDefault())
                                salesIncomeList.add(balanceSheetResponse.salesIncome.toDoubleOrDefault())
                                grossProfitAndLossList.add(balanceSheetResponse.grossProfitAndLoss.toDoubleOrDefault())
                                netOperatingProfitAndLossList.add(balanceSheetResponse.netOperatingProfitAndLoss.toDoubleOrDefault())
                                netProfitAndLossPeriodList.add(balanceSheetResponse.netProfitAndLossPeriod.toDoubleOrDefault())
                                cashAndCashEquivalentsList.add(balanceSheetResponse.cashAndCashEquivalents.toDoubleOrDefault())
                                financialInvestmentsList.add(balanceSheetResponse.financialInvestments.toDoubleOrDefault())
                                financialDebtsShortList.add(balanceSheetResponse.financialDebtsShort.toDoubleOrDefault())
                                financialDebtsLongList.add(balanceSheetResponse.financialDebtsLong.toDoubleOrDefault())
                                shortTermLiabilitiesList.add(balanceSheetResponse.shortTermLiabilities.toDoubleOrDefault())
                                longTermLiabilitiesList.add(balanceSheetResponse.longTermLiabilities.toDoubleOrDefault())

                                periodList.add(companyCard.period?.get(index).orEmpty())
                            }
                        }

                        currentAssetList.forEachIndexed { index, value ->
                            currentAssetGraphBarDataList.add(index = index, element = value.toFloat()/currentAssetList.max().toFloat())
                        }
                        longTermAssetList.forEachIndexed { index, value ->
                            longTermAssetGraphBarDataList.add(index = index, element = value.toFloat()/longTermAssetList.max().toFloat())
                        }
                        equitiesList.forEachIndexed { index, value ->
                            equitiesGraphBarDataList.add(index = index, element = value.toFloat()/equitiesList.max().toFloat())
                        }
                        salesIncomeList.forEachIndexed { index, value ->
                            salesIncomeGraphBarDataList.add(index = index, element = value.toFloat()/salesIncomeList.max().toFloat())
                        }
                        grossProfitAndLossList.forEachIndexed { index, value ->
                            grossProfitAndLossGraphBarDataList.add(index = index, element = value.toFloat()/grossProfitAndLossList.max().toFloat())
                        }
                        netOperatingProfitAndLossList.forEachIndexed { index, value ->
                            netOperatingProfitAndLossGraphBarDataList.add(index = index, element = value.toFloat()/netOperatingProfitAndLossList.max().toFloat())
                        }
                        netProfitAndLossPeriodList.forEachIndexed { index, value ->
                            netProfitAndLossPeriodGraphBarDataList.add(index = index, element = value.toFloat()/netProfitAndLossPeriodList.max().toFloat())
                        }
                        cashAndCashEquivalentsList.forEachIndexed { index, value ->
                            cashAndCashEquivalentsGraphBarDataList.add(index = index, element = value.toFloat()/cashAndCashEquivalentsList.max().toFloat())
                        }
                        financialInvestmentsList.forEachIndexed { index, value ->
                            financialInvestmentsGraphBarDataList.add(index = index, element = value.toFloat()/financialInvestmentsList.max().toFloat())
                        }
                        financialDebtsShortList.forEachIndexed { index, value ->
                            financialDebtsShortGraphBarDataList.add(index = index, element = value.toFloat()/financialDebtsShortList.max().toFloat())
                        }
                        financialDebtsLongList.forEachIndexed { index, value ->
                            financialDebtsLongGraphBarDataList.add(index = index, element = value.toFloat()/financialDebtsLongList.max().toFloat())
                        }
                        shortTermLiabilitiesList.forEachIndexed { index, value ->
                            shortTermLiabilitiesGraphBarDataList.add(index = index, element = value.toFloat()/shortTermLiabilitiesList.max().toFloat())
                        }
                        longTermLiabilitiesList.forEachIndexed { index, value ->
                            longTermLiabilitiesGraphBarDataList.add(index = index, element = value.toFloat()/longTermLiabilitiesList.max().toFloat())
                        }

                        BarGraphWizard("DÖNEN VARLIKLAR", currentAssetGraphBarDataList, periodList, currentAssetList)
                        BarGraphWizard("DURAN VARLIKLAR", longTermAssetGraphBarDataList, periodList, longTermAssetList)
                        BarGraphWizard("ÖZKAYNAKLAR", equitiesGraphBarDataList, periodList, equitiesList)
                        BarGraphWizard("SATIŞ GELİRLERİ", salesIncomeGraphBarDataList, periodList, salesIncomeList)
                        BarGraphWizard("BRÜT KAR (ZARAR)", grossProfitAndLossGraphBarDataList, periodList, grossProfitAndLossList)
                        BarGraphWizard("NET FAALİYET KAR/ZARARI", netOperatingProfitAndLossGraphBarDataList, periodList, netOperatingProfitAndLossList)
                        BarGraphWizard("DÖNEM NET KAR/ZARAR", netProfitAndLossPeriodGraphBarDataList, periodList, netProfitAndLossPeriodList)
                        BarGraphWizard("NAKİT VE NAKİT BENZERLERİ", cashAndCashEquivalentsGraphBarDataList, periodList, cashAndCashEquivalentsList)
                        BarGraphWizard("FİNANSAL YATIRIMLAR", financialInvestmentsGraphBarDataList, periodList, financialInvestmentsList)
                        BarGraphWizard("K.V. FİNANSAL BORÇLAR", financialDebtsShortGraphBarDataList, periodList, financialDebtsShortList)
                        BarGraphWizard("U.V. FİNANSAL BORÇLAR", financialDebtsLongGraphBarDataList, periodList, financialDebtsLongList)
                        BarGraphWizard("KISA VADELİ YÜKÜMLÜLÜKLER", shortTermLiabilitiesGraphBarDataList, periodList, shortTermLiabilitiesList)
                        BarGraphWizard("UZUN VADELİ YÜKÜMLÜLÜKLER", longTermLiabilitiesGraphBarDataList, periodList, longTermLiabilitiesList)
                    }
                }
            }
            when (balanceSheetUiState.isLoading) {
                true -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                false -> {
                    balanceSheetUiState.error?.let { error ->
                        Text(text = "Hata: $error", color = MaterialTheme.colorScheme.error)
                    }

                    balanceSheetUiState.data?.let { balanceSheets ->

                        val marketBookAndBookValueMin = balanceSheets.minOfOrNull { it.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
                        val marketBookAndBookValueMax = balanceSheets.maxOfOrNull { it.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

                        val priceAndEarningMin = balanceSheets.minOfOrNull { it.priceAndEarning.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
                        val priceAndEarningMax = balanceSheets.maxOfOrNull { it.priceAndEarning.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

                        val companyValueAndEbitdaMin = balanceSheets.minOfOrNull { it.companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
                        val companyValueAndEbitdaMax = balanceSheets.maxOfOrNull { it.companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

                        val marketValueAndNetOperatingProfitMin = balanceSheets.minOfOrNull { it.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
                        val marketValueAndNetOperatingProfitMax = balanceSheets.maxOfOrNull { it.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

                        val companyValueAndNetSalesMin = balanceSheets.minOfOrNull { it.companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
                        val companyValueAndNetSalesMax = balanceSheets.maxOfOrNull { it.companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

                        val netOperatingProfitAndMarketValueMin = balanceSheets.minOfOrNull { it.netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
                        val netOperatingProfitAndMarketValueMax = balanceSheets.maxOfOrNull { it.netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Column(
                                Modifier
                                    .width(75.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text("DÖNEM", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                balanceSheets.forEach { balanceSheet ->
                                    if (balanceSheet.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                                        return@forEach
                                    }
                                    Text(balanceSheet.period, modifier = Modifier.fillMaxWidth())
                                }
                            }
                            Column(
                                Modifier
                                    .width(75.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    "Fiyat",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                balanceSheets.forEach { balanceSheet ->
                                    Text(String.format(Locale.getDefault(), "%.2f", balanceSheet.price), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                }
                            }
                            Column(
                                Modifier
                                    .width(75.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    "PD / DD",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                balanceSheets.forEach { balanceSheet ->
                                    if (balanceSheet.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                                        return@forEach
                                    }
                                    val backgroundColor = getBackgroundColor(
                                        balanceSheet.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault(),
                                        marketBookAndBookValueMin,
                                        marketBookAndBookValueMax
                                    )
                                    Text(
                                        balanceSheet.marketBookAndBookValue, modifier = Modifier
                                            .fillMaxWidth()
                                            .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                                    )
                                }
                            }
                            Column(
                                Modifier
                                    .width(75.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    "F / K",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                balanceSheets.forEach { balanceSheet ->
                                    if (balanceSheet.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                                        return@forEach
                                    }
                                    val backgroundColor = getBackgroundColor(
                                        balanceSheet.priceAndEarning.cleanedNumberFormat().toDoubleOrDefault(),
                                        priceAndEarningMin,
                                        priceAndEarningMax
                                    )
                                    Text(
                                        if (balanceSheet.priceAndEarning.cleanedNumberFormat().toDoubleOrDefault() < 0) "-"
                                        else balanceSheet.priceAndEarning, modifier = Modifier
                                            .fillMaxWidth()
                                            .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                                    )
                                }
                            }
                            Column(
                                Modifier
                                    .width(105.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    "FD / FAVÖK",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                balanceSheets.forEach { balanceSheet ->
                                    if (balanceSheet.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                                        return@forEach
                                    }
                                    val backgroundColor = getBackgroundColor(
                                        balanceSheet.companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault(),
                                        companyValueAndEbitdaMin,
                                        companyValueAndEbitdaMax
                                    )
                                    Text(
                                        if (balanceSheet.companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault() < 0) "-"
                                        else balanceSheet.companyValueAndEbitda, modifier = Modifier
                                            .fillMaxWidth()
                                            .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                                    )
                                }
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    "PD / NFK",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                balanceSheets.forEach { balanceSheet ->
                                    if (balanceSheet.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                                        return@forEach
                                    }
                                    val backgroundColor = getBackgroundColor(
                                        balanceSheet.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault(),
                                        marketValueAndNetOperatingProfitMin,
                                        marketValueAndNetOperatingProfitMax
                                    )
                                    Text(
                                        if (balanceSheet.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault() < 0) "-"
                                        else balanceSheet.marketValueAndNetOperatingProfit, modifier = Modifier
                                            .fillMaxWidth()
                                            .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                                    )
                                }
                            }
                            Column(
                                Modifier
                                    .width(75.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    "FD / NS",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                balanceSheets.forEach { balanceSheet ->
                                    if (balanceSheet.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                                        return@forEach
                                    }
                                    val backgroundColor = getBackgroundColor(
                                        balanceSheet.companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault(),
                                        companyValueAndNetSalesMin,
                                        companyValueAndNetSalesMax
                                    )
                                    Text(
                                        if (balanceSheet.companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault() < 0) "-"
                                        else balanceSheet.companyValueAndNetSales, modifier = Modifier
                                            .fillMaxWidth()
                                            .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                                    )
                                }
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    "NFK / PD",
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                balanceSheets.forEach { balanceSheet ->
                                    if (balanceSheet.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                                        return@forEach
                                    }
                                    val backgroundColor = getBackgroundColor(
                                        balanceSheet.netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault(),
                                        netOperatingProfitAndMarketValueMin,
                                        netOperatingProfitAndMarketValueMax,
                                        true
                                    )
                                    Text(
                                        balanceSheet.netOperatingProfitAndMarketValue + "%", modifier = Modifier
                                            .fillMaxWidth()
                                            .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getBackgroundColor(value: Double, min: Double, max: Double, isInverted: Boolean = false): Color {
    val mid = if (min < 0) max / 2.0 else (min + max) / 2.0
    val minTwo = if (min < 0) mid - mid / 10 else mid - (mid + min) / 10
    val minThree = if (min < 0) mid - mid / 10 * 2 else mid - (mid + min) / 10 * 2
    val minFour = if (min < 0) mid - mid / 10 * 3 else mid - (mid + min) / 10 * 3
    val minFive = if (min < 0) mid - mid / 10 * 4 else mid - (mid + min) / 10 * 4
    val minSix = if (min < 0) mid - mid / 10 * 5 else mid - (mid + min) / 10 * 5
    val minSeven = if (min < 0) mid - mid / 10 * 6 else mid - (mid + min) / 10 * 6
    val minEight = if (min < 0) mid - mid / 10 * 7 else mid - (mid + min) / 10 * 7
    val minNine = if (min < 0) mid - mid / 10 * 8 else mid - (mid + min) / 10 * 8
    val minTen = if (min < 0) mid / 10 else min + (mid + min) / 10
    val maxTwo = max - (max - mid) / 10
    val maxThree = max - (max - mid) / 10 * 2
    val maxFour = max - (max - mid) / 10 * 3
    val maxFive = mid - (max - mid) / 10 * 4
    val maxSix = mid - (max - mid) / 10 * 5
    val maxSeven = mid - (max - mid) / 10 * 6
    val maxEight = mid - (max - mid) / 10 * 7
    val maxNine = mid - (max - mid) / 10 * 8
    val maxTen = mid - (max - mid) / 10 * 9

    // 00DB00 - 0, 219, 0 - 70%, 0%, 100%, 0%
    // 78FC78 - 120, 252, 120 - 46%, 0%, 78%, 0%
    // C3F9C3 - 195, 249, 195 - 22%, 0%, 32%, 0%
    // FACEA6 - 250, 206, 166 - 2%, 20%, 35%, 0% - mid
    // FA9B9B - 250, 155, 155 - 1%, 48%, 27%, 0%
    // FA6868 - 250, 1, 104 - 0%, 99%, 35%, 0%
    // FF0000 - 255, 0, 0 - 0%, 99%, 100%, 0%

    return when {
        value < 0 -> if (isInverted) Color(0xFFA30000)  else Color(0xFFD5D5D5)
        value == min -> if (isInverted) Color(0xFFAC1A1A) else Color(0xFF00A300)
        value == max -> if (isInverted) Color(0xFF00A300) else Color(0xFFAC1A1A)
        value > min && value <= minTen -> if (isInverted) Color(0xFFB53333) else Color(0xFF1AAC1A)
        value > minTen && value <= minNine -> if (isInverted) Color(0xFFBE4D4D) else Color(0xFF33B533)
        value > minNine && value <= minEight -> if (isInverted) Color(0xFFC76666) else Color(0xFF4DBE4D)
        value > minEight && value <= minSeven -> if (isInverted) Color(0xFFD08080) else Color(0xFF66C766)
        value > minSeven && value <= minSix -> if (isInverted) Color(0xFFD99999) else Color(0xFF80D080)
        value > minSix && value <= minFive -> if (isInverted) Color(0xFFE2B3B3) else Color(0xFF99D999)
        value > minFive && value <= minFour -> if (isInverted) Color(0xFFEBCCCC) else Color(0xFFB3E2B3)
        value > minFour && value <= minThree -> if (isInverted) Color(0xFFF4E6E6) else Color(0xFFCCEBCC)
        value > minThree && value <= minTwo -> if (isInverted) Color(0xFFF4E6E6) else Color(0xFFCCEBCC)
        value < max && value >= maxTwo -> if (isInverted) Color(0xFF1AAC1A) else Color(0xFFAC1A1A)
        value < maxTwo && value >= maxThree -> if (isInverted) Color(0xFF33B533) else Color(0xFFB53333)
        value < maxThree && value >= maxFour -> if (isInverted) Color(0xFF4DBE4D) else Color(0xFFBE4D4D)
        value < maxFour && value >= maxFive -> if (isInverted) Color(0xFF66C766) else Color(0xFFC76666)
        value < maxFive && value >= maxSix -> if (isInverted) Color(0xFF80D080) else Color(0xFFD08080)
        value < maxSix && value >= maxSeven -> if (isInverted) Color(0xFF99D999) else Color(0xFFD99999)
        value < maxSeven && value >= maxEight -> if (isInverted) Color(0xFF99D999) else Color(0xFFE2B3B3)
        value < maxEight && value >= maxNine -> if (isInverted) Color(0xFFB3E2B3) else Color(0xFFEBCCCC)
        value < maxNine && value >= maxTen -> if (isInverted) Color(0xFFCCEBCC) else Color(0xFFF4E6E6)
        else -> if (isInverted) Color(0xFFA30000) else Color(0xFF00A300)
    }
}
