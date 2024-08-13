package com.yavuzmobile.borsaanalizim.ext

fun String?.toDoubleOrDefault(): Double = this?.toDouble() ?: 0.0

fun String?.cleanedNumberFormat(): String = this?.replace(".", "")?.replace(",", ".") ?: "0.00"
