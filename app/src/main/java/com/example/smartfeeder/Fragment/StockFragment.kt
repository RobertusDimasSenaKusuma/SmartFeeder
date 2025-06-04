package com.example.smartfeeder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.smartfeeder.models.ScheduleModel
import com.example.smartfeeder.utils.FirebaseHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StockFragment : Fragment() {

    private lateinit var tvStockAmount: TextView
    private lateinit var detailContainer: LinearLayout

    companion object {
        private const val TAG = "StockFragment"
        private const val TOTAL_CAPACITY = 500 // Total capacity in grams
        private const val PORTION_PER_SCHEDULE = 100 // Grams per schedule
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        loadStockData()
    }

    private fun initializeViews(view: View) {
        tvStockAmount = view.findViewById(R.id.tv_stock_amount)
        detailContainer = view.findViewById(R.id.detail_container)
    }

    private fun loadStockData() {
        lifecycleScope.launch {
            try {
                FirebaseHelper.getAllSchedules().fold(
                    onSuccess = { schedules ->
                        Log.d(TAG, "Loaded ${schedules.size} schedules for stock calculation")
                        calculateAndDisplayStock(schedules)
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Failed to load schedules: ${error.message}", error)
                        showToast("Gagal memuat data jadwal")

                        // Show default values when error occurs
                        tvStockAmount.text = TOTAL_CAPACITY.toString()
                        clearDetailContainer()
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading stock data: ${e.message}", e)
                showToast("Terjadi kesalahan saat memuat data")

                // Show default values when exception occurs
                tvStockAmount.text = TOTAL_CAPACITY.toString()
                clearDetailContainer()
            }
        }
    }

    private fun calculateAndDisplayStock(schedules: List<ScheduleModel>) {
        // Filter active schedules (you might want to add isActive field to ScheduleModel)
        val activeSchedules = schedules.filter {
            // Add your logic here to filter active schedules if needed
            // For now, we'll consider all schedules as active
            true
        }

        // Calculate remaining stock
        val totalUsed = activeSchedules.size * PORTION_PER_SCHEDULE
        val remainingStock = TOTAL_CAPACITY - totalUsed

        // Update stock display
        tvStockAmount.text = remainingStock.toString()

        // Update detail usage
        displayUsageDetails(activeSchedules)

        Log.d(TAG, "Stock calculation: Total=${TOTAL_CAPACITY}g, Used=${totalUsed}g, Remaining=${remainingStock}g")
    }

    private fun displayUsageDetails(schedules: List<ScheduleModel>) {
        clearDetailContainer()

        if (schedules.isEmpty()) {
            // Show empty state message
            addEmptyStateView()
            return
        }

        // Sort schedules by time
        val sortedSchedules = schedules.sortedBy { it.waktu }

        for (schedule in sortedSchedules) {
            addUsageDetailView(schedule)
        }
    }

    private fun addUsageDetailView(schedule: ScheduleModel) {
        val context = requireContext()

        // Create CardView
        val cardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = resources.getDimensionPixelSize(R.dimen.card_margin_bottom) // 12dp
            }
            radius = resources.getDimension(R.dimen.card_corner_radius) // 12dp
            cardElevation = resources.getDimension(R.dimen.card_elevation) // 2dp
            setCardBackgroundColor(resources.getColor(R.color.white, null))
        }

        // Create main LinearLayout
        val mainLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            val padding = resources.getDimensionPixelSize(R.dimen.card_padding) // 16dp
            setPadding(padding, padding, padding, padding)
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        // Create left side layout (schedule info)
        val leftLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            orientation = LinearLayout.VERTICAL
        }

        // Schedule name
        val scheduleNameTV = TextView(context).apply {
            text = schedule.namaJadwal
            setTextColor(resources.getColor(R.color.black, null))
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Schedule time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val scheduleTimeTV = TextView(context).apply {
            text = "${schedule.jenisMakanan} • ${timeFormat.format(schedule.waktu)}"
            setTextColor(resources.getColor(R.color.gray, null))
            textSize = 14f
        }

        leftLayout.addView(scheduleNameTV)
        leftLayout.addView(scheduleTimeTV)

        // Create right side layout (portion amount)
        val rightLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        // Portion amount
        val portionAmountTV = TextView(context).apply {
            text = PORTION_PER_SCHEDULE.toString()
            setTextColor(resources.getColor(R.color.orange, null))
            textSize = 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            val marginEnd = resources.getDimensionPixelSize(R.dimen.text_margin_end) // 8dp
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, marginEnd, 0)
            }
        }

        // Gram text
        val gramTV = TextView(context).apply {
            text = "gram"
            setTextColor(resources.getColor(R.color.orange, null))
            textSize = 14f
        }

        rightLayout.addView(portionAmountTV)
        rightLayout.addView(gramTV)

        // Add all views to main layout
        mainLayout.addView(leftLayout)
        mainLayout.addView(rightLayout)

        // Add main layout to card
        cardView.addView(mainLayout)

        // Add card to container
        detailContainer.addView(cardView)
    }

    private fun addEmptyStateView() {
        val context = requireContext()

        val emptyStateTV = TextView(context).apply {
            text = "Belum ada jadwal pemberian makan"
            setTextColor(resources.getColor(R.color.gray, null))
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            val padding = resources.getDimensionPixelSize(R.dimen.empty_state_padding) // 24dp
            setPadding(padding, padding, padding, padding)
        }

        detailContainer.addView(emptyStateTV)
    }

    private fun clearDetailContainer() {
        detailContainer.removeAllViews()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        loadStockData()
    }
}