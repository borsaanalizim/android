package com.yavuzmobile.borsaanalizim.ui.comparestocks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.yavuzmobile.borsaanalizim.data.model.IndexResponse
import com.yavuzmobile.borsaanalizim.data.model.SectorResponse
import com.yavuzmobile.borsaanalizim.util.Constant
import com.yavuzmobile.borsaanalizim.util.DateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareStocksFilterBottomSheet(
    viewModel: CompareStocksViewModel,
    indexes: List<IndexResponse>,
    sectors: SectorResponse,
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            var expandedPeriod by remember { mutableStateOf(false) }
            var expandedIndex by remember { mutableStateOf(false) }
            var expandedSector by remember { mutableStateOf(false) }

            var selectedPeriod by remember { mutableStateOf(viewModel.selectedPeriodFilterState.value) }
            var selectedIndex by remember { mutableStateOf(viewModel.selectedIndexStockFilterState.value) }
            var selectedSector by remember { mutableStateOf(viewModel.selectedSectorStockFilterState.value) }

            // Period
            ExposedDropdownMenuBox(
                expanded = expandedPeriod,
                onExpandedChange = { expandedPeriod = !expandedPeriod }
            ) {
                TextField(
                    value = "${selectedPeriod.year}/${selectedPeriod.month}",
                    onValueChange = {},
                    label = { Text("Period Seçin") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIndex) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = expandedPeriod,
                    onDismissRequest = { expandedPeriod = false },
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
                ) {
                    DateUtil.getLastTwelvePeriods().forEach { period ->
                        DropdownMenuItem(
                            text = { Text("${period.year}/${period.month}") },
                            onClick = {
                                selectedPeriod = period
                                expandedPeriod = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        HorizontalDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(16.dp))
            // Index
            ExposedDropdownMenuBox(
                expanded = expandedIndex,
                onExpandedChange = { expandedIndex = !expandedIndex }
            ) {
                TextField(
                    value = selectedIndex,
                    onValueChange = {},
                    label = { Text("Endeks Seçin") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedIndex) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = expandedIndex,
                    onDismissRequest = { expandedIndex = false },
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(Constant.ALL) },
                        onClick = {
                            selectedIndex = Constant.ALL
                            expandedIndex = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                    HorizontalDivider()
                    indexes.forEach { index ->
                        DropdownMenuItem(
                            text = { Text("${index.name}") },
                            onClick = {
                                selectedIndex = "${index.name}"
                                expandedIndex = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                        HorizontalDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(16.dp))

            // Sector
            ExposedDropdownMenuBox(
                expanded = expandedSector,
                onExpandedChange = { expandedSector = !expandedSector }
            ) {
                TextField(
                    value = selectedSector,
                    onValueChange = {},
                    label = { Text("Sektör Seçin") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSector) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )

                ExposedDropdownMenu(
                    expanded = expandedSector,
                    onDismissRequest = { expandedSector = false },
                    modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(Constant.ALL) },
                        onClick = {
                            selectedSector = Constant.ALL
                            expandedSector = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                    sectors.sectors?.forEach { sector ->
                        DropdownMenuItem(
                            text = { Text(sector) },
                            onClick = {
                                selectedSector = sector
                                expandedSector = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                        HorizontalDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.padding(16.dp))

            ElevatedButton(
                onClick = {
                    viewModel.onClickCompareFilterButton(selectedPeriod, selectedIndex, selectedSector)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors()
            ) {
                Text("Filtrele")
            }
        }

    }
}