package com.yavuzmobile.borsaanalizim.ui.comparestocksdetail

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yavuzmobile.borsaanalizim.data.local.entity.BalanceSheetRatioEntity
import com.yavuzmobile.borsaanalizim.enums.ActionButtons
import com.yavuzmobile.borsaanalizim.ext.cleanedNumberFormat
import com.yavuzmobile.borsaanalizim.ext.findActivity
import com.yavuzmobile.borsaanalizim.ext.orDefault
import com.yavuzmobile.borsaanalizim.ext.toDoubleOrDefault
import com.yavuzmobile.borsaanalizim.model.SortByBalanceSheetRatios
import com.yavuzmobile.borsaanalizim.model.StockFilter
import com.yavuzmobile.borsaanalizim.ui.BaseScreen
import com.yavuzmobile.borsaanalizim.ui.comparestocks.CompareStocksViewModel
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_COMPANY_VALUE_AND_EBITDA
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_COMPANY_VALUE_AND_NET_SALES
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_EBITDA_GROWTH_RATE_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_MARKET_BOOK_AND_BOOK_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_MARKET_VALUE_AND_OPERATION_PROFIT
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_NET_DEBT_EQUITY_RATIO_VALUE
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
fun CompareStocksDetailScreen(
    navController: NavController,
    viewModel: CompareStocksViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context.findActivity()

    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

    val stocks by viewModel.selectedStocksUiState.collectAsState()

    val selectedSort = remember { mutableStateOf(SortByBalanceSheetRatios.STOCK_CODE) }

    val sortedList = remember(selectedSort.value) {
        if (selectedSort.value == SortByBalanceSheetRatios.NET_OPERATING_PROFIT_AND_MARKET_VALUE) {
            stocks.sortedByDescending { it.balanceSheetRatios.netOperatingProfitAndMarketValue }
        } else {
            stocks.sortedBy {
                when(selectedSort.value) {
                    SortByBalanceSheetRatios.STOCK_CODE -> it.balanceSheetRatios.stockCode
                    SortByBalanceSheetRatios.MARKET_BOOK_AND_BOOK_VALUE -> it.balanceSheetRatios.marketBookAndBookValue
                    SortByBalanceSheetRatios.PRICE_AND_EARNING -> it.balanceSheetRatios.priceAndEarning
                    SortByBalanceSheetRatios.COMPANY_VALUE_AND_EBITDA -> it.balanceSheetRatios.companyValueAndEbitda
                    SortByBalanceSheetRatios.MARKET_VALUE_AND_NET_OPERATING_PROFIT -> it.balanceSheetRatios.marketValueAndNetOperatingProfit
                    SortByBalanceSheetRatios.COMPANY_VALUE_AND_NET_SALES -> it.balanceSheetRatios.companyValueAndNetSales
                    SortByBalanceSheetRatios.NET_OPERATING_PROFIT_AND_MARKET_VALUE -> it.balanceSheetRatios.netOperatingProfitAndMarketValue
                    SortByBalanceSheetRatios.NET_DEBT_EQUITY_RATIO_VALUE -> it.balanceSheetRatios.netOperatingProfitAndMarketValue
                    SortByBalanceSheetRatios.NET_REVENUE_GROWTH_RATE_VALUE -> it.balanceSheetRatios.netOperatingProfitAndMarketValue
                    SortByBalanceSheetRatios.EBITDA_GROWTH_RATE_VALUE -> it.balanceSheetRatios.netOperatingProfitAndMarketValue
                    SortByBalanceSheetRatios.NET_PROFIT_GROWTH_RATE_VALUE -> it.balanceSheetRatios.netOperatingProfitAndMarketValue
                    SortByBalanceSheetRatios.NET_OPERATING_MARGIN_VALUE -> it.balanceSheetRatios.netOperatingProfitAndMarketValue
                    SortByBalanceSheetRatios.RETURN_ON_EQUITY_VALUE -> it.balanceSheetRatios.netOperatingProfitAndMarketValue
                }
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val graphicsLayerTableRatios = rememberGraphicsLayer()

    BaseScreen(navController, Modifier.fillMaxSize(), "Karşılaştırma", ActionButtons.SHARE, {
        var fileName = ""
        if (stocks.size > 5) {
            fileName = stocks
                .mapNotNull { map -> map.stock.sector }
                .toSet()
                .joinToString(separator = "-")
        } else {
            stocks.forEachIndexed { index, stockFilter ->
                fileName += if (index == 0) stockFilter.stock.code else "-${stockFilter.stock.code}"
            }
        }

        coroutineScope.launch {
            val bitmap = graphicsLayerTableRatios.toImageBitmap()
            val resultFileName = if (fileName.contains("/")) "karsilastirma" else fileName
            ShareUtil.shareBitmap(context, bitmap.asAndroidBitmap(), resultFileName)
        }
    }) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            TableRatios(
                modifier = Modifier.drawWithContent {
                    graphicsLayerTableRatios.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayerTableRatios)
                },
                stocks = stocks,
                sortedList = sortedList, selectedSort.value
            ) { selected ->
                selectedSort.value = selected
            }

            Spacer(Modifier.height(16.dp))

            DetailTableRatios(
                modifier = Modifier,
                stocks = stocks
            )

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
            }
        }
    }

}

