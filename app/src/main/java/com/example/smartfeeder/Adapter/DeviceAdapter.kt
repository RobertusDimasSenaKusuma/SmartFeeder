package com.example.smartfeeder.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfeeder.R
import com.example.smartfeeder.models.SmartFeederDevice
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class DeviceAdapter(
    private val devices: MutableList<SmartFeederDevice> = mutableListOf(),
    private val onDeviceClick: (SmartFeederDevice) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    init {
        // Load dummy data if list is empty
        if (devices.isEmpty()) {
            loadDummyData()
        }
    }

    private fun loadDummyData() {
        val dummyDevices = listOf(
            SmartFeederDevice(
                id = "feeder_001",
                name = "Smart Feeder Ruang Tamu",
                wifiSSID = "HOME_WIFI_5G",
                ipAddress = "192.168.1.105",
                isOnline = true,
                lastSeen = Timestamp.now(),
                firmwareVersion = "2.1.0",
                batteryLevel = 85,
                nextFeedingTime = "14:30 - Siang"
            ),

        )

        devices.addAll(dummyDevices)
    }

    // Method to add more dummy data (for testing scan functionality)
    fun addDummyDevice() {
        val randomId = "feeder_${(100..999).random()}"
        val locations = listOf("Kebun", "Balkon", "Gudang", "Kamar", "Ruang Kerja")
        val wifiNetworks = listOf("HOME_WIFI_5G", "HOME_WIFI_2.4G", "OFFICE_WIFI", "GUEST_NETWORK")

        val newDevice = SmartFeederDevice(
            id = randomId,
            name = "Smart Feeder ${locations.random()}",
            wifiSSID = wifiNetworks.random(),
            ipAddress = "192.168.1.${(100..200).random()}",
            isOnline = (0..1).random() == 1,
            lastSeen = Timestamp(Date(System.currentTimeMillis() - (0..7200000).random())),
            firmwareVersion = listOf("1.9.0", "2.0.0", "2.1.0").random(),
            batteryLevel = (10..100).random(),
            nextFeedingTime = if ((0..1).random() == 1) {
                listOf("06:00 - Pagi", "12:00 - Siang", "18:00 - Malam").random()
            } else {
                "Tidak dijadwalkan"
            }
        )

        devices.add(newDevice)
        notifyItemInserted(devices.size - 1)
    }

    // Method to clear and reload dummy data
    fun refreshDummyData() {
        devices.clear()
        loadDummyData()
        notifyDataSetChanged()
    }

    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardDevice: CardView = itemView.findViewById(R.id.cardDevice)
        private val ivDeviceIcon: ImageView = itemView.findViewById(R.id.ivDeviceIcon)
        private val tvDeviceName: TextView = itemView.findViewById(R.id.tvDeviceName)
        private val tvWifiSSID: TextView = itemView.findViewById(R.id.tvWifiSSID)
        private val tvIpAddress: TextView = itemView.findViewById(R.id.tvIpAddress)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvLastSeen: TextView = itemView.findViewById(R.id.tvLastSeen)
        private val tvBatteryLevel: TextView = itemView.findViewById(R.id.tvBatteryLevel)
        private val tvNextFeeding: TextView = itemView.findViewById(R.id.tvNextFeeding)

        fun bind(device: SmartFeederDevice) {
            tvDeviceName.text = device.name
            tvWifiSSID.text = "WiFi: ${device.wifiSSID}"
            tvIpAddress.text = "IP: ${device.ipAddress}"
            tvStatus.text = if (device.isOnline) "Online" else "Offline"
            tvLastSeen.text = "Terakhir dilihat: ${formatLastSeen(device.lastSeen)}"
            tvBatteryLevel.text = "Baterai: ${device.batteryLevel}%"
            tvNextFeeding.text = "Jadwal berikutnya: ${device.nextFeedingTime}"

            // Set device icon based on status
            val iconRes = if (device.isOnline) {
                android.R.drawable.presence_online
            } else {
                android.R.drawable.presence_offline
            }
            ivDeviceIcon.setImageResource(iconRes)

            // Set status color
            val statusColor = if (device.isOnline) {
                ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark)
            } else {
                ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
            }
            tvStatus.setTextColor(statusColor)

            // Set battery level color
            val batteryColor = when {
                device.batteryLevel > 50 -> ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark)
                device.batteryLevel > 20 -> ContextCompat.getColor(itemView.context, android.R.color.holo_orange_dark)
                else -> ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
            }
            tvBatteryLevel.setTextColor(batteryColor)

            // Click listener
            cardDevice.setOnClickListener {
                onDeviceClick(device)
            }

            // Card appearance based on status
            if (device.isOnline) {
                cardDevice.alpha = 1.0f
                cardDevice.isEnabled = true
            } else {
                cardDevice.alpha = 0.7f
                cardDevice.isEnabled = true
            }
        }

        private fun formatLastSeen(timestamp: Timestamp?): String {
            if (timestamp == null) return "Tidak diketahui"

            val now = System.currentTimeMillis()
            val lastSeenTime = timestamp.toDate().time
            val diffInMillis = now - lastSeenTime

            return when {
                diffInMillis < 60000 -> "Baru saja"
                diffInMillis < 3600000 -> "${diffInMillis / 60000} menit yang lalu"
                diffInMillis < 86400000 -> "${diffInMillis / 3600000} jam yang lalu"
                else -> {
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    sdf.format(timestamp.toDate())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount(): Int = devices.size

    // Public methods for external access
    fun getDevices(): List<SmartFeederDevice> = devices.toList()

    fun filterDevices(query: String): List<SmartFeederDevice> {
        return if (query.isEmpty()) {
            devices.toList()
        } else {
            val lowercaseQuery = query.lowercase()
            devices.filter {
                it.name.lowercase().contains(lowercaseQuery) ||
                        it.wifiSSID.lowercase().contains(lowercaseQuery) ||
                        it.ipAddress.contains(query)
            }
        }
    }
}