package com.yavuzmobile.borsaanalizim.ui.stock

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockFilterBottomSheetScreen(
    viewModel: StocksViewModel,
    indexes: List<IndexResponse>,
    sectors: List<SectorResponse>,
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
            var expandedIndex by remember { mutableStateOf(false) }
            var expandedSector by remember { mutableStateOf(false) }

            var selectedIndex by remember { mutableStateOf(viewModel.selectedIndexStockFilterState.value) }
            var selectedSector by remember { mutableStateOf(viewModel.selectedSectorStockFilterState.value) }

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
                    sectors.forEach { sector ->
                        DropdownMenuItem(
                            text = { Text("${sector.mainCategory}") },
                            onClick = {
                                selectedSector = "${sector.mainCategory}"
                                expandedSector = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            modifier = Modifier.background(Color.LightGray)
                        )
                        HorizontalDivider()
                        sector.subCategories?.forEach { subCategory ->
                            DropdownMenuItem(
                                text = { Text(subCategory) },
                                onClick = {
                                    selectedSector = subCategory
                                    expandedSector = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(16.dp))

            ElevatedButton(
                onClick = {
                    viewModel.onClickFilterButton(selectedIndex, selectedSector)
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