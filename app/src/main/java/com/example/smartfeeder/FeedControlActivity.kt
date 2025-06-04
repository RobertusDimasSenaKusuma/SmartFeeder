package com.example.smartfeeder

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartfeeder.models.ScheduleModel
import com.example.smartfeeder.utils.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FeedControlActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    // UI Components
    private lateinit var etNamaJadwal: EditText
    private lateinit var etTanggal: EditText
    private lateinit var spinnerMakananMinuman: Spinner
    private lateinit var tvWaktuDisplay: TextView
    private lateinit var btnSimpan: Button
    private lateinit var btnKembali: Button

    // Data
    private val selectedDate: Calendar = Calendar.getInstance()
    private val selectedTime: Calendar = Calendar.getInstance().apply {
        add(Calendar.MINUTE, 10)
    }
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    companion object {
        private const val TAG = "FeedControlActivity"
        private const val DEFAULT_DEVICE_ID = "feeder_001"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_control)

        initializeFirebase()
        setupTransparentStatusBar()
        initializeViews()
        setupProgressDialog()
        setupMakananMinumanSpinner()
        setupDatePicker()
        setupTimePicker()
        setupButtons()
    }

    private fun initializeFirebase() {
        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "Checking authentication status")
        if (auth.currentUser == null) {
            signInAnonymously()
        } else {
            Log.d(TAG, "User already authenticated: ${auth.currentUser?.uid}")
        }
    }

    private fun signInAnonymously() {
        showProgressDialog("Connecting to Smart Feeder...")
        lifecycleScope.launch {
            try {
                auth.signInAnonymously().await()
                Log.d(TAG, "Anonymous sign-in successful: ${auth.currentUser?.uid}")
                showToast("Connected to Smart Feeder")
            } catch (e: Exception) {
                Log.e(TAG, "Anonymous sign-in failed: ${e.message}", e)
                showToast("Connection failed: ${e.message}")
            } finally {
                hideProgressDialog()
            }
        }
    }

    private fun setupProgressDialog() {
        progressDialog = ProgressDialog(this).apply {
            setMessage("Menyimpan jadwal...")
            setCancelable(false)
        }
    }

    private fun setupTransparentStatusBar() {
        window.apply {
            statusBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setDecorFitsSystemWindows(false)
                insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun initializeViews() {
        try {
            etNamaJadwal = findViewById(R.id.et_nama_jadwal)
            etTanggal = findViewById(R.id.et_tanggal)
            spinnerMakananMinuman = findViewById(R.id.spinner_makanan_minuman)
            tvWaktuDisplay = findViewById(R.id.tv_waktu_display)
            btnSimpan = findViewById(R.id.btn_simpan)
            btnKembali = findViewById(R.id.btn_kembali)
            findViewById<View>(R.id.btn_back)?.setOnClickListener { handleBackButton() }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views: ${e.message}", e)
            showToast("Error initializing UI: ${e.message}")
        }
    }

    private fun setupMakananMinumanSpinner() {
        val options = arrayOf("Pilih Jenis", "Makanan", "Air Minum", "Makanan + Air")
        spinnerMakananMinuman.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            options
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerMakananMinuman.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    Log.d(TAG, "Selected food type: ${options[position]}")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDatePicker() {
        // Set initial date to today
        etTanggal.setText(dateFormat.format(selectedDate.time))

        etTanggal.setOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                etTanggal.setText(dateFormat.format(selectedDate.time))
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).apply {
            // Set minimum date to today
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }

    private fun setupTimePicker() {
        tvWaktuDisplay.text = timeFormat.format(selectedTime.time)
        tvWaktuDisplay.setOnClickListener { showTimePicker() }
    }

    private fun showTimePicker() {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                selectedTime.set(Calendar.SECOND, 0)
                selectedTime.set(Calendar.MILLISECOND, 0)
                tvWaktuDisplay.text = timeFormat.format(selectedTime.time)
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun setupButtons() {
        btnKembali.setOnClickListener { handleBackButton() }
        btnSimpan.setOnClickListener { handleSimpanButton() }
    }

    private fun handleSimpanButton() {
        val namaJadwal = etNamaJadwal.text.toString().trim()
        val tanggal = etTanggal.text.toString().trim()
        val spinnerPosition = spinnerMakananMinuman.selectedItemPosition
        val waktu = tvWaktuDisplay.text.toString()

        if (!validateInput(namaJadwal, tanggal, spinnerPosition)) return
        if (isScheduleInPast()) {
            showToast("Tidak dapat menjadwalkan untuk waktu yang sudah berlalu")
            return
        }

        val scheduleId = ScheduleModel.generateId()

        // Combine selected date and time
        val scheduledDateTime = Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
            set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val schedule = ScheduleModel(
            id = scheduleId,
            namaJadwal = namaJadwal,
            jenisMakanan = spinnerMakananMinuman.selectedItem.toString(),
            waktu = scheduledDateTime.time,
            userId = auth.currentUser?.uid ?: ""
        )

        saveScheduleToFirebase(schedule)
    }

    private fun validateInput(namaJadwal: String, tanggal: String, spinnerPosition: Int): Boolean {
        return when {
            namaJadwal.isEmpty() -> {
                etNamaJadwal.error = "Nama jadwal harus diisi"
                etNamaJadwal.requestFocus()
                false
            }
            namaJadwal.length < 3 -> {
                etNamaJadwal.error = "Nama jadwal minimal 3 karakter"
                etNamaJadwal.requestFocus()
                false
            }
            tanggal.isEmpty() -> {
                showToast("Pilih tanggal jadwal")
                etTanggal.requestFocus()
                false
            }
            spinnerPosition == 0 -> {
                showToast("Pilih jenis makanan atau minuman")
                false
            }
            else -> true
        }
    }

    private fun isScheduleInPast(): Boolean {
        return try {
            val now = Calendar.getInstance()
            val scheduledDateTime = Calendar.getInstance().apply {
                set(Calendar.YEAR, selectedDate.get(Calendar.YEAR))
                set(Calendar.MONTH, selectedDate.get(Calendar.MONTH))
                set(Calendar.DAY_OF_MONTH, selectedDate.get(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            scheduledDateTime.before(now)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking schedule time: ${e.message}", e)
            false
        }
    }

    private fun saveScheduleToFirebase(schedule: ScheduleModel) {
        showProgressDialog("Menyimpan jadwal...")
        lifecycleScope.launch {
            FirebaseHelper.saveSchedule(schedule).fold(
                onSuccess = { message ->
                    Log.d(TAG, "Schedule saved successfully: ${schedule.id}")
                    sendFeedingCommandToDevice(schedule)
                    handleSaveSuccess(schedule, message)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to save schedule: ${error.message}", error)
                    handleSaveError(error.message ?: "Unknown error")
                }
            )
            hideProgressDialog()
        }
    }

    private fun sendFeedingCommandToDevice(schedule: ScheduleModel) {
        lifecycleScope.launch {
            val portion = when (schedule.jenisMakanan) {
                "Makanan" -> 50
                "Air Minum" -> 100
                "Makanan + Air" -> 75
                else -> 50
            }

            FirebaseHelper.triggerFeeding(
                deviceId = DEFAULT_DEVICE_ID,
                scheduleId = schedule.id,
                portion = portion
            ).fold(
                onSuccess = { Log.d(TAG, "Feeding command sent successfully for schedule: ${schedule.id}") },
                onFailure = { error -> Log.e(TAG, "Failed to send feeding command for schedule: ${schedule.id}, error: ${error.message}", error) }
            )
        }
    }

    private fun handleSaveSuccess(schedule: ScheduleModel, message: String) {
        logScheduleData(schedule)
        clearForm()
        showSuccessMessage(schedule, message)
        setResult(RESULT_OK)
        finish()
    }

    private fun handleSaveError(error: String) {
        Log.e(TAG, "Save error: $error")
        showToast(error)
    }

    private fun logScheduleData(schedule: ScheduleModel) {
        Log.d(TAG, """
            === JADWAL TERSIMPAN ===
            ID: ${schedule.id}
            Nama: ${schedule.namaJadwal}
            Jenis: ${schedule.jenisMakanan}
            Tanggal & Waktu: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(schedule.waktu)}
            User ID: ${schedule.userId}
            ====================
        """.trimIndent())
    }

    private fun showSuccessMessage(schedule: ScheduleModel, message: String) {
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        showToast("""
            $message
            ${schedule.namaJadwal}
            ${schedule.jenisMakanan}
            ${dateTimeFormat.format(schedule.waktu)}
            Smart Feeder akan otomatis memberikan makan sesuai jadwal.
        """.trimIndent())
    }

    private fun clearForm() {
        try {
            etNamaJadwal.setText("")
            spinnerMakananMinuman.setSelection(0)

            // Reset to current date
            selectedDate.time = Date()
            etTanggal.setText(dateFormat.format(selectedDate.time))

            // Reset time to 10 minutes from now
            selectedTime.time = Date()
            selectedTime.add(Calendar.MINUTE, 10)
            tvWaktuDisplay.text = timeFormat.format(selectedTime.time)

            etNamaJadwal.error = null
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing form: ${e.message}", e)
            showToast("Error clearing form")
        }
    }

    private fun handleBackButton() {
        try {
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
            finish()
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating back: ${e.message}", e)
            showToast("Error navigating back")
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showProgressDialog(message: String) {
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    private fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgressDialog()
    }
}