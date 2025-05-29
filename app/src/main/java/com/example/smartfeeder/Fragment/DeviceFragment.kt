package com.example.smartfeeder.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.smartfeeder.R

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class DeviceFragment : Fragment() {

    // Views
    private lateinit var etSearchDevice: TextInputEditText
    private lateinit var btnScanWifi: MaterialButton
    private lateinit var btnRefresh: MaterialButton
    private lateinit var cardStatus: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

    private lateinit var layoutEmptyState: LinearLayout

    // Handler untuk simulasi scanning
    private val handler = Handler(Looper.getMainLooper())
    private var isScanning = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()
        setupSearchListener()
    }

    private fun initViews(view: View) {
        etSearchDevice = view.findViewById(R.id.etSearchDevice)
        btnScanWifi = view.findViewById(R.id.btnScanWifi)
        btnRefresh = view.findViewById(R.id.btnRefresh)
        cardStatus = view.findViewById(R.id.cardStatus)
        progressBar = view.findViewById(R.id.progressBar)
        tvStatus = view.findViewById(R.id.tvStatus)

        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
    }

    private fun setupListeners() {
        btnScanWifi.setOnClickListener {
            if (!isScanning) {
                startWifiScan()
            }
        }

        btnRefresh.setOnClickListener {
            refreshDevices()
        }
    }

    private fun setupSearchListener() {
        etSearchDevice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter devices berdasarkan input pencarian
                filterDevices(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun startWifiScan() {
        isScanning = true
        showScanningState()

        // Simulasi scanning WiFi (ganti dengan implementasi WiFi scanning yang sesungguhnya)
        handler.postDelayed({
            completeScan()
        }, 3000) // 3 detik scanning
    }

    private fun showScanningState() {
        // Animate button
        val scaleAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        btnScanWifi.startAnimation(scaleAnim)

        // Update UI
        btnScanWifi.text = "Scanning..."
        btnScanWifi.isEnabled = false

        // Show status card
        cardStatus.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        tvStatus.text = "Mencari Smart Feeder di jaringan WiFi..."

        // Hide empty state
        layoutEmptyState.visibility = View.GONE

        // Animate status card appearance
        val slideDown = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        cardStatus.startAnimation(slideDown)
    }

    private fun completeScan() {
        isScanning = false

        // Update status
        tvStatus.text = "Pencarian selesai"
        progressBar.visibility = View.GONE

        // Reset button
        btnScanWifi.text = "Scan WiFi Smart Feeder"
        btnScanWifi.isEnabled = true

        // Hide status after delay
        handler.postDelayed({
            hideStatusCard()
        }, 1500)

        // Show empty state (karena tidak ada device yang ditampilkan)
        showEmptyState()
    }

    private fun hideStatusCard() {
        val slideUp = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)
        cardStatus.startAnimation(slideUp)

        handler.postDelayed({
            cardStatus.visibility = View.GONE
        }, 300)
    }

    private fun showEmptyState() {
        layoutEmptyState.visibility = View.VISIBLE
        val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        layoutEmptyState.startAnimation(fadeIn)
    }

    private fun refreshDevices() {
        // Animate refresh button
        val rotateAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        btnRefresh.startAnimation(rotateAnim)

        // Clear search
        etSearchDevice.text?.clear()

        // Show toast
        Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()

        // Reset to empty state
        showEmptyState()
    }

    private fun filterDevices(query: String) {
        // Implementasi filter akan ditambahkan ketika ada data device
        // Untuk saat ini hanya menampilkan toast
        if (query.isNotEmpty()) {
            Toast.makeText(context, "Searching for: $query", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}