package app.utilities

import android.os.Build
import androidx.annotation.RequiresApi
import app.model.ActivityItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object ActivityItemFilter {

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterByOneDay(items: List<ActivityItem>): List<ActivityItem> {
        val now = LocalDateTime.now()
        val oneDayFromNow = now.plusDays(1)

        return items.filter { item ->
            val itemDate = parseDateTime(item.date, item.time)
            itemDate != null && itemDate.isAfter(now) && itemDate.isBefore(oneDayFromNow)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterBySevenDays(items: List<ActivityItem>): List<ActivityItem> {
        val now = LocalDateTime.now()
        val sevenDaysFromNow = now.plusDays(7)

        return items.filter { item ->
            val itemDate = parseDateTime(item.date, item.time)
            itemDate != null && itemDate.isAfter(now) && itemDate.isBefore(sevenDaysFromNow)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun filterByOneHour(items: List<ActivityItem>): List<ActivityItem> {
        val now = LocalDateTime.now()
        val oneHourFromNow = now.plusHours(1)

        return items.filter { item ->
            val itemDate = parseDateTime(item.date, item.time)
            itemDate != null && itemDate.isAfter(now) && itemDate.isBefore(oneHourFromNow)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDateTime(date: String, time: String): LocalDateTime? {
        return try {
            val dateTimeString = "$date $time"
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            LocalDateTime.parse(dateTimeString, formatter)
        } catch (e: Exception) {
            // Handle parsing exception
            null
        }
    }
}
