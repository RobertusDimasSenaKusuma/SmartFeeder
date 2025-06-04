package com.example.smartfeeder.models

import java.text.SimpleDateFormat
import java.util.*

data class ScheduleModel(
    var id: String = "",
    var namaJadwal: String = "",
    var jenisMakanan: String = "",
    var waktu: Date = Date(), // Changed to Date object
    var userId: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var createdAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
) {

    // Constructor tanpa parameter untuk Firebase
    constructor() : this("", "", "", Date(), "", System.currentTimeMillis(), "")

    /**
     * Mengkonversi waktu ke format yang bisa dibaca Arduino
     * Format: HH:MM:SS
     */
    fun getTimeForArduino(): String {
        return try {
            val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            outputFormat.format(waktu)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Mendapatkan timestamp dalam milidetik dari waktu jadwal
     */
    fun getScheduleTimestamp(): Long {
        return waktu.time
    }

    /**
     * Mendapatkan selisih waktu dalam menit dari sekarang (untuk waktu hari ini)
     */
    fun getMinutesUntilSchedule(): Long {
        // Create a calendar for the current date and set the schedule time
        val scheduleCalendar = Calendar.getInstance().apply {
            time = waktu // This `waktu` Date object should ideally only contain time information
            // For a complete schedule timestamp (date + time), you'd typically combine `tanggal` and `waktu`.
            // Since `tanggal` is removed, this calculation assumes `waktu` refers to a time on the current day.
            // If you need scheduling for future dates, consider re-introducing a date component or using a more robust date-time library.
        }

        val currentTime = System.currentTimeMillis()
        return (scheduleCalendar.timeInMillis - currentTime) / (1000 * 60)
    }

    /**
     * Format data untuk ditampilkan
     */
    fun getDisplayText(): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return "$namaJadwal - $jenisMakanan pada ${timeFormat.format(waktu)}"
    }

    /**
     * Convert ke Map untuk Firebase
     */
    fun toMap(): Map<String, Any> {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // Format for storing time as String in Firebase
        return mapOf(
            "id" to id,
            "namaJadwal" to namaJadwal,
            "jenisMakanan" to jenisMakanan,
            "waktu" to timeFormat.format(waktu), // Store as formatted String in Firebase
            "userId" to userId,
            "timestamp" to timestamp,
            "createdAt" to createdAt,
            "timeForArduino" to getTimeForArduino(),
            "scheduleTimestamp" to getScheduleTimestamp()
        )
    }

    /**
     * Create dari Map (untuk membaca dari Firebase)
     */
    companion object {
        fun fromMap(map: Map<String, Any>): ScheduleModel {
            val timeString = map["waktu"] as? String ?: "00:00"
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val parsedTime = try {
                timeFormat.parse(timeString) ?: Date()
            } catch (e: Exception) {
                Date()
            }

            return ScheduleModel(
                id = map["id"] as? String ?: "",
                namaJadwal = map["namaJadwal"] as? String ?: "",
                jenisMakanan = map["jenisMakanan"] as? String ?: "",
                waktu = parsedTime,
                userId = map["userId"] as? String ?: "",
                timestamp = map["timestamp"] as? Long ?: System.currentTimeMillis(),
                createdAt = map["createdAt"] as? String ?: ""
            )
        }

        /**
         * Generate ID unik untuk jadwal
         */
        fun generateId(): String {
            return "schedule_${System.currentTimeMillis()}_${(1000..9999).random()}"
        }
    }

    override fun toString(): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return "ScheduleModel(id='$id', namaJadwal='$namaJadwal', jenisMakanan='$jenisMakanan', waktu='${timeFormat.format(waktu)}', userId='$userId')"
    }
}