package com.example.smartfeeder.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfeeder.R
import com.example.smartfeeder.models.ScheduleModel
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

class ScheduleAdapter(
    private val schedules: MutableList<ScheduleModel>,
    private val onItemClick: (ScheduleModel) -> Unit,
    private val onDeleteClick: (ScheduleModel) -> Unit,
    private val onStatusToggle: (ScheduleModel, Boolean) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    companion object {
        private const val TAG = "ScheduleAdapter"
    }

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardSchedule)
        val tvScheduleName: MaterialTextView = itemView.findViewById(R.id.tvScheduleName)
        val tvFoodType: MaterialTextView = itemView.findViewById(R.id.tvFoodType)
        val tvDateTime: MaterialTextView = itemView.findViewById(R.id.tvDateTime)
        val tvTimeLeft: MaterialTextView = itemView.findViewById(R.id.tvTimeLeft)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]

        // Set data ke views
        holder.tvScheduleName.text = schedule.namaJadwal
        holder.tvFoodType.text = schedule.jenisMakanan

        // Format date and time - using SimpleDateFormat to show both date and time
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.tvDateTime.text = dateTimeFormat.format(schedule.waktu)

        // Determine schedule status based on time and create a default status
        val currentStatus = determineScheduleStatus(schedule)
        val isScheduleActive = isScheduleStillActive(schedule)




        // Set time left information
        setTimeLeftInfo(holder, schedule, currentStatus, isScheduleActive)

        // Set card background based on status
        setCardBackground(holder, schedule, currentStatus, isScheduleActive)

        // Set click listeners
        holder.cardView.setOnClickListener {
            onItemClick(schedule)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(schedule)
        }

    }

    override fun getItemCount(): Int = schedules.size

    private fun determineScheduleStatus(schedule: ScheduleModel): String {
        val currentTime = System.currentTimeMillis()
        val scheduleTime = schedule.waktu.time

        return when {
            scheduleTime < currentTime - (60 * 60 * 1000) -> "expired" // More than 1 hour past
            scheduleTime < currentTime -> "completed" // Past time but within 1 hour
            else -> "pending" // Future time
        }
    }

    private fun isScheduleStillActive(schedule: ScheduleModel): Boolean {
        val currentTime = System.currentTimeMillis()
        val scheduleTime = schedule.waktu.time

        // Consider schedule active if it's in the future or within 1 hour of completion
        return scheduleTime > currentTime - (60 * 60 * 1000)
    }

    private fun getStatusText(status: String): String {
        return when (status) {
            "pending" -> "Menunggu"
            "completed" -> "Selesai"
            "cancelled" -> "Dibatalkan"
            "expired" -> "Kedaluwarsa"
            else -> status.uppercase()
        }
    }

    private fun getStatusColor(status: String): Int {
        return when (status) {
            "pending" -> Color.parseColor("#FF9800") // Orange
            "completed" -> Color.parseColor("#4CAF50") // Green
            "cancelled" -> Color.parseColor("#F44336") // Red
            "expired" -> Color.parseColor("#9E9E9E") // Gray
            else -> Color.parseColor("#2196F3") // Blue
        }
    }

    private fun setTimeLeftInfo(holder: ScheduleViewHolder, schedule: ScheduleModel, status: String, isActive: Boolean) {
        if (isActive && status == "pending") {
            val minutesLeft = schedule.getMinutesUntilSchedule()

            when {
                minutesLeft > 1440 -> { // More than 1 day
                    val days = minutesLeft / 1440
                    holder.tvTimeLeft.text = "Dalam ${days} hari"
                    holder.tvTimeLeft.setTextColor(Color.parseColor("#4CAF50"))
                }
                minutesLeft > 60 -> { // More than 1 hour
                    val hours = minutesLeft / 60
                    holder.tvTimeLeft.text = "Dalam ${hours} jam"
                    holder.tvTimeLeft.setTextColor(Color.parseColor("#FF9800"))
                }
                minutesLeft > 0 -> { // Less than 1 hour
                    holder.tvTimeLeft.text = "Dalam ${minutesLeft} menit"
                    holder.tvTimeLeft.setTextColor(Color.parseColor("#F44336"))
                }
                else -> {
                    holder.tvTimeLeft.text = "Sudah lewat waktu"
                    holder.tvTimeLeft.setTextColor(Color.parseColor("#9E9E9E"))
                }
            }
            holder.tvTimeLeft.visibility = View.VISIBLE
        } else {
            holder.tvTimeLeft.visibility = View.GONE
        }
    }

    private fun setCardBackground(holder: ScheduleViewHolder, schedule: ScheduleModel, status: String, isActive: Boolean) {
        when {
            status == "completed" -> {
                holder.cardView.setCardBackgroundColor(
                    Color.parseColor("#E8F5E8") // Light green
                )
            }
            status == "expired" || status == "cancelled" -> {
                holder.cardView.setCardBackgroundColor(
                    Color.parseColor("#FFEBEE") // Light red
                )
            }
            !isActive -> {
                holder.cardView.setCardBackgroundColor(
                    Color.parseColor("#F5F5F5") // Light gray
                )
            }
            isActive && schedule.getMinutesUntilSchedule() <= 60 && schedule.getMinutesUntilSchedule() > 0 -> {
                holder.cardView.setCardBackgroundColor(
                    Color.parseColor("#FFF3E0") // Light orange
                )
            }
            else -> {
                holder.cardView.setCardBackgroundColor(Color.WHITE)
            }
        }
    }

    // Helper functions for adapter updates
    fun addSchedule(schedule: ScheduleModel) {
        schedules.add(0, schedule) // Add to beginning
        notifyItemInserted(0)
    }

    fun updateSchedule(updatedSchedule: ScheduleModel) {
        val index = schedules.indexOfFirst { it.id == updatedSchedule.id }
        if (index != -1) {
            schedules[index] = updatedSchedule
            notifyItemChanged(index)
        }
    }

    fun removeSchedule(scheduleId: String) {
        val index = schedules.indexOfFirst { it.id == scheduleId }
        if (index != -1) {
            schedules.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateSchedules(newSchedules: List<ScheduleModel>) {
        schedules.clear()
        schedules.addAll(newSchedules)
        notifyDataSetChanged()
    }

    fun getScheduleAt(position: Int): ScheduleModel? {
        return if (position in 0 until schedules.size) {
            schedules[position]
        } else null
    }
}