package com.yavuzmobile.borsaanalizim.ui.favorite

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yavuzmobile.borsaanalizim.ui.BaseHomeScreen

@Composable
fun FavoriteScreen(navController: NavController, viewModel: FavoriteViewModel = hiltViewModel()) {

    BaseHomeScreen(navController = navController) {

    }
}