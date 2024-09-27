package com.yavuzmobile.borsaanalizim.ui.comparestocksdetail

import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_MARKET_BOOK_AND_BOOK_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_MARKET_VALUE_AND_OPERATION_PROFIT
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_NET_OPERATING_PROFIT_AND_MARKET_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.EXPLANATION_PRICE_AND_EARNING
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_COMPANY_VALUE_AND_EBITDA
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_COMPANY_VALUE_AND_NET_SALES
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_MARKET_BOOK_AND_BOOK_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_MARKET_VALUE_AND_OPERATION_PROFIT
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_NET_OPERATING_PROFIT_AND_MARKET_VALUE
import com.yavuzmobile.borsaanalizim.util.RatiosConstant.LABEL_PRICE_AND_EARNING
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
                }
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val graphicsLayerTableRatios = rememberGraphicsLayer()

    BaseScreen(navController, Modifier.fillMaxSize(), "Karşılaştırma", ActionButtons.SHARE, {
        var fileName = ""
        stocks.forEachIndexed { index, stockFilter -> fileName += if (index == 0) stockFilter.stock.code else "-${stockFilter.stock.code}" }
        coroutineScope.launch {
            val bitmap = graphicsLayerTableRatios.toImageBitmap()
            ShareUtil.shareBitmap(context, bitmap.asAndroidBitmap(), fileName)
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

    val marketBookAndBookValueMin = stocks.minOfOrNull { it.balanceSheetRatios.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
    val marketBookAndBookValueMax = stocks.maxOfOrNull { it.balanceSheetRatios.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

    val priceAndEarningMin = stocks.minOfOrNull { it.balanceSheetRatios.priceAndEarning.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
    val priceAndEarningMax = stocks.maxOfOrNull { it.balanceSheetRatios.priceAndEarning.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

    val companyValueAndEbitdaMin = stocks.minOfOrNull { it.balanceSheetRatios.companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
    val companyValueAndEbitdaMax = stocks.maxOfOrNull { it.balanceSheetRatios.companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

    val marketValueAndNetOperatingProfitMin = stocks.minOfOrNull { it.balanceSheetRatios.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
    val marketValueAndNetOperatingProfitMax = stocks.maxOfOrNull { it.balanceSheetRatios.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

    val companyValueAndNetSalesMin = stocks.minOfOrNull { it.balanceSheetRatios.companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
    val companyValueAndNetSalesMax = stocks.maxOfOrNull { it.balanceSheetRatios.companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

    val netOperatingProfitAndMarketValueMin = stocks.minOfOrNull { it.balanceSheetRatios.netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault() } ?: 0.0
    val netOperatingProfitAndMarketValueMax = stocks.maxOfOrNull { it.balanceSheetRatios.netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault() } ?: 1.0

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
                    marketBookAndBookValueMin,
                    marketBookAndBookValueMax
                )
                Text(
                    marketBookAndBookValue, modifier = Modifier
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
                    priceAndEarningMin,
                    priceAndEarningMax
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
                    companyValueAndEbitdaMin,
                    companyValueAndEbitdaMax
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
                    marketValueAndNetOperatingProfitMin,
                    marketValueAndNetOperatingProfitMax
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
                    companyValueAndNetSalesMin,
                    companyValueAndNetSalesMax
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
                    netOperatingProfitAndMarketValueMin,
                    netOperatingProfitAndMarketValueMax,
                    true
                )
                Text(
                    "$netOperatingProfitAndMarketValue%", modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor), textAlign = TextAlign.Center, color = Color(0xFF333333)
                )
            }
        }
    }
}

@Composable
fun DetailTableRatios(modifier: Modifier, stocks: List<StockFilter>) {
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
                    text = "${stock.stock.sectors?.first()}", modifier = Modifier.fillMaxWidth(),
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