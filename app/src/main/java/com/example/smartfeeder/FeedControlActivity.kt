package com.example.smartfeeder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class FeedControlActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    // UI Components
    private lateinit var etNamaJadwal: EditText
    private lateinit var spinnerMakananMinuman: Spinner
    private lateinit var etTanggal: EditText
    private lateinit var tvWaktuDisplay: TextView
    private lateinit var btnSimpan: Button
    private lateinit var btnKembali: Button

    // Data
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_control)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Setup transparent status bar (if needed)
        setupTransparentStatusBar()

        // Initialize UI components
        initializeViews()

        // Setup dropdown
        setupMakananMinumanSpinner()

        // Setup date picker
        setupDatePicker()

        // Setup time picker
        setupTimePicker()

        // Setup buttons
        setupButtons()

        // Set up back button
        findViewById<View>(R.id.btn_back).setOnClickListener {
            handleBackButton()
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
            spinnerMakananMinuman = findViewById(R.id.spinner_makanan_minuman)
            etTanggal = findViewById(R.id.et_tanggal)
            tvWaktuDisplay = findViewById(R.id.tv_waktu_display)
            btnSimpan = findViewById(R.id.btn_simpan)
            btnKembali = findViewById(R.id.btn_kembali)
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing views: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun setupMakananMinumanSpinner() {
        // Data untuk dropdown
        val makananMinumanOptions = arrayOf(
            "Pilih Jenis",
            "Makanan",
            "Minuman"
        )

        // Create adapter
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            makananMinumanOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set adapter to spinner
        spinnerMakananMinuman.adapter = adapter

        // Set listener for spinner selection
        spinnerMakananMinuman.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = makananMinumanOptions[position]
                if (position > 0) { // Skip first item "Pilih Jenis"
                    // Optional: Remove toast or make it less intrusive
                    // Toast.makeText(this@FeedControlActivity, "Dipilih: $selectedItem", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupDatePicker() {
        // Set initial date display
        etTanggal.setText(dateFormat.format(selectedDate.time))

        // Make EditText non-editable but clickable
        etTanggal.isFocusable = false
        etTanggal.isClickable = true

        // Set click listener for date picker
        etTanggal.setOnClickListener {
            showDatePicker()
        }

        // Also handle calendar icon click (with error handling)
        try {
            findViewById<View>(R.id.ic_calendar)?.setOnClickListener {
                showDatePicker()
            }
        } catch (e: Exception) {
            // Calendar icon might not exist in layout, that's okay
            println("Calendar icon not found: ${e.message}")
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Update display
                etTanggal.setText(dateFormat.format(selectedDate.time))
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )

        // Set minimum date to today
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun setupTimePicker() {
        // Set initial time display
        selectedTime.set(Calendar.HOUR_OF_DAY, 9)
        selectedTime.set(Calendar.MINUTE, 30)
        tvWaktuDisplay.text = timeFormat.format(selectedTime.time)

        // Set click listener for time picker
        tvWaktuDisplay.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)

                // Update display
                tvWaktuDisplay.text = timeFormat.format(selectedTime.time)
            },
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            true // 24 hour format
        )

        timePickerDialog.show()
    }

    private fun setupButtons() {
        // Kembali button
        btnKembali.setOnClickListener {
            handleBackButton()
        }

        // Simpan button
        btnSimpan.setOnClickListener {
            handleSimpanButton()
        }
    }

    private fun handleSimpanButton() {
        // Get form data
        val namaJadwal = etNamaJadwal.text.toString().trim()
        val selectedSpinnerPosition = spinnerMakananMinuman.selectedItemPosition
        val tanggal = etTanggal.text.toString()
        val waktu = tvWaktuDisplay.text.toString()

        // Validation
        if (namaJadwal.isEmpty()) {
            etNamaJadwal.error = "Nama jadwal harus diisi"
            etNamaJadwal.requestFocus()
            return
        }

        if (selectedSpinnerPosition == 0) {
            Toast.makeText(this, "Pilih jenis makanan atau minuman", Toast.LENGTH_SHORT).show()
            return
        }

        // Get selected spinner value
        val jenisMakanan = spinnerMakananMinuman.selectedItem.toString()

        // Here you can save the data to Firebase or local database
        saveScheduleData(namaJadwal, jenisMakanan, tanggal, waktu)
    }

    private fun saveScheduleData(namaJadwal: String, jenis: String, tanggal: String, waktu: String) {
        try {
            // Show loading
            Toast.makeText(this, "Menyimpan jadwal...", Toast.LENGTH_SHORT).show()

            // TODO: Implement save to Firebase
            // For now, just show success message
            Toast.makeText(this, "Jadwal berhasil disimpan!", Toast.LENGTH_LONG).show()

            // Log the data for debugging
            println("=== JADWAL TERSIMPAN ===")
            println("Nama: $namaJadwal")
            println("Jenis: $jenis")
            println("Tanggal: $tanggal")
            println("Waktu: $waktu")
            println("Timestamp: ${System.currentTimeMillis()}")

            // Clear form after successful save
            clearForm()

        } catch (e: Exception) {
            Toast.makeText(this, "Error menyimpan jadwal: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun clearForm() {
        try {
            etNamaJadwal.setText("")
            spinnerMakananMinuman.setSelection(0)

            // Reset to current date and default time
            selectedDate = Calendar.getInstance()
            selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, 9)
            selectedTime.set(Calendar.MINUTE, 30)

            etTanggal.setText(dateFormat.format(selectedDate.time))
            tvWaktuDisplay.text = timeFormat.format(selectedTime.time)

        } catch (e: Exception) {
            Toast.makeText(this, "Error clearing form: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun handleBackButton() {
        try {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error navigating back: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Helper method to get selected data
    fun getScheduleData(): Map<String, Any> {
        return try {
            mapOf(
                "namaJadwal" to etNamaJadwal.text.toString(),
                "jenis" to spinnerMakananMinuman.selectedItem.toString(),
                "tanggal" to etTanggal.text.toString(),
                "waktu" to tvWaktuDisplay.text.toString(),
                "timestamp" to System.currentTimeMillis()
            )
        } catch (e: Exception) {
            println("Error getting schedule data: ${e.message}")
            emptyMap()
        }
    }
}