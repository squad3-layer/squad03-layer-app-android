package com.example.feature.news.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatDate(dateString: String): String {
    return try {
        val localePtBr = Locale.forLanguageTag("pt-BR")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val date = inputFormat.parse(dateString) ?: return dateString

        val calendar = Calendar.getInstance()
        calendar.time = date

        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)

        val timeFormat = SimpleDateFormat("HH:mm", localePtBr)
        val dateFormat = SimpleDateFormat("dd/MM", localePtBr)

        when {
            isSameDay(calendar, today) -> "Hoje, ${timeFormat.format(date)}"
            isSameDay(calendar, yesterday) -> "Ontem, ${timeFormat.format(date)}"
            else -> dateFormat.format(date)
        }
    } catch (e: Exception) {
        dateString
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}