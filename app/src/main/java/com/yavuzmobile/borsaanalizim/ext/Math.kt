package com.yavuzmobile.borsaanalizim.ext

fun String?.toDoubleOrDefault(): Double = if (this.orEmpty().isEmpty()) 0.0 else this.orEmpty().toDouble()

fun String?.cleanedNumberFormat(): String = this?.replace(".", "")?.replace(",", ".") ?: "0.00"
