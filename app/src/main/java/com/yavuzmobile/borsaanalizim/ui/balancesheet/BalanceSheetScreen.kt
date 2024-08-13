package com.yavuzmobile.borsaanalizim.ui.balancesheet

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BalanceSheetScreen(
    navController: NavController,
    code: String,
    viewModel: BalanceSheetViewModel = hiltViewModel()
) {

    val balanceSheetUiState by viewModel.balanceSheetUiState.collectAsState()

    LaunchedEffect(code) {
        viewModel.fetchData(code)
    }

    BaseScreen(navController = navController, title = code) {
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

                    Text("FETCH DATA", Modifier.clickable {
                        viewModel.fetchBalanceSheetJson(code)
                    })

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .horizontalScroll(rememberScrollState()) // Yatay kaydırma
                    ) {
                        Column(
                            Modifier
                                .width(75.dp)
                                .padding(horizontal = 4.dp)) {
                            Text("DÖNEM", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            balanceSheets.forEach { balanceSheet ->
                                Text(balanceSheet.period, modifier = Modifier.fillMaxWidth())
                            }
                        }
                        Column(
                            Modifier
                                .width(75.dp)
                                .padding(horizontal = 4.dp)) {
                            Text("Fiyat", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            Text(String.format(Locale.getDefault(), "%.2f", viewModel.priceDateHistoryState.value.data?.data?.lastOrNull()?.price.toString().toDoubleOrDefault()), Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            viewModel.periodPrice.forEach {  periodPerice ->
                                Text(String.format(Locale.getDefault(), "%.2f", periodPerice.value.toString().toDoubleOrDefault()), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        }
                        Column(
                            Modifier
                                .width(75.dp)
                                .padding(horizontal = 4.dp)) {
                            Text("PD / DD", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            balanceSheets.forEach { balanceSheet ->
                                val backgroundColor = getBackgroundColor(balanceSheet.marketBookAndBookValue.cleanedNumberFormat().toDoubleOrDefault(), marketBookAndBookValueMin, marketBookAndBookValueMax)
                                Text(balanceSheet.marketBookAndBookValue, modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor), textAlign = TextAlign.Center)
                            }
                        }
                        Column(
                            Modifier
                                .width(75.dp)
                                .padding(horizontal = 4.dp)) {
                            Text("F / K", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            balanceSheets.forEach { balanceSheet ->
                                val backgroundColor = getBackgroundColor(balanceSheet.priceAndEarning.cleanedNumberFormat().toDoubleOrDefault(), priceAndEarningMin, priceAndEarningMax)
                                Text(balanceSheet.priceAndEarning, modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor), textAlign = TextAlign.Center)
                            }
                        }
                        Column(
                            Modifier
                                .width(105.dp)
                                .padding(horizontal = 4.dp)) {
                            Text("FD / FAVÖK", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            balanceSheets.forEach { balanceSheet ->
                                val backgroundColor = getBackgroundColor(balanceSheet.companyValueAndEbitda.cleanedNumberFormat().toDoubleOrDefault(), companyValueAndEbitdaMin, companyValueAndEbitdaMax)
                                Text(balanceSheet.companyValueAndEbitda, modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor), textAlign = TextAlign.Center)
                            }
                        }
                        Column(
                            Modifier
                                .width(90.dp)
                                .padding(horizontal = 4.dp)) {
                            Text("PD / NFK", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            balanceSheets.forEach { balanceSheet ->
                                val backgroundColor = getBackgroundColor(balanceSheet.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault(), marketValueAndNetOperatingProfitMin, marketValueAndNetOperatingProfitMax)
                                Text(if (balanceSheet.marketValueAndNetOperatingProfit.cleanedNumberFormat().toDoubleOrDefault() < 0) "-" else balanceSheet.marketValueAndNetOperatingProfit, modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor), textAlign = TextAlign.Center)
                            }
                        }
                        Column(
                            Modifier
                                .width(75.dp)
                                .padding(horizontal = 4.dp)) {
                            Text("FD / NS", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            balanceSheets.forEach { balanceSheet ->
                                val backgroundColor = getBackgroundColor(balanceSheet.companyValueAndNetSales.cleanedNumberFormat().toDoubleOrDefault(), companyValueAndNetSalesMin, companyValueAndNetSalesMax)
                                Text(balanceSheet.companyValueAndNetSales, modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor), textAlign = TextAlign.Center)
                            }
                        }
                        Column(
                            Modifier
                                .width(90.dp)
                                .padding(horizontal = 4.dp)) {
                            Text("NFK / PD", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            balanceSheets.forEach { balanceSheet ->
                                val backgroundColor = getBackgroundColor(balanceSheet.netOperatingProfitAndMarketValue.cleanedNumberFormat().toDoubleOrDefault(), netOperatingProfitAndMarketValueMin, netOperatingProfitAndMarketValueMax, true)
                                Text(balanceSheet.netOperatingProfitAndMarketValue + "%", modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor), textAlign = TextAlign.Center)
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
    val minTwo = if (min < 0) mid - mid / 5 else mid - (mid + min) / 5
    val minThree = if (min < 0) mid - mid / 5 * 2 else mid - (mid + min) / 5 * 2
    val minFour = if (min < 0) mid - mid / 5 * 3 else mid - (mid + min) / 5 * 3
    val minFive = if (min < 0) mid / 5 else min + (mid + min) / 5
    val maxTwo = max - (max - mid) / 5
    val maxThree = max - (max - mid) / 5 * 2
    val maxFour = max - (max - mid) / 5 * 3
    val maxFive = mid - (max - mid) / 5 * 4

    // 00DB00 - 0, 219, 0 - 70%, 0%, 100%, 0%
    // 78FC78 - 120, 252, 120 - 46%, 0%, 78%, 0%
    // C3F9C3 - 195, 249, 195 - 22%, 0%, 32%, 0%
    // FACEA6 - 250, 206, 166 - 2%, 20%, 35%, 0% - mid
    // FA9B9B - 250, 155, 155 - 1%, 48%, 27%, 0%
    // FA6868 - 250, 1, 104 - 0%, 99%, 35%, 0%
    // FF0000 - 255, 0, 0 - 0%, 99%, 100%, 0%

    return when {
        value < 0 -> Color(0xFFFF0000)
        value == min -> if (isInverted) Color(0xFFFF0000) else  Color(0xFF00DB00)
        value == max -> if (isInverted) Color(0xFF00DB00) else Color(0xFFFF0000)
        value > min && value <= minFive -> if (isInverted) Color(0xFFFF0000) else Color(0xFF00DB00)
        value > minFive && value <= minFour -> if (isInverted) Color(0xFFFA6868) else Color(0xFF78FC78)
        value > minFour && value <= minThree -> if (isInverted) Color(0xFFFA9B9B) else Color(0xFFC3F9C3)
        (value < maxFour && value > maxFive) || (value > minThree && value <= minTwo) -> Color(0xFFFACEA6)
        value < max && value >= maxTwo -> if (isInverted) Color(0xFF00DB00) else Color(0xFFFF0000)
        value < maxTwo && value >= maxThree -> if (isInverted) Color(0xFF78FC78) else Color(0xFFFA6868)
        value < maxThree && value >= maxFour -> if (isInverted) Color(0xFFC3F9C3) else Color(0xFFFA9B9B)
        else -> if (isInverted) Color(0xFFFF0000) else Color(0xFF00DB00)
    }
}
