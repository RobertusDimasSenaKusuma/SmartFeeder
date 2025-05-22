package com.example.smartfeeder.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartfeeder.R

class DeviceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device, container, false) // Buat layout fragment_device.xml
    }

    // Tidak perlu onViewCreated untuk mengatur ActionBar di sini
    // karena MainActivity sudah menanganinya di listener BottomNavigationView
}