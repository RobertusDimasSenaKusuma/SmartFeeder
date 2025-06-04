package com.example.smartfeeder.models

import com.google.firebase.Timestamp

data class SmartFeederDevice(
    val id: String = "",
    val name: String = "",
    val wifiSSID: String = "",
    val ipAddress: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Timestamp? = null,
    val firmwareVersion: String = "1.0.0",
    val batteryLevel: Int = 100,
    val nextFeedingTime: String = "",
    val totalFeedings: Int = 0,
    val lastFeedingTime: Timestamp? = null
) {
    fun getStatusText(): String {
        return if (isOnline) "Online" else "Offline"
    }

    fun getStatusColor(): Int {
        return if (isOnline) android.graphics.Color.GREEN else android.graphics.Color.RED
    }

    fun getLastSeenText(): String {
        return lastSeen?.let {
            val now = System.currentTimeMillis()
            val lastSeenMillis = it.seconds * 1000
            val diffInMinutes = (now - lastSeenMillis) / (1000 * 60)

            when {
                diffInMinutes < 1 -> "Baru saja"
                diffInMinutes < 60 -> "${diffInMinutes.toInt()} menit yang lalu"
                diffInMinutes < 1440 -> "${(diffInMinutes / 60).toInt()} jam yang lalu"
                else -> "${(diffInMinutes / 1440).toInt()} hari yang lalu"
            }
        } ?: "Tidak diketahui"
    }
}