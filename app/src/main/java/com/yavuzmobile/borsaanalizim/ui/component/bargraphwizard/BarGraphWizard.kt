package com.yavuzmobile.borsaanalizim.ui.component.bargraphwizard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yavuzmobile.borsaanalizim.ui.component.BarGraph
import com.yavuzmobile.borsaanalizim.ui.component.BarType
import com.yavuzmobile.borsaanalizim.ui.theme.Purple80
import com.yavuzmobile.borsaanalizim.util.ShareUtil
import kotlinx.coroutines.launch

@Composable
fun BarGraphWizard(title: String, graphBarData: List<Float>, xAxisScaleData: List<String>, barData: List<Double>) {
    if (graphBarData.first().isNaN()) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayerTable = rememberGraphicsLayer()

    Column(
        Modifier
            .fillMaxWidth()
            .drawWithContent {
                graphicsLayerTable.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayerTable)
            }
            .clickable {
                coroutineScope.launch {
                    val bitmap = graphicsLayerTable.toImageBitmap()
                    ShareUtil.shareBitmap(context, bitmap.asAndroidBitmap(), title)
                }
            }) {
        Text(title,
            Modifier
                .padding(8.dp)
                .fillMaxWidth(), textAlign = TextAlign.Center)
        BarGraph(
            graphBarData = graphBarData,
            xAxisScaleData = xAxisScaleData,
            barData_ = barData,
            height = 300.dp,
            roundType = BarType.TOP_CURVED,
            barWidth = 60.dp,
            barColor = Purple80,
            barArrangement = Arrangement.SpaceEvenly
        )
    }
}