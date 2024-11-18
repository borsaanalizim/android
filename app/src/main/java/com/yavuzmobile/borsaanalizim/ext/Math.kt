package com.yavuzmobile.borsaanalizim.ext

import com.yavuzmobile.borsaanalizim.util.DateUtil

fun String?.toDoubleOrDefault(): Double = if (this.orEmpty().isEmpty()) 0.0 else this.orEmpty().toDouble()

fun String?.cleanedNumberFormat(): String = this?.replace(".", "")?.replace(",", ".") ?: "0.00"

fun String?.decimalNumberFormat(): String = this?.replace(".", ",") ?: "0,00"

fun Double?.orDefault(): Double = this ?: kotlin.run { 0.0 }

fun String.totalNumber(): Int {
    val (year, quarter) = this.split("/").map { it.toInt() }
    return year * 10 + quarter
}

fun String.isComparePeriod(): Boolean {
    val totalPeriod = this.totalNumber()
    val firstTotalPeriod = DateUtil.getFirstPeriod().totalNumber()
    return totalPeriod >= firstTotalPeriod
}