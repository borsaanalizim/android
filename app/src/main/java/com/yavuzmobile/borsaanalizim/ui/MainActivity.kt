package com.yavuzmobile.borsaanalizim.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yavuzmobile.borsaanalizim.ui.navgraph.Navigation
import com.yavuzmobile.borsaanalizim.ui.theme.BorsaAnalizimTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BorsaAnalizimTheme {
                Navigation()
            }
        }
    }
}