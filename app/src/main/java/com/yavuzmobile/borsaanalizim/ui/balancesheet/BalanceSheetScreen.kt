package com.yavuzmobile.borsaanalizim.ui.balancesheet

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatioEntity
import com.yavuzmobile.borsaanalizim.enums.ActionButtons
import com.yavuzmobile.borsaanalizim.ext.cleanedNumberFormat
import com.yavuzmobile.borsaanalizim.ext.decimalNumberFormat
import com.yavuzmobile.borsaanalizim.ext.findActivity
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import com.yavuzmobile.borsaanalizim.ui.BaseScreen
import com.yavuzmobile.borsaanalizim.ui.component.bargraphwizard.BarGraphWizard
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_COMPANY_VALUE_AND_EBITDA
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_COMPANY_VALUE_AND_NET_SALES
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_EBITDA_GROWTH_RATE_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_MARKET_BOOK_AND_BOOK_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_MARKET_VALUE_AND_OPERATION_PROFIT
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_NET_DEBT_EQUITY_RATIO_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_NET_OPERATING_MARGIN_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_NET_OPERATING_PROFIT_AND_MARKET_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_NET_PROFIT_GROWTH_RATE_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_NET_REVENUE_GROWTH_RATE_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_PRICE_AND_EARNING
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_RETURN_ON_EQUITY_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_COMPANY_VALUE_AND_EBITDA
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_COMPANY_VALUE_AND_NET_SALES
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_EBITDA_GROWTH_RATE_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_MARKET_BOOK_AND_BOOK_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_MARKET_VALUE_AND_OPERATION_PROFIT
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_NET_DEBT_EQUITY_RATIO_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_NET_OPERATING_MARGIN_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_NET_OPERATING_PROFIT_AND_MARKET_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_NET_PROFIT_GROWTH_RATE_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_NET_REVENUE_GROWTH_RATE_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_PRICE_AND_EARNING
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_RETURN_ON_EQUITY_VALUE
import com.yavuzmobile.borsaanalizim.util.ShareUtil
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun BalanceSheetScreen(
    navController: NavController,
    code: String,
    viewModel: BalanceSheetViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context.findActivity()

    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    val balanceSheetWithRatiosUiState by viewModel.balanceSheetWithRatiosState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val graphicsLayerTableRatios = rememberGraphicsLayer()

    val rememberedCode = remember { code }

    LaunchedEffect(rememberedCode) {
        viewModel.getBalanceSheetsByStock(code)
    }

    BaseScreen(navController, Modifier.fillMaxSize(), code, actionButton = ActionButtons.SHARE, onClickAction = {
        coroutineScope.launch {
            val bitmap = graphicsLayerTableRatios.toImageBitmap()
            ShareUtil.shareBitmap(context, bitmap.asAndroidBitmap(), code)
        }
    }) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { coroutineScope.launch { viewModel.fetchBalanceSheetsByStock(code) } },) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Yenile")
                }
            }

            when(balanceSheetWithRatiosUiState.isLoading) {
                true -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                false -> {
                    balanceSheetWithRatiosUiState.error?.let { error ->
                        Text(text = "Hata: $error", color = MaterialTheme.colorScheme.error)
                    }
                    balanceSheetWithRatiosUiState.data?.let { balanceSheetWithRatios ->
                        Column(modifier = Modifier
                            .padding(start = 32.dp, end = 16.dp)
                            .fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

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

                            balanceSheetWithRatios.balanceSheets.forEach { balanceSheetResponse ->
                                if (balanceSheetResponse.currentAssets.isEmpty()) return@forEach
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

                                periodList.add(balanceSheetResponse.period)
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

                            Text("PD/DD: " + String.format(Locale.getDefault(), "%.2f", balanceSheetWithRatios.ratios.first().marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault()), Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                            Text("F/K: " + String.format(Locale.getDefault(), "%.2f", balanceSheetWithRatios.ratios.first().priceAndEarning.cleanedNumberFormat().toDoubleOrDefault()), Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                            val lastPeriod = balanceSheetWithRatios.ratios[1].period
                            val lastEbitda = balanceSheetWithRatios.ratios[1].ebitda
                            if (balanceSheetWithRatios.ratios.size > 2) {
                                val previousEbitda = balanceSheetWithRatios.ratios[2].ebitda
                                val previousPeriod = balanceSheetWithRatios.
                                ratios[2].period
                                val ebitdaRatio = (lastEbitda.cleanedNumberFormat().toDoubleOrDefault() - previousEbitda.cleanedNumberFormat().toDoubleOrDefault()) / previousEbitda.cleanedNumberFormat().toDoubleOrDefault()
                                Text("${lastPeriod}: " + lastEbitda + " (%${String.format(Locale.getDefault(), "%.2f", ebitdaRatio)})", Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                                Text("${previousPeriod}: " + previousEbitda, Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                            }
                            if (balanceSheetWithRatios.ratios.size > 5) {
                                val previousYearEbitda = balanceSheetWithRatios.ratios[5].ebitda
                                val previousYearPeriod = balanceSheetWithRatios.ratios[5].period
                                val ebitdaRatio = (lastEbitda.cleanedNumberFormat().toDoubleOrDefault() - previousYearEbitda.cleanedNumberFormat().toDoubleOrDefault()) / previousYearEbitda.cleanedNumberFormat().toDoubleOrDefault()
                                Text("${previousYearPeriod}: " + previousYearEbitda + " (%${String.format(Locale.getDefault(), "%.2f", ebitdaRatio)})", Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                            }

                            BarGraphWizard("DÖNEN VARLIKLAR", currentAssetGraphBarDataList, periodList, currentAssetList)
                            BarGraphWizard("DURAN VARLIKLAR", longTermAssetGraphBarDataList, periodList, longTermAssetList)
                            BarGraphWizard("ÖZKAYNAKLAR", equitiesGraphBarDataList, periodList, equitiesList)
                            BarGraphWizard("SATIŞ GELİRLERİ", salesIncomeGraphBarDataList, periodList, salesIncomeList)
                            BarGraphWizard("BRÜT KAR-ZARAR", grossProfitAndLossGraphBarDataList, periodList, grossProfitAndLossList)
                            BarGraphWizard("NET FAALİYET KAR-ZARAR", netOperatingProfitAndLossGraphBarDataList, periodList, netOperatingProfitAndLossList)
                            BarGraphWizard("DÖNEM NET KAR-ZARAR", netProfitAndLossPeriodGraphBarDataList, periodList, netProfitAndLossPeriodList)
                            BarGraphWizard("NAKİT VE NAKİT BENZERLERİ", cashAndCashEquivalentsGraphBarDataList, periodList, cashAndCashEquivalentsList)
                            BarGraphWizard("FİNANSAL YATIRIMLAR", financialInvestmentsGraphBarDataList, periodList, financialInvestmentsList)
                            BarGraphWizard("K.V FİNANSAL BORÇLAR", financialDebtsShortGraphBarDataList, periodList, financialDebtsShortList)
                            BarGraphWizard("U.V FİNANSAL BORÇLAR", financialDebtsLongGraphBarDataList, periodList, financialDebtsLongList)
                            BarGraphWizard("K.V YÜKÜMLÜLÜKLER", shortTermLiabilitiesGraphBarDataList, periodList, shortTermLiabilitiesList)
                            BarGraphWizard("U.V YÜKÜMLÜLÜKLER", longTermLiabilitiesGraphBarDataList, periodList, longTermLiabilitiesList)
                        }

                        val companyValueAndEbitdaValues = balanceSheetWithRatios.ratios.map { it.companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault() }
                        val marketValueAndNetOperatingProfitValues = balanceSheetWithRatios.ratios.map { it.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault() }
                        val companyValueAndNetSalesValues = balanceSheetWithRatios.ratios.map { it.companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault() }
                        val netOperatingProfitAndMarketValues = balanceSheetWithRatios.ratios.map { it.netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault() }
                        val netDebtAndEquitiesValues = balanceSheetWithRatios.ratios.map { it.netDebtAndEquities.cleanedNumberFormat().toDoubleOrDefault() }
                        val netSalesGrowthRateValues = balanceSheetWithRatios.ratios.map { it.salesGrowthRate.cleanedNumberFormat().toDoubleOrDefault() }
                        val ebitdaGrowthRateValues = balanceSheetWithRatios.ratios.map { it.ebitdaGrowthRate.cleanedNumberFormat().toDoubleOrDefault() }
                        val netProfitGrowthRateValues = balanceSheetWithRatios.ratios.map { it.netProfitGrowthRate.cleanedNumberFormat().toDoubleOrDefault() }
                        val operatingProfitMarginValues = balanceSheetWithRatios.ratios.map { it.operatingProfitMargin.cleanedNumberFormat().toDoubleOrDefault() }
                        val equityProfitabilityValues = balanceSheetWithRatios.ratios.map { it.equityProfitability.cleanedNumberFormat().toDoubleOrDefault() }

                        val clipboardManager = LocalClipboardManager.current
                        var isProcessing by remember { mutableStateOf(false) }
                        var copyText by remember { mutableStateOf("") }

                        val coroutineScope = rememberCoroutineScope()

                        Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, end = 16.dp)) {
                            Button(
                                onClick = {
                                    isProcessing = true
                                    coroutineScope.launch {
                                        // Metni arka planda hazırla
                                        copyText = buildString {
                                            val ratioHeaders = listOf(
                                                LABEL_COMPANY_VALUE_AND_EBITDA,
                                                LABEL_MARKET_VALUE_AND_OPERATION_PROFIT,
                                                LABEL_COMPANY_VALUE_AND_NET_SALES,
                                                LABEL_NET_OPERATING_PROFIT_AND_MARKET_VALUE,
                                                "$LABEL_NET_DEBT_EQUITY_RATIO_VALUE ($EXPLANATION_NET_DEBT_EQUITY_RATIO_VALUE)",
                                                "$LABEL_NET_REVENUE_GROWTH_RATE_VALUE ($EXPLANATION_NET_REVENUE_GROWTH_RATE_VALUE)",
                                                "$LABEL_EBITDA_GROWTH_RATE_VALUE ($EXPLANATION_EBITDA_GROWTH_RATE_VALUE)",
                                                "$LABEL_NET_PROFIT_GROWTH_RATE_VALUE ($EXPLANATION_NET_PROFIT_GROWTH_RATE_VALUE)",
                                                "$LABEL_NET_OPERATING_MARGIN_VALUE ($EXPLANATION_NET_OPERATING_MARGIN_VALUE)",
                                                "$LABEL_RETURN_ON_EQUITY_VALUE ($EXPLANATION_RETURN_ON_EQUITY_VALUE)"
                                            )
                                            val ratioValues: List<(BalanceSheetRatioEntity) -> String> = listOf(
                                                { it.companyValueAndEbitda },
                                                { it.marketValueAndNetOperatingProfit },
                                                { it.companyValueAndNetSales },
                                                { it.netOperatingProfitAndMarketValue },
                                                { it.netDebtAndEquities },
                                                { it.salesGrowthRate },
                                                { it.ebitdaGrowthRate },
                                                { it.netProfitGrowthRate },
                                                { it.operatingProfitMargin },
                                                { it.equityProfitability }
                                            )


                                            append("#${code}\n\n")
                                            ratioHeaders.forEachIndexed { index, header ->
                                                append("$header: ")
                                                balanceSheetWithRatios.ratios.forEachIndexed { ratioIndex, ratio ->
                                                    val value = ratioValues[index](ratio)
                                                    append("${ratio.period}(${if (index == 3 || index > 4) "$value%" else value})")
                                                    append(if (ratioIndex == balanceSheetWithRatios.ratios.lastIndex) "\n\n" else ", ")
                                                }
                                            }
                                        }
                                        // Panoya kopyala
                                        clipboardManager.setText(AnnotatedString(copyText))
                                        isProcessing = false
                                    }
                                },
                                modifier = Modifier.align(Alignment.CenterEnd),
                                enabled = !isProcessing // Butonu işleme sırasında devre dışı bırak
                            ) {
                                Text(if (isProcessing) "Processing..." else "Copy Ratios")
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, end = 16.dp)
                                .horizontalScroll(rememberScrollState())
                                .drawWithContent {
                                    graphicsLayerTableRatios.record {
                                        this@drawWithContent.drawContent()
                                    }
                                    drawLayer(graphicsLayerTableRatios)
                                }
                        ) {
                            Column(
                                Modifier
                                    .width(75.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text("DÖNEM", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                balanceSheetWithRatios.ratios.forEach { balanceSheet ->
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
                                balanceSheetWithRatios.ratios.forEach { balanceSheet ->
                                    Text(balanceSheet.price, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                }
                            }
                            Column(
                                Modifier
                                    .width(105.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_COMPANY_VALUE_AND_EBITDA, companyValueAndEbitdaValues, isPercentageRate = false, isInverted = false)
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_MARKET_VALUE_AND_OPERATION_PROFIT, marketValueAndNetOperatingProfitValues, isPercentageRate = false, isInverted = false)
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_COMPANY_VALUE_AND_NET_SALES, companyValueAndNetSalesValues, isPercentageRate = false, isInverted = false)
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_NET_OPERATING_PROFIT_AND_MARKET_VALUE, netOperatingProfitAndMarketValues, isPercentageRate = true, isInverted = true)
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_NET_DEBT_EQUITY_RATIO_VALUE, netDebtAndEquitiesValues, isPercentageRate = false, isInverted = false, isReverse = true)
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_NET_REVENUE_GROWTH_RATE_VALUE, netSalesGrowthRateValues, isPercentageRate = true, isInverted = true)
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_EBITDA_GROWTH_RATE_VALUE, ebitdaGrowthRateValues, isPercentageRate = true, isInverted = true)
                            }
                            Column(
                                Modifier
                                    .width(95.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_NET_PROFIT_GROWTH_RATE_VALUE, netProfitGrowthRateValues, isPercentageRate = true, isInverted = true)
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_NET_OPERATING_MARGIN_VALUE, operatingProfitMarginValues, isPercentageRate = true, isInverted = true)
                            }
                            Column(
                                Modifier
                                    .width(90.dp)
                                    .padding(horizontal = 4.dp)
                            ) {
                                TableRatiosItemText(LABEL_RETURN_ON_EQUITY_VALUE, equityProfitabilityValues, isPercentageRate = true, isInverted = true)
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, end = 16.dp)
                        ) {
                            Text("$LABEL_MARKET_BOOK_AND_BOOK_VALUE: $EXPLANATION_MARKET_BOOK_AND_BOOK_VALUE")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_PRICE_AND_EARNING: $EXPLANATION_PRICE_AND_EARNING")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_COMPANY_VALUE_AND_EBITDA: $EXPLANATION_COMPANY_VALUE_AND_EBITDA")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_MARKET_VALUE_AND_OPERATION_PROFIT: $EXPLANATION_MARKET_VALUE_AND_OPERATION_PROFIT")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_COMPANY_VALUE_AND_NET_SALES: $EXPLANATION_COMPANY_VALUE_AND_NET_SALES")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_NET_OPERATING_PROFIT_AND_MARKET_VALUE: $EXPLANATION_NET_OPERATING_PROFIT_AND_MARKET_VALUE")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_NET_DEBT_EQUITY_RATIO_VALUE: $EXPLANATION_NET_DEBT_EQUITY_RATIO_VALUE")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_NET_REVENUE_GROWTH_RATE_VALUE: $EXPLANATION_NET_REVENUE_GROWTH_RATE_VALUE")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_EBITDA_GROWTH_RATE_VALUE: $EXPLANATION_EBITDA_GROWTH_RATE_VALUE")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_NET_PROFIT_GROWTH_RATE_VALUE: $EXPLANATION_NET_PROFIT_GROWTH_RATE_VALUE")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_NET_OPERATING_MARGIN_VALUE: $EXPLANATION_NET_OPERATING_MARGIN_VALUE")
                            Spacer(Modifier.height(16.dp))
                            Text("$LABEL_RETURN_ON_EQUITY_VALUE: $EXPLANATION_RETURN_ON_EQUITY_VALUE")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableRatiosItemText(label: String, values: List<Double>, isPercentageRate: Boolean = false, isInverted: Boolean = false, isReverse: Boolean = false) {
    Text(
        label,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    values.forEach { valueItem ->
        val backgroundColor = getBackgroundColor(valueItem, values, isInverted, isReverse)
        val value = if (isPercentageRate) "$valueItem".decimalNumberFormat() + "%" else "$valueItem".decimalNumberFormat()
        Text(
            value,
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
        )
    }
}

@Composable
fun getBackgroundColor(value: Double, values: List<Double>, isInverted: Boolean = false, isReverse: Boolean = false): Color {

    if (value < 0 && isReverse) {
        return Color(0xFF00A300)
    }

    if (value < 0 && !isInverted) {
        return Color(0xFFA30000)
    }

    val colors = listOf(
        Color(0xFF00A300), // En düşük değer (Yeşil)
        Color(0xFF1AAC1A),
        Color(0xFF33B533),
        Color(0xFF4DBE4D),
        Color(0xFF66C766),
        Color(0xFF80D080),
        Color(0xFF99D999),
        Color(0xFFB3E2B3),
        Color(0xFFCCEBCC),
        Color(0xFFEBCCCC),
        Color(0xFFE2B3B3),
        Color(0xFFD99999),
        Color(0xFFD08080),
        Color(0xFFC76666),
        Color(0xFFBE4D4D),
        Color(0xFFB53333),
        Color(0xFFAC1A1A),
        Color(0xFFA30000) // En yüksek değer (Kırmızı)
    )

    val sortedValues = values.sorted()
    val position = sortedValues.indexOf(value)

    val colorIndex = (position.toFloat() / (values.size - 1) * (colors.size - 1)).toInt()
    val selectedColor = colors[colorIndex]

    return if (isInverted) colors.reversed()[colorIndex] else selectedColor
}
