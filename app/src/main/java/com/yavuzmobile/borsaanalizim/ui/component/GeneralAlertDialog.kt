package com.yavuzmobile.borsaanalizim.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GeneralAlertDialog(confirmText: String, text: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text(text = confirmText)
            }
        },
        text = { Text(text) }
    )
}