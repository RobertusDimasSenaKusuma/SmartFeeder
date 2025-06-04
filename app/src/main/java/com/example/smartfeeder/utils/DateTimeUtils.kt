package com.example.smartfeeder.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {

    // Date and time formats
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    private val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
    private val shortDisplayFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    /**
     * Parse schedule date and time strings into a Date object
     */
    fun parseScheduleDateTime(tanggal: String, waktu: String): Date? {
        return try {
            dateTimeFormat.parse("$tanggal $waktu")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Format current date for display
     */
    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    /**
     * Format current time for display
     */
    fun getCurrentTime(): String {
        return timeFormat.format(Date())
    }

    /**
     * Get current timestamp
     */
    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

    /**
     * Format timestamp to readable date time
     */
    fun formatTimestamp(timestamp: Long): String {
        return timestampFormat.format(Date(timestamp))
    }

    /**
     * Format date for display (e.g., "Senin, 15 Januari 2024")
     */
    fun formatDisplayDate(date: Date): String {
        return displayFormat.format(date)
    }

    /**
     * Format date for short display (e.g., "15 Jan 2024")
     */
    fun formatShortDate(date: Date): String {
        return shortDisplayFormat.format(date)
    }

    /**
     * Format countdown time remaining
     */
    fun formatCountdown(milliseconds: Long): String {
        if (milliseconds <= 0) return "Waktu habis"

        val days = TimeUnit.MILLISECONDS.toDays(milliseconds)
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60

        return when {
            days > 0 -> "${days}h ${hours}j ${minutes}m"
            hours > 0 -> "${hours}j ${minutes}m ${seconds}d"
            minutes > 0 -> "${minutes}m ${seconds}d"
            else -> "${seconds}d"
        }
    }

    /**
     * Format time remaining in a more readable format
     */
    fun formatTimeRemaining(milliseconds: Long): String {
        if (milliseconds <= 0) return "Sudah berlalu"

        val days = TimeUnit.MILLISECONDS.toDays(milliseconds)
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60

        return when {
            days > 1 -> "$days hari lagi"
            days == 1L -> "Besok"
            hours > 1 -> "$hours jam lagi"
            hours == 1L -> "1 jam lagi"
            minutes > 30 -> "$minutes menit lagi"
            minutes > 10 -> "$minutes menit lagi"
            minutes > 0 -> "Kurang dari $minutes menit lagi"
            else -> "Sebentar lagi"
        }
    }

    /**
     * Check if a schedule is today
     */
    fun isToday(tanggal: String): Boolean {
        return try {
            val scheduleDate = dateFormat.parse(tanggal)
            val today = Calendar.getInstance()
            val scheduleCalendar = Calendar.getInstance()

            if (scheduleDate != null) {
                scheduleCalendar.time = scheduleDate

                today.get(Calendar.YEAR) == scheduleCalendar.get(Calendar.YEAR) &&
                        today.get(Calendar.DAY_OF_YEAR) == scheduleCalendar.get(Calendar.DAY_OF_YEAR)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if a schedule is tomorrow
     */
    fun isTomorrow(tanggal: String): Boolean {
        return try {
            val scheduleDate = dateFormat.parse(tanggal)
            val tomorrow = Calendar.getInstance()
            tomorrow.add(Calendar.DAY_OF_MONTH, 1)
            val scheduleCalendar = Calendar.getInstance()

            if (scheduleDate != null) {
                scheduleCalendar.time = scheduleDate

                tomorrow.get(Calendar.YEAR) == scheduleCalendar.get(Calendar.YEAR) &&
                        tomorrow.get(Calendar.DAY_OF_YEAR) == scheduleCalendar.get(Calendar.DAY_OF_YEAR)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if schedule is in the past
     */
    fun isInPast(tanggal: String, waktu: String): Boolean {
        val scheduleDateTime = parseScheduleDateTime(tanggal, waktu)
        return scheduleDateTime?.before(Date()) ?: false
    }

    /**
     * Check if schedule is within next hour
     */
    fun isWithinNextHour(tanggal: String, waktu: String): Boolean {
        val scheduleDateTime = parseScheduleDateTime(tanggal, waktu)
        if (scheduleDateTime == null) return false

        val currentTime = System.currentTimeMillis()
        val oneHourLater = currentTime + TimeUnit.HOURS.toMillis(1)

        return scheduleDateTime.time in currentTime..oneHourLater
    }

    /**
     * Get time difference in milliseconds
     */
    fun getTimeDifference(tanggal: String, waktu: String): Long {
        val scheduleDateTime = parseScheduleDateTime(tanggal, waktu)
        return if (scheduleDateTime != null) {
            scheduleDateTime.time - System.currentTimeMillis()
        } else {
            0
        }
    }

    /**
     * Format time for Arduino (epoch seconds)
     */
    fun formatForArduino(tanggal: String, waktu: String): Long {
        val scheduleDateTime = parseScheduleDateTime(tanggal, waktu)
        return scheduleDateTime?.time?.div(1000) ?: 0
    }

    /**
     * Get relative time string (e.g., "2 hours ago", "in 3 minutes")
     */
    fun getRelativeTimeString(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val difference = timestamp - currentTime
        val absDifference = kotlin.math.abs(difference)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(absDifference)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(absDifference)
        val hours = TimeUnit.MILLISECONDS.toHours(absDifference)
        val days = TimeUnit.MILLISECONDS.toDays(absDifference)

        val timeString = when {
            days > 0 -> "${days} hari"
            hours > 0 -> "${hours} jam"
            minutes > 0 -> "${minutes} menit"
            else -> "${seconds} detik"
        }

        return if (difference < 0) {
            "$timeString yang lalu"
        } else {
            "dalam $timeString"
        }
    }

    /**
     * Get next execution time for recurring schedules
     */
    fun getNextExecutionTime(tanggal: String, waktu: String, isDaily: Boolean = false): Date? {
        if (!isDaily) {
            return parseScheduleDateTime(tanggal, waktu)
        }

        val currentTime = Calendar.getInstance()
        val scheduleTime = Calendar.getInstance()

        try {
            val time = timeFormat.parse(waktu)
            if (time != null) {
                scheduleTime.time = time

                // Set today's date with schedule time
                currentTime.apply {
                    set(Calendar.HOUR_OF_DAY, scheduleTime.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, scheduleTime.get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // If time has passed today, schedule for tomorrow
                if (currentTime.timeInMillis <= System.currentTimeMillis()) {
                    currentTime.add(Calendar.DAY_OF_MONTH, 1)
                }

                return currentTime.time
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * Format duration in human readable format
     */
    fun formatDuration(startTime: Long, endTime: Long): String {
        val duration = endTime - startTime
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60

        return when {
            minutes > 0 -> "${minutes} menit ${seconds} detik"
            else -> "${seconds} detik"
        }
    }

    /**
     * Get day of week in Indonesian
     */
    fun getDayOfWeek(tanggal: String): String {
        return try {
            val date = dateFormat.parse(tanggal)
            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date

                when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SUNDAY -> "Minggu"
                    Calendar.MONDAY -> "Senin"
                    Calendar.TUESDAY -> "Selasa"
                    Calendar.WEDNESDAY -> "Rabu"
                    Calendar.THURSDAY -> "Kamis"
                    Calendar.FRIDAY -> "Jumat"
                    Calendar.SATURDAY -> "Sabtu"
                    else -> ""
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Add minutes to current time
     */
    fun addMinutesToCurrentTime(minutes: Int): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutes)

        return Pair(
            dateFormat.format(calendar.time),
            timeFormat.format(calendar.time)
        )
    }

    /**
     * Validate date format
     */
    fun isValidDate(dateString: String): Boolean {
        return try {
            dateFormat.parse(dateString) != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Validate time format
     */
    fun isValidTime(timeString: String): Boolean {
        return try {
            timeFormat.parse(timeString) != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get schedule status based on time
     */
    fun getScheduleStatus(tanggal: String, waktu: String, isActive: Boolean, isExecuted: Boolean): String {
        if (!isActive) return "Nonaktif"
        if (isExecuted) return "Selesai"

        return if (isInPast(tanggal, waktu)) {
            "Terlewat"
        } else {
            "Aktif"
        }
    }
}