@Composable
fun TableRatios(modifier: Modifier, stocks: List<StockFilter>, sortedList: List<StockFilter>, selectedSort: SortByBalanceSheetRatios, onClickSort: (selectedSort: SortByBalanceSheetRatios) -> Unit) {

    val marketBookAndBookValues = stocks.map { it.balanceSheetRatios.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault() }
    val priceAndEarningValues = stocks.map { it.balanceSheetRatios.priceAndEarning.cleanedNumberFormat().toDoubleOrDefault() }
    val companyValueAndEbitdaValues = stocks.map { it.balanceSheetRatios.companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault() }
    val marketValueAndNetOperatingProfitValues = stocks.map { it.balanceSheetRatios.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault() }
    val companyValueAndNetSalesValues = stocks.map { it.balanceSheetRatios.companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault() }
    val netOperatingProfitAndMarketValues = stocks.map { it.balanceSheetRatios.netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault() }
    val netDebtAndEquitiesValues = stocks.map { it.balanceSheetRatios.netDebtAndEquities.cleanedNumberFormat().toDoubleOrDefault() }
    val netSalesGrowthRateValues = stocks.map { it.balanceSheetRatios.salesGrowthRate.cleanedNumberFormat().toDoubleOrDefault() }
    val ebitdaGrowthRateValues = stocks.map { it.balanceSheetRatios.ebitdaGrowthRate.cleanedNumberFormat().toDoubleOrDefault() }
    val netProfitGrowthRateValues = stocks.map { it.balanceSheetRatios.netProfitGrowthRate.cleanedNumberFormat().toDoubleOrDefault() }
    val operatingProfitMarginValues = stocks.map { it.balanceSheetRatios.operatingProfitMargin.cleanedNumberFormat().toDoubleOrDefault() }
    val equityProfitabilityValues = stocks.map { it.balanceSheetRatios.equityProfitability.cleanedNumberFormat().toDoubleOrDefault() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 8.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .width(100.dp)
                .padding(horizontal = 4.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text("FİRMA", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.STOCK_CODE) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.STOCK_CODE) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stock ->
                Text(stock.balanceSheetRatios.stockCode, modifier = Modifier.fillMaxWidth())
            }
        }

        // Ratios
        Column(
            Modifier
                .width(100.dp)
                .padding(horizontal = 4.dp)
        ) {
            Row {
                Text(LABEL_MARKET_BOOK_AND_BOOK_VALUE, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.MARKET_BOOK_AND_BOOK_VALUE) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.MARKET_BOOK_AND_BOOK_VALUE) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val marketBookAndBookValue = stockFilter.balanceSheetRatios.marketBookAndBookValue
                if (marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault(),
                    marketBookAndBookValues
                )
                Text(
                    marketBookAndBookValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF333333)
                )
            }
        }
        Column(
            Modifier
                .width(90.dp)
                .padding(horizontal = 4.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(LABEL_PRICE_AND_EARNING, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.PRICE_AND_EARNING) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if(selectedSort == SortByBalanceSheetRatios.PRICE_AND_EARNING) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val priceAndEarning = stockFilter.balanceSheetRatios.priceAndEarning
                if (priceAndEarning.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    priceAndEarning.cleanedNumberFormat().toDoubleOrDefault(),
                    priceAndEarningValues
                )
                Text(
                    if (priceAndEarning.cleanedNumberFormat().toDoubleOrDefault() < 0) "-"
                    else priceAndEarning, modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                )
            }
        }
        Column(
            Modifier
                .width(130.dp)
                .padding(horizontal = 4.dp)
        ) {
            Row {
                Text(LABEL_COMPANY_VALUE_AND_EBITDA, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.COMPANY_VALUE_AND_EBITDA) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.COMPANY_VALUE_AND_EBITDA) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val companyValueAndEbitda = stockFilter.balanceSheetRatios.companyValueAndEbitda
                if (companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault(),
                    companyValueAndEbitdaValues
                )
                Text(
                    if (companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault() < 0) "-"
                    else companyValueAndEbitda, modifier = Modifier
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
            Row {
                Text(LABEL_MARKET_VALUE_AND_OPERATION_PROFIT, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.MARKET_VALUE_AND_NET_OPERATING_PROFIT) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.MARKET_VALUE_AND_NET_OPERATING_PROFIT) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val marketValueAndNetOperatingProfit = stockFilter.balanceSheetRatios.marketValueAndNetOperatingProfit
                if (marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault(),
                    marketValueAndNetOperatingProfitValues
                )
                Text(
                    if (marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault() < 0) "-"
                    else marketValueAndNetOperatingProfit, modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                )
            }
        }
        Column(
            Modifier
                .width(100.dp)
                .padding(horizontal = 4.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(LABEL_COMPANY_VALUE_AND_NET_SALES, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.COMPANY_VALUE_AND_NET_SALES) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.COMPANY_VALUE_AND_NET_SALES) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val companyValueAndNetSales = stockFilter.balanceSheetRatios.companyValueAndNetSales
                if (companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault(),
                    companyValueAndNetSalesValues
                )
                Text(
                    if (companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault() < 0) "-"
                    else companyValueAndNetSales, modifier = Modifier
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
            Row(Modifier.fillMaxWidth()) {
                Text(LABEL_NET_OPERATING_PROFIT_AND_MARKET_VALUE, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.NET_OPERATING_PROFIT_AND_MARKET_VALUE) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.NET_OPERATING_PROFIT_AND_MARKET_VALUE) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val netOperatingProfitAndMarketValue = stockFilter.balanceSheetRatios.netOperatingProfitAndMarketValue
                if (netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault(),
                    netOperatingProfitAndMarketValues,
                    true
                )
                Text(
                    "$netOperatingProfitAndMarketValue%", modifier = Modifier
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
            Row(Modifier.fillMaxWidth()) {
                Text(LABEL_NET_DEBT_EQUITY_RATIO_VALUE, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.NET_DEBT_EQUITY_RATIO_VALUE) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.NET_DEBT_EQUITY_RATIO_VALUE) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val netDebtAndEquitiesValue = stockFilter.balanceSheetRatios.netDebtAndEquities
                if (netDebtAndEquitiesValue.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    netDebtAndEquitiesValue.cleanedNumberFormat().toDoubleOrDefault(),
                    netDebtAndEquitiesValues,
                    true,
                    isReverse = true
                )
                Text(
                    netDebtAndEquitiesValue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor),
                    textAlign = TextAlign.Center, color = Color(0xFF333333)
                )
            }
        }
        Column(
            Modifier
                .width(105.dp)
                .padding(horizontal = 4.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(LABEL_NET_REVENUE_GROWTH_RATE_VALUE, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.NET_REVENUE_GROWTH_RATE_VALUE) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.NET_REVENUE_GROWTH_RATE_VALUE) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val salesGrowthRate = stockFilter.balanceSheetRatios.salesGrowthRate
                if (salesGrowthRate.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    salesGrowthRate.cleanedNumberFormat().toDoubleOrDefault(),
                    netSalesGrowthRateValues,
                    true
                )
                Text(
                    "$salesGrowthRate%", modifier = Modifier
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
            Row(Modifier.fillMaxWidth()) {
                Text(LABEL_EBITDA_GROWTH_RATE_VALUE, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.EBITDA_GROWTH_RATE_VALUE) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.EBITDA_GROWTH_RATE_VALUE) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val ebitdaGrowthRate = stockFilter.balanceSheetRatios.ebitdaGrowthRate
                if (ebitdaGrowthRate.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    ebitdaGrowthRate.cleanedNumberFormat().toDoubleOrDefault(),
                    ebitdaGrowthRateValues,
                    true
                )
                Text(
                    "$ebitdaGrowthRate%", modifier = Modifier
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
            Row(Modifier.fillMaxWidth()) {
                Text(LABEL_NET_PROFIT_GROWTH_RATE_VALUE, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.NET_PROFIT_GROWTH_RATE_VALUE) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.NET_PROFIT_GROWTH_RATE_VALUE) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val netProfitGrowthRate = stockFilter.balanceSheetRatios.netProfitGrowthRate
                if (netProfitGrowthRate.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    netProfitGrowthRate.cleanedNumberFormat().toDoubleOrDefault(),
                    netProfitGrowthRateValues,
                    true
                )
                Text(
                    "$netProfitGrowthRate%", modifier = Modifier
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
            Row(Modifier.fillMaxWidth()) {
                Text(LABEL_NET_OPERATING_MARGIN_VALUE, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.NET_OPERATING_MARGIN_VALUE) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.NET_OPERATING_MARGIN_VALUE) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val operatingProfitMargin = stockFilter.balanceSheetRatios.operatingProfitMargin
                if (operatingProfitMargin.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    operatingProfitMargin.cleanedNumberFormat().toDoubleOrDefault(),
                    operatingProfitMarginValues,
                    true
                )
                Text(
                    "$operatingProfitMargin%", modifier = Modifier
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
            Row(Modifier.fillMaxWidth()) {
                Text(LABEL_RETURN_ON_EQUITY_VALUE, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                IconButton(onClick = { onClickSort(SortByBalanceSheetRatios.RETURN_ON_EQUITY_VALUE) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = if (selectedSort == SortByBalanceSheetRatios.RETURN_ON_EQUITY_VALUE) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp, contentDescription = "")
                }
            }
            sortedList.forEach { stockFilter ->
                val equityProfitability = stockFilter.balanceSheetRatios.equityProfitability
                if (equityProfitability.cleanedNumberFormat().toDoubleOrDefault().isNaN()) {
                    return@forEach
                }
                val backgroundColor = getBackgroundColor(
                    equityProfitability.cleanedNumberFormat().toDoubleOrDefault(),
                    equityProfitabilityValues,
                    true
                )
                Text(
                    "$equityProfitability%", modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                )
            }
        }
    }
}

@Composable
fun DetailTableRatios(modifier: Modifier, stocks: List<StockFilter>) {
    val clipboardManager = LocalClipboardManager.current
    var isProcessing by remember { mutableStateOf(false) }
    var copyText by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxWidth().padding(top = 16.dp, end = 16.dp)) {
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
                            "$LABEL_NET_OPERATING_MARGIN_VALUE ($EXPLANATION_NET_OPERATING_PROFIT_AND_MARKET_VALUE)",
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

                        ratioHeaders.forEachIndexed { index, header ->
                            append("$header: ")
                            stocks.forEachIndexed { stockIndex, stock ->
                                val value = ratioValues[index](stock.balanceSheetRatios)
                                append("${stock.balanceSheetRatios.stockCode}(${if (index == 3 || index > 4) "$value%" else value})")
                                append(if (stockIndex == stocks.lastIndex) "\n\n" else ", ")
                            }
                        }
                        stocks.forEach { stock ->
                            append("#${stock.balanceSheetRatios.stockCode} ")
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
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 8.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .width(90.dp)
                .padding(horizontal = 4.dp)
        ) {
            Text("FİRMA", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            stocks.forEach { stock ->
                Text(stock.balanceSheetRatios.stockCode, modifier = Modifier.fillMaxWidth())
            }
        }
        Column(
            Modifier
                .width(90.dp)
                .padding(horizontal = 4.dp)
        ) {
            Text("SEKTÖR", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            stocks.forEach { stock ->
                Text(
                    text = "${stock.stock.sector}", modifier = Modifier.fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
        Column(
            Modifier
                .width(90.dp)
                .padding(horizontal = 4.dp)
        ) {
            Text("Bilanço F.", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            stocks.forEach { stock ->
                Text(
                    stock.balanceSheetRatios.price,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(
            Modifier
                .width(90.dp)
                .padding(horizontal = 4.dp)
        ) {
            Text("Güncel F.", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            stocks.forEach { stock ->
                Text(
                    "${stock.balanceSheetDate.stock.lastPrice}",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(
            Modifier
                .width(105.dp)
                .padding(horizontal = 4.dp)
        ) {
            Text("Gerç. Marj", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            stocks.forEach { stock ->
                Text(
                    "${String.format(Locale.getDefault(), "%.2f", ((stock.balanceSheetDate.stock.lastPrice.orDefault()) - (stock.balanceSheetRatios.price.cleanedNumberFormat().toDoubleOrDefault())) / (stock.balanceSheetRatios.price.cleanedNumberFormat().toDoubleOrDefault()) * 100)}%",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun getBackgroundColor(value: Double, values: List<Double>, isInverted: Boolean = false, isReverse: Boolean = false): Color {
    if (value < 0 && isReverse) {
        return Color(0xFF00A300)
    }
    if (value < 0 && !isInverted) {
        return Color(0xFFF4E6E6) // (Bej)
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