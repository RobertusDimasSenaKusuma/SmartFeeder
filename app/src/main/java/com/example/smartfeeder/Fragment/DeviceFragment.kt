package com.example.smartfeeder.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartfeeder.R
import com.example.smartfeeder.Adapter.DeviceAdapter
import com.example.smartfeeder.models.SmartFeederDevice
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
    private lateinit var recyclerViewDevices: RecyclerView

    // Data
    private val deviceList = mutableListOf<SmartFeederDevice>()
    private val filteredDeviceList = mutableListOf<SmartFeederDevice>()
    private lateinit var deviceAdapter: DeviceAdapter

    // Handler untuk simulasi scanning
    private val handler = Handler(Looper.getMainLooper())
    private var isScanning = false

    // Flag untuk menggunakan dummy data
    private val useDummyData = true // Set ke false untuk menggunakan Firebase

    companion object {
        private const val TAG = "DeviceFragment"
    }

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
        setupRecyclerView()
        setupListeners()
        setupSearchListener()

        if (useDummyData) {
            loadDummyData()
        } else {
            // startListeningToDevices() // Uncomment untuk Firebase
        }
    }

    private fun initViews(view: View) {
        etSearchDevice = view.findViewById(R.id.etSearchDevice)
        btnScanWifi = view.findViewById(R.id.btnScanWifi)
        btnRefresh = view.findViewById(R.id.btnRefresh)
        cardStatus = view.findViewById(R.id.cardStatus)
        progressBar = view.findViewById(R.id.progressBar)
        tvStatus = view.findViewById(R.id.tvStatus)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        recyclerViewDevices = view.findViewById(R.id.recyclerViewDevices)
    }

    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter(filteredDeviceList) { device ->
            onDeviceClick(device)
        }
        recyclerViewDevices.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceAdapter
        }
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
                filterDevices(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadDummyData() {
        // Adapter sudah memiliki dummy data built-in
        deviceList.clear()
        deviceList.addAll(deviceAdapter.getDevices())
        filterDevices(etSearchDevice.text.toString())
        updateUI()
        Log.d(TAG, "Loaded ${deviceList.size} dummy devices")
    }

    private fun startWifiScan() {
        isScanning = true
        showScanningState()

        // Simulasi scan WiFi dengan menambah dummy device baru
        handler.postDelayed({
            if (useDummyData) {
                // Simulasi menemukan device baru
                deviceAdapter.addDummyDevice()
                deviceList.clear()
                deviceList.addAll(deviceAdapter.getDevices())
                filterDevices(etSearchDevice.text.toString())
            }
            completeScan()
        }, 3000) // 3 detik scanning
    }

    private fun showScanningState() {
        val scaleAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        btnScanWifi.startAnimation(scaleAnim)

        btnScanWifi.text = "Scanning..."
        btnScanWifi.isEnabled = false

        cardStatus.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        tvStatus.text = "Mencari Smart Feeder di jaringan WiFi..."

        val slideDown = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        cardStatus.startAnimation(slideDown)
    }

    private fun completeScan() {
        isScanning = false

        tvStatus.text = "Pencarian selesai - Ditemukan ${deviceList.size} device"
        progressBar.visibility = View.GONE

        btnScanWifi.text = "Scan WiFi"
        btnScanWifi.isEnabled = true

        handler.postDelayed({
            hideStatusCard()
        }, 2000)
    }

    private fun hideStatusCard() {
        val slideUp = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right)
        cardStatus.startAnimation(slideUp)

        handler.postDelayed({
            cardStatus.visibility = View.GONE
        }, 300)
    }

    private fun refreshDevices() {
        val rotateAnim = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
        btnRefresh.startAnimation(rotateAnim)

        etSearchDevice.text?.clear()

        if (useDummyData) {
            // Refresh dummy data
            deviceAdapter.refreshDummyData()
            deviceList.clear()
            deviceList.addAll(deviceAdapter.getDevices())
            filterDevices("")
            updateUI()
        } else {
            // startListeningToDevices() // Uncomment untuk Firebase
        }

        Toast.makeText(context, "Refreshing devices...", Toast.LENGTH_SHORT).show()
    }

    private fun filterDevices(query: String) {
        filteredDeviceList.clear()

        if (query.isEmpty()) {
            filteredDeviceList.addAll(deviceList)
        } else {
            val lowercaseQuery = query.lowercase()
            filteredDeviceList.addAll(
                deviceList.filter {
                    it.name.lowercase().contains(lowercaseQuery) ||
                            it.wifiSSID.lowercase().contains(lowercaseQuery) ||
                            it.ipAddress.contains(query)
                }
            )
        }

        deviceAdapter.notifyDataSetChanged()
        updateUI()
    }

    private fun updateUI() {
        if (filteredDeviceList.isEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
        }
    }

    private fun showEmptyState() {
        layoutEmptyState.visibility = View.VISIBLE
        recyclerViewDevices.visibility = View.GONE

        val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        layoutEmptyState.startAnimation(fadeIn)
    }

    private fun hideEmptyState() {
        layoutEmptyState.visibility = View.GONE
        recyclerViewDevices.visibility = View.VISIBLE
    }

    private fun onDeviceClick(device: SmartFeederDevice) {
        Toast.makeText(context, "Connecting to ${device.name}...", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Device clicked: ${device.name} (${device.id})")

        // Simulasi koneksi berhasil
        handler.postDelayed({
            Toast.makeText(context, "Connected to ${device.name}!", Toast.LENGTH_SHORT).show()
        }, 1500)
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Log.e(TAG, message)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}