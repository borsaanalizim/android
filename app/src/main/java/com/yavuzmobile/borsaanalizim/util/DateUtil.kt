package com.yavuzmobile.borsaanalizim.util

import android.util.Log
import com.yavuzmobile.borsaanalizim.model.YearMonth
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtil {

    // Int olan timestamp'i Date formatına dönüştüren fonksiyon
    fun fromTimestamp(timestamp: Long, pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): Date? {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val date = Date(timestamp)
        sdf.timeZone = TimeZone.getTimeZone("UTC+3")
        val formattedDate = sdf.format(date)
        return fromString(formattedDate) // Timestamp saniye cinsinden olduğu için 1000 ile çarpıyoruz
    }

    // String olan tarihi Date formatına dönüştüren fonksiyon
    fun fromString(dateString: String, pattern: String = "yyyy-MM-dd'T'HH:mm:ssXXX", timeZone: String = "UTC+3"): Date? {
        return try {
            val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone(timeZone)
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // convert format "yyyy-MM-dd"
    fun getYearMonthDayDateString(date: Date): String {
        val formatYearMonthDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatYearMonthDay.format(date)
    }

    fun getNow(pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): String {
        val currentTime = ZonedDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return currentTime.format(formatter)
    }

    fun getNowDate(): Date? {
        return fromString(getNow())
    }

    fun getFourYearDateAgo(pattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"): Date? {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val fourYearsTimeAgo = ZonedDateTime.now().minusYears(4L)
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return dateFormat.parse(fourYearsTimeAgo.format(formatter))
    }

    fun getLastTwelvePeriods(): List<YearMonth> {
        try {
            val today = LocalDate.now()
            val periods = mutableListOf<YearMonth>()
            val year = today.year
            val month = today.monthValue
            var periodYear = year
            var periodMonth = month


            for (i in 0 until 12) {
                when {
                    month in 1..2 -> {
                        periodYear -= 1
                        periodMonth = 12
                        periods.add(YearMonth("$periodYear", "$periodMonth"))
                    }
                    periodMonth % 3 == 0 -> {
                        periodMonth -= 3
                        if (periodMonth == 0) {
                            periodYear -= 1
                            periodMonth = 12
                        }
                        periods.add(YearMonth("$periodYear", "$periodMonth"))
                    }
                    else -> {
                        periodMonth -= periodMonth % 3
                        periods.add(YearMonth("$periodYear", "$periodMonth"))
                    }
                }
            }
            return periods
        } catch (e: Exception) {
            Log.e("DATE EXCEPTION", e.message.toString())
            return emptyList()
        }
    }
}