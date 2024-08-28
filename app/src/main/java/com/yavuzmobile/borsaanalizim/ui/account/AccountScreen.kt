package com.yavuzmobile.borsaanalizim.ui.account

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yavuzmobile.borsaanalizim.ui.BaseHomeScreen

@Composable
fun AccountScreen(navController: NavController, viewModel: AccountViewModel = hiltViewModel()) {

    BaseHomeScreen(navController = navController) {

    }
}