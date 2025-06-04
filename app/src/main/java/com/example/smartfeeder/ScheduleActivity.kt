package com.example.smartfeeder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfeeder.adapters.ScheduleAdapter
import com.example.smartfeeder.models.ScheduleModel
import com.example.smartfeeder.utils.FirebaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ScheduleActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var fabAddSchedule: FloatingActionButton

    private val schedules = mutableListOf<ScheduleModel>()

    companion object {
        private const val TAG = "ScheduleActivity"
        private const val REQUEST_ADD_SCHEDULE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        initializeViews()
        setupRecyclerView()
        setupFab()
        loadSchedules()
    }

    private fun initializeViews() {
        try {
            recyclerView = findViewById(R.id.recyclerViewSchedules)
            fabAddSchedule = findViewById(R.id.fabAddSchedule)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views: ${e.message}", e)
            showToast("Error initializing UI components")
        }
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter(
            schedules = schedules,
            onItemClick = { schedule ->
                handleScheduleItemClick(schedule)
            },
            onDeleteClick = { schedule ->
                showDeleteConfirmationDialog(schedule)
            },
            onStatusToggle = { schedule, isActive ->
                handleScheduleStatusToggle(schedule, isActive)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ScheduleActivity)
            adapter = scheduleAdapter
        }
    }

    private fun setupFab() {
        fabAddSchedule.setOnClickListener {
            startAddScheduleActivity()
        }
    }

    private fun loadSchedules() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                FirebaseHelper.getAllSchedules().fold(
                    onSuccess = { scheduleList ->
                        Log.d(TAG, "Loaded ${scheduleList.size} schedules")
                        schedules.clear()
                        schedules.addAll(scheduleList)
                        scheduleAdapter.notifyDataSetChanged()

                        if (schedules.isEmpty()) {
                            showEmptyState()
                        } else {
                            hideEmptyState()
                        }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Failed to load schedules: ${error.message}", error)
                        showToast("Gagal memuat jadwal: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading schedules: ${e.message}", e)
                showToast("Terjadi kesalahan saat memuat jadwal")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun handleScheduleItemClick(schedule: ScheduleModel) {
        Log.d(TAG, "Schedule item clicked: ${schedule.id}")
        // You can implement edit functionality here if needed
        showToast("Jadwal: ${schedule.namaJadwal}")
    }

    private fun showDeleteConfirmationDialog(schedule: ScheduleModel) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Jadwal")
            .setMessage("Apakah Anda yakin ingin menghapus jadwal '${schedule.namaJadwal}'?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteSchedule(schedule)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteSchedule(schedule: ScheduleModel) {
        lifecycleScope.launch {
            try {
                showLoading(true)

                FirebaseHelper.deleteSchedule(schedule.id).fold(
                    onSuccess = {
                        Log.d(TAG, "Schedule deleted successfully: ${schedule.id}")
                        scheduleAdapter.removeSchedule(schedule.id)
                        showToast("Jadwal berhasil dihapus")

                        if (schedules.isEmpty()) {
                            showEmptyState()
                        }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Failed to delete schedule: ${error.message}", error)
                        showToast("Gagal menghapus jadwal: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception deleting schedule: ${e.message}", e)
                showToast("Terjadi kesalahan saat menghapus jadwal")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun handleScheduleStatusToggle(schedule: ScheduleModel, isActive: Boolean) {
        lifecycleScope.launch {
            try {
                val status = if (isActive) "pending" else "cancelled"

                FirebaseHelper.updateScheduleStatus(schedule.id, isActive, status).fold(
                    onSuccess = {
                        Log.d(TAG, "Schedule status updated: ${schedule.id}, isActive: $isActive")
                        // Update local data
                        val updatedSchedule = schedule.copy().apply {
                            // Note: Since we can't modify ScheduleModel, we just refresh the list
                        }

                        // Refresh the specific item
                        scheduleAdapter.notifyDataSetChanged()

                        val statusText = if (isActive) "diaktifkan" else "dinonaktifkan"
                        showToast("Jadwal berhasil $statusText")
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Failed to update schedule status: ${error.message}", error)
                        showToast("Gagal mengubah status jadwal")

                        // Revert the switch state
                        scheduleAdapter.notifyDataSetChanged()
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating schedule status: ${e.message}", e)
                showToast("Terjadi kesalahan saat mengubah status")
                scheduleAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun startAddScheduleActivity() {
        try {
            val intent = Intent(this, FeedControlActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_SCHEDULE)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting FeedControlActivity: ${e.message}", e)
            showToast("Gagal membuka halaman tambah jadwal")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ADD_SCHEDULE && resultCode == RESULT_OK) {
            Log.d(TAG, "Schedule added successfully, refreshing list")
            loadSchedules()
        }
    }

    private fun showLoading(show: Boolean) {
        // Implement loading indicator if you have one in your layout
        // For example: progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmptyState() {
        // Implement empty state if you have one in your layout
        // For example: emptyStateView.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        // Implement hide empty state if you have one in your layout
        // For example: emptyStateView.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh schedules when returning to this activity
        loadSchedules()
    }
}