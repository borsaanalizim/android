package com.yavuzmobile.borsaanalizim.util

import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtil {

    // Int olan timestamp'i Date formatına dönüştüren fonksiyon
    fun fromTimestamp(timestamp: Long): Date? {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = Date(timestamp)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val formattedDate = sdf.format(date)
        return fromString(formattedDate) // Timestamp saniye cinsinden olduğu için 1000 ile çarpıyoruz
    }

    // String olan tarihi Date formatına dönüştüren fonksiyon
    fun fromString(dateString: String, format: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): Date? {
        return try {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Zulu time (UTC) formatı için timezone ayarı
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // convert format "yyyy-MM-dd"
    fun getYearMonthDayDateString(date: Date): String? {
        val formatYearMonthDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatYearMonthDay.format(date)
    }

    fun getNow(): String {
        val currentTime = ZonedDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return currentTime.format(formatter)
    }
}